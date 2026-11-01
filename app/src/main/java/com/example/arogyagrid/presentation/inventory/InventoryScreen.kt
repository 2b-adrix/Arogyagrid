package com.example.arogyagrid.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showTransactionDialog by remember { mutableStateOf<InventoryItem?>(null) }
    var transactionType by remember { mutableStateOf("RECEIPT") }
    var quantityText by remember { mutableStateOf("50") }

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
                        text = "Real-time stock balances & daily consumption tracking",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading inventory", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadInventory() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val filteredItems = uiState.items.filter {
                    searchQuery.isBlank() ||
                            it.medicineName.contains(searchQuery, ignoreCase = true) ||
                            it.facilityName.contains(searchQuery, ignoreCase = true)
                }

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
                            modifier = Modifier.fillMaxWidth()
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
                                            text = "${item.facilityName} • Batch: ${item.batchNumber ?: "N/A"}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = item.criticality)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("CLOSING STOCK", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.closingStock}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Column {
                                        Text("DAILY CONSUMPTION", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.avgDailyConsumption}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("COVERAGE DAYS", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.coverageDays} days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Min: ${item.minimumStock} | Max: ${item.maximumStock} | Gap: ${item.stockGap}", fontSize = 11.sp, color = TextMuted)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            transactionType = "RECEIPT"
                                            showTransactionDialog = item
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Receive Stock", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            transactionType = "ISSUE"
                                            showTransactionDialog = item
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Issue Stock", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showTransactionDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showTransactionDialog = null },
            title = { Text("$transactionType: ${item.medicineName}") },
            text = {
                Column {
                    Text("Facility: ${item.facilityName}", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull() ?: 1
                        viewModel.recordTransaction(
                            facilityId = item.facilityId,
                            medicineId = item.medicineId,
                            type = transactionType,
                            quantity = qty
                        )
                        showTransactionDialog = null
                    }
                ) {
                    Text("Submit Transaction")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTransactionDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
