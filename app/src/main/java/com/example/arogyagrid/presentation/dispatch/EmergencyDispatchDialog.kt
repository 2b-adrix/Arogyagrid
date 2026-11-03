package com.example.arogyagrid.presentation.dispatch

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.arogyagrid.presentation.theme.*

data class DispatchManifest(
    val passNumber: String = "RJ-MED-TR-2026-089",
    val sourceHospital: String = "SMS Hospital, Jaipur (FAC-JPR-001)",
    val destinationHospital: String = "PHC Amer, Jaipur (FAC-JPR-011)",
    val vehicleNumber: String = "RJ-14-EA-4821 (Medical Van)",
    val driverName: String = "Rameshwar Gurjar (+91 98290 11234)",
    val items: List<String> = listOf(
        "500x ORS Sachets (MED-202) • Batch B-SMS-ORS-04",
        "100x Zinc Sulfate Tablets (MED-203) • Batch B-SMS-ZNC-01"
    ),
    val corridorAuthorization: String = "Jaipur Traffic Green Corridor #GC-82"
)

@Composable
fun EmergencyDispatchDialog(
    manifest: DispatchManifest = DispatchManifest(),
    onDismiss: () -> Unit,
    onConfirmDispatch: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(max = 620.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(PrimaryTeal.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Official Emergency Gate Pass", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Govt. of Rajasthan • Medical Supplies Dispatch", fontSize = 10.sp, color = PrimaryTeal)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Pass Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PASS #${manifest.passNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = PrimaryTeal
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StatusGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "GREEN CORRIDOR APPROVED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("DISPATCH DETAILS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Origin: ${manifest.sourceHospital}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text("• Destination: ${manifest.destinationHospital}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text("• Vehicle: ${manifest.vehicleNumber}", fontSize = 11.sp, color = TextDark)
                    Text("• Driver: ${manifest.driverName}", fontSize = 11.sp, color = TextDark)
                    Text("• Authorization: ${manifest.corridorAuthorization}", fontSize = 11.sp, color = PrimaryTeal)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("SUPPLY MANIFEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    manifest.items.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = item,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Checkpoint Verification Code Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR Pass", tint = PrimaryTeal, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Checkpoint Verification QR Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Scan with ArogyaGrid Scanner at receiving facility dock to auto-verify intake.", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareBody = """
                                *[AROGYAGRID EMERGENCY MEDICAL GATE PASS]*
                                Pass #: ${manifest.passNumber}
                                From: ${manifest.sourceHospital}
                                To: ${manifest.destinationHospital}
                                Vehicle: ${manifest.vehicleNumber}
                                Driver: ${manifest.driverName}
                                Corridor: ${manifest.corridorAuthorization}
                                
                                *SUPPLY MANIFEST:*
                                ${manifest.items.joinToString("\n")}
                                
                                Verified by Rajasthan State Health Logistics Hub.
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Dispatch Gate Pass"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Pass", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onConfirmDispatch()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm & Dispatch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
