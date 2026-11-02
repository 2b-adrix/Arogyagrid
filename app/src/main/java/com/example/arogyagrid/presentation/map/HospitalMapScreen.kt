package com.example.arogyagrid.presentation.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
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

    remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        true
    }

    val hospitals = listOf(
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
                Column {
                    Text(text = "Jaipur Health Grid", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(text = "Interactive OpenStreetMap & Routing", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(26.9124, 75.7873))
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    hospitals.forEach { h ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(h.latitude, h.longitude)
                        marker.title = h.name
                        marker.snippet = "${h.type} • ${h.status}"
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(text = "Hospitals by Proximity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            items(hospitals.sortedBy { it.distanceKm }) { hospital ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
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
                        Button(
                            onClick = {
                                val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}")
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                try { context.startActivity(intent) } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Navigate via Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
