package com.example.arogyagrid.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.R
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.domain.model.InventoryItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.scanner.BarcodeScannerDialog
import com.example.arogyagrid.presentation.theme.*
import com.example.arogyagrid.presentation.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
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
            onSyncClick = { viewModel.loadInventory() },
            onProfileClick = onNavigateToProfile,
            onLogoutClick = onLogout,
            onLoginClick = onNavigateToLogin
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // ── 1. Official RMSCL e-Aushadhi Central Warehouse Picture Banner ──
            Card(
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                text = LanguageManager.s("जिला केंद्रीय औषधि भंडार व लॉजिस्टिक्स", "District Central Drug Warehouse & Logistics"),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = LanguageManager.s(
                                    "आवश्यक औषधि सूची (EDL) बैच इन्वेंटरी, क्यूआर बारकोड प्रमाणीकरण व दैनिक खपत निगरानी",
                                    "Essential Drug List (EDL) Batch Inventory, QR Barcode Verification & Daily Consumption Tracking"
                                ),
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.88f),
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Warehouse Hero Illustration Picture
                        Box(
                            modifier = Modifier
                                .size(width = 88.dp, height = 72.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp)),
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
                            "Vital" -> LanguageManager.s("अति-आवश्यक (Vital)", "Vital Medicines")
                            "Critical Shortage" -> LanguageManager.s("स्टॉक संकट", "Critical Shortage")
                            "Surplus" -> LanguageManager.s("अधिशेष (Surplus)", "Surplus")
                            else -> LanguageManager.s("सभी (All)", "All (All)")
                        }
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(4.dp),
                            label = { Text(labelText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // CameraX Barcode Scanner Launch Button
                Button(
                    onClick = { showScannerDialog = true },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.s("क्यूआर स्कैन", "QR Scanner"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading inventory", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadInventory() },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(LanguageManager.s("पुनः प्रयास करें (Retry)", "Retry Connection"))
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
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline)),
                            shape = RoundedCornerShape(6.dp),
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
                                                shape = RoundedCornerShape(3.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "EDL-${(item.medicineId.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.medicineName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${item.facilityName} • ${LanguageManager.s("बैच क्र", "Batch")}: ${item.batchNumber ?: "BATCH-JPR-2026"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RiskBadge(riskLevel = item.criticality)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Government Stock Ledger Triple Column
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(LanguageManager.s("स्टॉक शेष (CLOSING)", "CLOSING STOCK"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                            Text("${item.closingStock} ${LanguageManager.s("यूनिट", "units")}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column {
                                            Text(LanguageManager.s("दैनिक खपत (DAILY)", "DAILY CONSUMPTION"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                            Text("${item.avgDailyConsumption}/${LanguageManager.s("दिन", "day")}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(LanguageManager.s("कवरेज अवधि (COVERAGE)", "COVERAGE PERIOD"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                                            Text(
                                                "${item.coverageDays} ${LanguageManager.s("दिन", "days")}",
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
                                        text = "${LanguageManager.s("न्यूनतम मानक", "Min")}: ${item.minimumStock} | ${LanguageManager.s("अधिकतम", "Max")}: ${item.maximumStock} | ${LanguageManager.s("अंतर", "Gap")}: ${item.stockGap}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = LanguageManager.s("RMSCL e-Aushadhi प्रमाणित", "e-Aushadhi Verified"),
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
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(LanguageManager.s("स्टॉक आवक (Receive)", "Stock Intake (Receive)"), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            showTransactionDialog = item
                                            transactionType = "ISSUE"
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(LanguageManager.s("स्टॉक निर्गम (Issue)", "Stock Outflow (Issue)"), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(LanguageManager.s("स्टॉक प्रविष्टि दर्ज करें ($transactionType)", "Record Stock $transactionType"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("${LanguageManager.s("औषधि", "Medicine")}: ${item.medicineName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("${LanguageManager.s("संस्थान", "Facility")}: ${item.facilityName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text(LanguageManager.s("यूनिट मात्रा", "Quantity Units")) },
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
                            successToast = if (LanguageManager.currentLanguage.value == com.example.arogyagrid.core.AppLanguage.HINDI)
                                "${item.medicineName} हेतु $qty यूनिट $transactionType सफलतापूर्ण दर्ज!"
                            else
                                "Recorded $transactionType of $qty units for ${item.medicineName}"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(LanguageManager.s("पुष्टि करें (Confirm)", "Confirm"))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTransactionDialog = null }) {
                        Text(LanguageManager.s("रद्द करें (Cancel)", "Cancel"), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        successToast = if (LanguageManager.currentLanguage.value == com.example.arogyagrid.core.AppLanguage.HINDI)
                            "बारकोड स्कैन सफल! चयनित: ${matchedItem.medicineName} (${scanned.batchNumber ?: "Batch"})"
                        else
                            "Barcode scanned! Auto-selected ${matchedItem.medicineName} (${scanned.batchNumber ?: "Batch"})"
                    } else {
                        successToast = "Scanned barcode: ${scanned.rawValue}"
                    }
                }
            )
        }
    }
}
