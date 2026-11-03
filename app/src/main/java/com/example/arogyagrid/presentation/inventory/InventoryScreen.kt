package com.example.arogyagrid.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.R
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.domain.model.InventoryItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.GovtNavy
import com.example.arogyagrid.presentation.theme.GovtNavyLight
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusOrangeBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.theme.TirangaGreen
import com.example.arogyagrid.presentation.theme.TirangaSaffron
import com.example.arogyagrid.presentation.theme.TirangaSaffronLight
import com.example.arogyagrid.presentation.viewmodel.InventoryViewModel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.QrCodeScanner
import com.example.arogyagrid.presentation.scanner.BarcodeScannerDialog

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showTransactionDialog by remember { mutableStateOf<InventoryItem?>(null) }
    var showScannerDialog by remember { mutableStateOf(false) }
    var transactionType by remember { mutableStateOf("RECEIPT") }
    var quantityText by remember { mutableStateOf("50") }
    var successToast by remember { mutableStateOf<String?>(null) }

    val filterOptions = listOf("All", "Vital", "Critical Shortage", "Surplus")

    val filteredItems = uiState.items.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
            item.medicineName.contains(searchQuery, ignoreCase = true) ||
            item.facilityName.contains(searchQuery, ignoreCase = true) ||
            (item.batchNumber?.contains(searchQuery, ignoreCase = true) == true)

        val matchesFilter = when (selectedFilter) {
            "Vital" -> item.criticality.equals("VITAL", ignoreCase = true)
            "Critical Shortage" -> item.coverageDays <= 5
            "Surplus" -> item.stockGap == 0
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SyntheticDataBanner()

        CommandTopBar(
            roleLabel = UserSession.userRole ?: "Pharmacist",
            districtName = UserSession.userDistrict ?: "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = { viewModel.loadInventory() }
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // ── 1. Official RMSCL e-Aushadhi Central Warehouse Picture Banner ──
            Card(
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color(0xFF002244), Color(0xFF003366), Color(0xFF1E3A8A))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "RMSCL e-AUSHADHI • राजस्थान सरकार",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "जिला केंद्रीय औषधि भंडार व लॉजिस्टिक्स",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "आवश्यक औषधि सूची (EDL) बैच इन्वेंटरी, क्यूआर बारकोड प्रमाणीकरण व दैनिक खपत निगरानी",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.88f),
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Warehouse Hero Illustration Picture
                        Box(
                            modifier = Modifier
                                .size(width = 96.dp, height = 76.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner_eaushadhi_warehouse),
                                contentDescription = "RMSCL Warehouse Picture",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // ── 2. Action Row: Barcode Scanner & Filter Chips ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filterOptions) { filter ->
                        val labelText = when (filter) {
                            "Vital" -> "अति-आवश्यक (Vital)"
                            "Critical Shortage" -> "स्टॉक संकट"
                            "Surplus" -> "अधिशेष"
                            else -> "सभी (All)"
                        }
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(4.dp),
                            label = { Text(labelText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovtNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // CameraX Barcode Scanner Launch Button
                Button(
                    onClick = { showScannerDialog = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GovtNavy),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("क्यूआर स्कैन", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (successToast != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = StatusGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(successToast ?: "", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GovtNavy)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading inventory", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadInventory() },
                            shape = RoundedCornerShape(4.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GovtNavy)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredItems) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            shape = RoundedCornerShape(4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(2.dp),
                                                color = GovtNavyLight
                                            ) {
                                                Text(
                                                    text = "EDL-${(item.medicineId.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GovtNavy,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.medicineName,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GovtNavy
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${item.facilityName} • बैच क्र: ${item.batchNumber ?: "BATCH-JPR-2026"}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = item.criticality)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Government Stock Ledger Triple Column
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("स्टॉक शेष (CLOSING)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                            Text("${item.closingStock} यूनिट", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                        Column {
                                            Text("दैनिक खपत (DAILY)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                            Text("${item.avgDailyConsumption}/दिन", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("कवरेज अवधि (COVERAGE)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                            Text(
                                                "${item.coverageDays} दिन",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.coverageDays <= 5) StatusRed else StatusGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "न्यूनतम मानक: ${item.minimumStock} | अधिकतम: ${item.maximumStock} | अंतर: ${item.stockGap}",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "RMSCL e-Aushadhi प्रमाणित",
                                        fontSize = 10.sp,
                                        color = TirangaGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            showTransactionDialog = item
                                            transactionType = "RECEIPT"
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GovtNavy),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("स्टॉक आवक (Receive)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            showTransactionDialog = item
                                            transactionType = "ISSUE"
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GovtNavy),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("स्टॉक निर्गम (Issue)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GovtNavy)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Transaction Recording Dialog
        if (showTransactionDialog != null) {
            val item = showTransactionDialog!!
            AlertDialog(
                onDismissRequest = { showTransactionDialog = null },
                title = { Text("Record Stock $transactionType") },
                text = {
                    Column {
                        Text("Medicine: ${item.medicineName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Facility: ${item.facilityName}", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("Quantity Units") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qty = quantityText.toIntOrNull() ?: 50
                            viewModel.recordTransaction(
                                facilityId = item.facilityId,
                                medicineId = item.medicineId,
                                type = transactionType,
                                quantity = qty
                            )
                            showTransactionDialog = null
                            successToast = "Successfully recorded $transactionType of $qty units for ${item.medicineName}"
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTransactionDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // CameraX Barcode & QR Scanner Dialog
        if (showScannerDialog) {
            BarcodeScannerDialog(
                onDismiss = { showScannerDialog = false },
                onBarcodeScanned = { scanned ->
                    showScannerDialog = false
                    val matchedItem = uiState.items.find {
                        (scanned.medicineId != null && it.medicineId.equals(scanned.medicineId, ignoreCase = true)) ||
                        it.medicineName.contains(scanned.rawValue, ignoreCase = true)
                    } ?: uiState.items.firstOrNull()

                    if (matchedItem != null) {
                        quantityText = scanned.quantity?.toString() ?: "100"
                        transactionType = "RECEIPT"
                        showTransactionDialog = matchedItem
                        successToast = "Barcode scanned! Auto-selected ${matchedItem.medicineName} (${scanned.batchNumber ?: "Batch"})"
                    } else {
                        successToast = "Scanned barcode: ${scanned.rawValue}"
                    }
                }
            )
        }
    }
}
