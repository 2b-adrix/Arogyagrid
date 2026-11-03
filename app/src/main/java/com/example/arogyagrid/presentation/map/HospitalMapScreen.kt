package com.example.arogyagrid.presentation.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.presentation.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MapHospital(
    val id: String,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val status: String,
    val isCritical: Boolean,
    val availableStock: Int? = null,
    val contactPhone: String? = null
)

data class MedicineOption(
    val id: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalMapScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Configure Osmdroid User-Agent to avoid OSM tile server rate-limiting
    remember {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = "ArogyaGridHealthcareApp/1.0 (Android; contact@arogyagrid.in)"
        }
        true
    }

    // 1. Humanitarian OpenStreetMap (HOT) - 100% Free, NO API Key Required
    val osmHotTileSource = remember {
        XYTileSource(
            "OpenStreetMapHOT",
            0, 19, 256, ".png",
            arrayOf(
                "https://a.tile.openstreetmap.fr/hot/",
                "https://b.tile.openstreetmap.fr/hot/",
                "https://c.tile.openstreetmap.fr/hot/"
            ),
            "© OpenStreetMap contributors, Tiles style by Humanitarian OpenStreetMap Team hosted by OpenStreetMap France",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    // 2. Official OpenStreetMap Standard Tiles - 100% Free, NO API Key Required
    val osmStandardTileSource = remember {
        XYTileSource(
            "OpenStreetMapStandard",
            0, 19, 256, ".png",
            arrayOf("https://tile.openstreetmap.org/"),
            "© OpenStreetMap contributors",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    // 3. OpenTopoMap - 100% Free OpenStreetMap with terrain & elevation contours, NO API Key Required
    val osmTopoTileSource = remember {
        XYTileSource(
            "OpenTopoMap",
            0, 17, 256, ".png",
            arrayOf(
                "https://a.tile.opentopomap.org/",
                "https://b.tile.opentopomap.org/",
                "https://c.tile.opentopomap.org/"
            ),
            "Kartendaten: © OpenStreetMap-Mitwirkende, SRTM | Kartendarstellung: © OpenTopoMap (CC-BY-SA)",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    var selectedTileSourceIndex by remember { mutableIntStateOf(0) }
    var selectedHospitalId by remember { mutableStateOf<String?>("FAC-JPR-001") }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    // Live Database State
    var hospitals by remember { mutableStateOf<List<MapHospital>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Medicine filter list for Haversine Geo-Proximity Routing
    val medicineOptions = remember {
        listOf(
            MedicineOption("ALL", "All Registered Facilities"),
            MedicineOption("MED-202", "ORS Sachets (Emergency)"),
            MedicineOption("MED-101", "Amoxicillin 500mg"),
            MedicineOption("MED-201", "Oxygen Cylinder (D-Type)"),
            MedicineOption("MED-104", "Ceftriaxone 1g Inj"),
            MedicineOption("MED-301", "Metformin 500mg"),
            MedicineOption("MED-401", "Paracetamol Syrup 125mg")
        )
    }
    var selectedMedicineIndex by remember { mutableIntStateOf(0) }
    var medicineDropdownExpanded by remember { mutableStateOf(false) }

    // Current origin hospital for distance routing (default: PHC Amer FAC-JPR-011)
    val originFacilityId = remember { "FAC-JPR-011" } // PHC Amer

    // Fetch live hospital dataset from Neon PostgreSQL Backend
    fun fetchLiveHospitals(medId: String) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                if (medId == "ALL") {
                    // Fetch all facilities live from Spring Boot backend
                    val res = withContext(Dispatchers.IO) { NetworkClient.apiService.getFacilities("Jaipur") }
                    if (res.isSuccessful && res.body()?.data != null) {
                        val facList = res.body()!!.data!!
                        // Calculate Haversine distance from origin (PHC Amer)
                        val originLat = 26.9855
                        val originLon = 75.8513
                        hospitals = facList.map { f ->
                            val lat = f.latitude ?: 26.8924
                            val lon = f.longitude ?: 75.8173
                            val dist = calculateHaversine(originLat, originLon, lat, lon)
                            MapHospital(
                                id = f.facilityId,
                                name = f.name,
                                type = f.type.replace("_", " "),
                                latitude = lat,
                                longitude = lon,
                                distanceKm = Math.round(dist * 10.0) / 10.0,
                                status = if (f.facilityId == "FAC-JPR-001" || f.facilityId == "FAC-JPR-005") "Surplus Stock Hub" else "Active Facility",
                                isCritical = f.facilityId == "FAC-JPR-011" || f.facilityId == "FAC-JPR-006",
                                contactPhone = f.contactPhone
                            )
                        }.sortedBy { it.distanceKm }
                    } else {
                        errorMessage = "Backend connection failed. Status: ${res.code()}"
                    }
                } else {
                    // Fetch real-time nearest surplus facilities for this exact medicine
                    val res = withContext(Dispatchers.IO) {
                        NetworkClient.apiService.getNearestSurplus(originFacilityId, medId)
                    }
                    if (res.isSuccessful && res.body()?.data != null) {
                        val nearestList = res.body()!!.data!!
                        hospitals = nearestList.map { n ->
                            MapHospital(
                                id = n.facilityId,
                                name = n.facilityName,
                                type = "Candidate Surplus Hub",
                                latitude = n.latitude,
                                longitude = n.longitude,
                                distanceKm = n.distanceKm,
                                status = "${n.availableStock} Units Available",
                                isCritical = false,
                                availableStock = n.availableStock,
                                contactPhone = n.contactPhone
                            )
                        }
                    } else {
                        errorMessage = "No surplus facilities returned: ${res.code()}"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Network error"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedMedicineIndex) {
        val selectedMed = medicineOptions[selectedMedicineIndex]
        fetchLiveHospitals(selectedMed.id)
    }

    DisposableEffect(Unit) {
        onDispose {
            mapViewInstance?.onPause()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Jaipur Supply Routing", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(
                            text = if (isLoading) "Syncing with Cloud Neon DB..." else "Origin: PHC Amer • ${hospitals.size} Facilities Live",
                            fontSize = 11.sp,
                            color = PrimaryTeal
                        )
                    }
                    FilterChip(
                        selected = true,
                        onClick = {
                            selectedTileSourceIndex = (selectedTileSourceIndex + 1) % 3
                            val targetSource = when (selectedTileSourceIndex) {
                                0 -> osmHotTileSource
                                1 -> osmStandardTileSource
                                else -> osmTopoTileSource
                            }
                            mapViewInstance?.let { mv ->
                                mv.setTileSource(targetSource)
                                mv.invalidate()
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        label = {
                            Text(
                                when (selectedTileSourceIndex) {
                                    0 -> "HOT"
                                    1 -> "Standard"
                                    else -> "Topo"
                                },
                                fontSize = 10.sp
                            )
                        }
                    )
                }

                // Filter Selector for Supply Item / Medicine
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { medicineDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Item: ${medicineOptions[selectedMedicineIndex].name}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        DropdownMenu(
                            expanded = medicineDropdownExpanded,
                            onDismissRequest = { medicineDropdownExpanded = false }
                        ) {
                            medicineOptions.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    text = { Text(option.name, fontSize = 13.sp) },
                                    onClick = {
                                        selectedMedicineIndex = index
                                        medicineDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { fetchLiveHospitals(medicineOptions[selectedMedicineIndex].id) }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PrimaryTeal)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AndroidView(
                factory = { ctx ->
                    val initialSource = when (selectedTileSourceIndex) {
                        0 -> osmHotTileSource
                        1 -> osmStandardTileSource
                        else -> osmTopoTileSource
                    }
                    val tileProvider = MapTileProviderBasic(ctx.applicationContext, initialSource)
                    MapView(ctx, tileProvider).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(26.9855, 75.8513)) // Centered at PHC Amer
                        mapViewInstance = this
                        onResume()
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    hospitals.forEach { h ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(h.latitude, h.longitude)
                        marker.title = h.name
                        marker.snippet = "${h.type} • ${h.status} • ${h.distanceKm} km"
                        marker.setOnMarkerClickListener { m, _ ->
                            selectedHospitalId = h.id
                            m.showInfoWindow()
                            true
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Map Quick Controls: Zoom In, Zoom Out, Center Origin Hospital (PHC Amer)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { mapViewInstance?.controller?.zoomIn() },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryTeal
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                }
                SmallFloatingActionButton(
                    onClick = { mapViewInstance?.controller?.zoomOut() },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryTeal
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                }
                SmallFloatingActionButton(
                    onClick = {
                        mapViewInstance?.controller?.animateTo(GeoPoint(26.9855, 75.8513))
                        mapViewInstance?.controller?.setZoom(13.0)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryTeal
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Center on PHC Amer", modifier = Modifier.size(18.dp))
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Failed to load live routing data", fontWeight = FontWeight.Bold, color = StatusRed)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = { fetchLiveHospitals(medicineOptions[selectedMedicineIndex].id) }) {
                        Text("Retry Connection")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedMedicineIndex == 0) "Facilities by Haversine Distance" else "Nearest Surplus Hubs for Supplies",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "${hospitals.size} live in DB",
                            fontSize = 11.sp,
                            color = PrimaryTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                items(hospitals) { hospital ->
                    val isSelected = selectedHospitalId == hospital.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (isSelected) PrimaryTeal else SlateBorder)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedHospitalId = hospital.id
                                mapViewInstance?.controller?.animateTo(GeoPoint(hospital.latitude, hospital.longitude))
                                mapViewInstance?.controller?.setZoom(14.5)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (hospital.isCritical) StatusRed.copy(alpha = 0.15f) else PrimaryTeal.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = if (hospital.isCritical) StatusRed else PrimaryTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    Column {
                                        Text(text = hospital.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(
                                            text = "${hospital.type} • ${hospital.distanceKm} km from PHC Amer",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hospital.isCritical) StatusRed.copy(alpha = 0.15f) else StatusGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = hospital.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hospital.isCritical) StatusRed else StatusGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (hospital.contactPhone != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Contact: ${hospital.contactPhone}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        selectedHospitalId = hospital.id
                                        mapViewInstance?.controller?.animateTo(GeoPoint(hospital.latitude, hospital.longitude))
                                        mapViewInstance?.controller?.setZoom(15.0)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Focus Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}")
                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Client-side Haversine distance calculator in km
private fun calculateHaversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}
