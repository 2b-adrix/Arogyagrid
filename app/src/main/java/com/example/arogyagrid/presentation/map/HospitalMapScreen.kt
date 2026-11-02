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
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
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
import com.example.arogyagrid.presentation.theme.*
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
    val isCritical: Boolean
)

@Composable
fun HospitalMapScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Set dedicated production User-Agent (avoids com.example.* blocklist on OpenStreetMap)
    remember {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = "ArogyaGridHealthcareApp/1.0 (Android; contact@arogyagrid.in)"
        }
        true
    }

    // 1. Humanitarian OpenStreetMap (HOT) - 100% Free, NO API Key Required, highlights hospitals & health centers
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

    var selectedTileSourceIndex by remember { mutableIntStateOf(0) } // 0 = HOT (Healthcare), 1 = Standard OSM, 2 = Topo
    var selectedHospitalId by remember { mutableStateOf<String?>("FAC-JPR-001") }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    val hospitals = remember {
        listOf(
            MapHospital("FAC-JPR-001", "Sawai ManSingh (SMS) Hospital, Jaipur", "Tertiary Hub", 26.8924, 75.8173, 0.0, "Surplus Stock Hub", false),
            MapHospital("FAC-JPR-002", "JK Lon Pediatric Hospital, Jaipur", "Pediatric Center", 26.8950, 75.8150, 0.4, "Sufficient Inventory", false),
            MapHospital("FAC-JPR-003", "Zanana Maternity Hospital, Jaipur", "Maternity Center", 26.9260, 75.8120, 3.8, "Moderate Stock", false),
            MapHospital("FAC-JPR-004", "Mahila Chikitsalaya, Jaipur", "Specialty Center", 26.9150, 75.8200, 2.6, "Normal Stock", false),
            MapHospital("FAC-JPR-005", "RUHS College of Medical Sciences, Jaipur", "Super-Specialty Hub", 26.8020, 75.8100, 10.2, "Surplus Hub", false),
            MapHospital("FAC-JPR-006", "CHC Sanganer, Jaipur", "Community Centre", 26.8267, 75.7885, 7.9, "Oxygen Low (Reorder)", true),
            MapHospital("FAC-JPR-007", "CHC Chomu, Jaipur", "Community Centre", 27.1661, 75.7217, 32.1, "Normal Stock", false),
            MapHospital("FAC-JPR-008", "CHC Amber, Jaipur", "Community Centre", 26.9880, 75.8550, 11.4, "Normal Stock", false),
            MapHospital("FAC-JPR-009", "CHC Kotputli, Jaipur", "Community Centre", 27.7020, 76.2010, 98.5, "Sufficient Inventory", false),
            MapHospital("FAC-JPR-010", "CHC Shahpura, Jaipur", "Community Centre", 27.3850, 75.9620, 56.2, "Moderate Stock", false),
            MapHospital("FAC-JPR-011", "PHC Amer, Jaipur", "Primary Health Centre", 26.9855, 75.8513, 11.2, "Critical ORS Shortage", true),
            MapHospital("FAC-JPR-012", "PHC Chaksu, Jaipur", "Primary Health Centre", 26.6058, 75.9471, 34.0, "Normal Stock", false),
            MapHospital("FAC-JPR-013", "PHC Phagi, Jaipur", "Primary Health Centre", 26.5868, 75.5741, 41.5, "Low Stock Warning", true),
            MapHospital("FAC-JPR-014", "PHC Bassi, Jaipur", "Primary Health Centre", 26.8340, 76.0420, 23.8, "Normal Stock", false),
            MapHospital("FAC-JPR-015", "PHC Jamwa Ramgarh, Jaipur", "Primary Health Centre", 27.0250, 76.0120, 24.3, "Normal Stock", false)
        )
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Jaipur Health Grid", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        text = when (selectedTileSourceIndex) {
                            0 -> "OSM Humanitarian (No API Key)"
                            1 -> "OSM Standard Tiles (No API Key)"
                            else -> "OSM Topo Terrain (No API Key)"
                        },
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
                                0 -> "Layer: HOT"
                                1 -> "Layer: Standard"
                                else -> "Layer: Topo"
                            },
                            fontSize = 10.sp
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
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
                        controller.setZoom(12.5)
                        controller.setCenter(GeoPoint(26.8924, 75.8173))
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
                        marker.snippet = "${h.type} • ${h.status}"
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

            // Map Quick Controls: Zoom In, Zoom Out, Center SMS Hospital
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
                        mapViewInstance?.controller?.animateTo(GeoPoint(26.8924, 75.8173))
                        mapViewInstance?.controller?.setZoom(13.0)
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryTeal
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Center on SMS Hospital", modifier = Modifier.size(18.dp))
                }
            }
        }

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
                    Text(text = "Hospitals by Proximity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Tap hospital to focus on map", fontSize = 11.sp, color = TextMuted)
                }
            }

            items(hospitals.sortedBy { it.distanceKm }) { hospital ->
                val isSelected = selectedHospitalId == hospital.id
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) PrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (isSelected) PrimaryTeal else SlateBorder)),
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
                                    .background(if (hospital.isCritical) StatusRed.copy(alpha = 0.15f) else PrimaryTeal.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = if (hospital.isCritical) StatusRed else PrimaryTeal, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                Column {
                                    Text(text = hospital.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(text = "${hospital.type} • ${hospital.distanceKm} km away", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = if (hospital.isCritical) StatusRed.copy(alpha = 0.15f) else StatusGreen.copy(alpha = 0.15f)) {
                                Text(text = hospital.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (hospital.isCritical) StatusRed else StatusGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
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
                                    try { context.startActivity(intent) } catch (_: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
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
