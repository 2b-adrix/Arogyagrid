package com.example.arogyagrid.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.example.arogyagrid.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.domain.models.InventoryItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel
) {
    val items by viewModel.inventoryList.collectAsState()
    val message by viewModel.userMessage.collectAsState()

    InventoryScreenContent(
        items = items,
        message = message,
        onRecordTransaction = { medId, qty, type, batch, expiry ->
            viewModel.recordTransaction(medId, qty, type, batch, expiry)
        },
        onClearMessage = { viewModel.clearMessage() }
    )
}

@Composable
fun InventoryScreenContent(
    items: List<InventoryItem>,
    message: String?,
    onRecordTransaction: (String, Int, String, String?, String?) -> Unit,
    onClearMessage: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showReceiveDialog by remember { mutableStateOf<InventoryItem?>(null) }
    var receiveQty by remember { mutableStateOf("100") }
    var batchNum by remember { mutableStateOf("B001") }

    val filteredItems = items.filter {
        searchQuery.isBlank() ||
        it.medicineName.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SyntheticDataBanner()

        CommandTopBar(
            roleLabel = "Facility Manager",
            districtName = "Jaipur PHC 1",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = {}
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_inventory_box),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Batch Inventory & Transactions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Jaipur PHC 1 • Daily balances & transaction logging",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems, key = { "${it.facilityId}_${it.medicineId}" }) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().animateItem()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.medicineName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextDark
                                    )
                                    Text(
                                        text = "${item.category} • Batch B001",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                RiskBadge(riskLevel = item.riskLevel)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("CURRENT STOCK", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text("${item.closingStock} ${item.unit}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("STOCK COVERAGE", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text("${String.format(Locale.US, "%.1f", item.coverageDays)} days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showReceiveDialog = item },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Receive Stock", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        onRecordTransaction(
                                            item.medicineId,
                                            10,
                                            "ISSUE",
                                            null,
                                            null
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Issue 10 Units", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showReceiveDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showReceiveDialog = null },
            title = { Text("Receive Stock: ${item.medicineName}") },
            text = {
                Column {
                    OutlinedTextField(
                        value = receiveQty,
                        onValueChange = { receiveQty = it },
                        label = { Text("Quantity Received") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = batchNum,
                        onValueChange = { batchNum = it },
                        label = { Text("Batch Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = receiveQty.toIntOrNull() ?: 100
                        onRecordTransaction(
                            item.medicineId,
                            qty,
                            "RECEIVE",
                            batchNum,
                            "2027-12-31"
                        )
                        showReceiveDialog = null
                    }
                ) {
                    Text("Confirm Receipt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReceiveDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    message?.let { msg ->
        AlertDialog(
            onDismissRequest = { onClearMessage() },
            title = { Text("Inventory Transaction Logged") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { onClearMessage() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun InventoryScreenPreview() {
    MaterialTheme {
        InventoryScreenContent(
            items = listOf(
                InventoryItem(
                    facilityId = "F-3",
                    medicineId = "MED-001",
                    medicineName = "Paracetamol 500mg",
                    category = "Analgesic",
                    unit = "Tablets",
                    closingStock = 1200,
                    averageDailyConsumption = 26.3,
                    coverageDays = 45.5,
                    stockGap = 0,
                    riskLevel = "LOW"
                ),
                InventoryItem(
                    facilityId = "F-3",
                    medicineId = "MED-002",
                    medicineName = "Amoxicillin 250mg",
                    category = "Antibiotic",
                    unit = "Capsules",
                    closingStock = 150,
                    averageDailyConsumption = 28.8,
                    coverageDays = 5.2,
                    stockGap = -100,
                    riskLevel = "HIGH"
                )
            ),
            message = null,
            onRecordTransaction = { _, _, _, _, _ -> },
            onClearMessage = {}
        )
    }
}

