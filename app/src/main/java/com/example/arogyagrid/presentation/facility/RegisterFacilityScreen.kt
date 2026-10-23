package com.example.arogyagrid.presentation.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.domain.repository.FacilityRepository
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import kotlinx.coroutines.launch

@Composable
fun RegisterFacilityScreen(
    facilityRepository: FacilityRepository?,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Jaipur") }
    var facilityType by remember { mutableStateOf("Primary Health Centre") }
    var totalBeds by remember { mutableStateOf("30") }
    var occupiedBeds by remember { mutableStateOf("12") }
    var latitude by remember { mutableStateOf("26.9124") }
    var longitude by remember { mutableStateOf("75.7873") }

    var isSubmitting by remember { mutableStateOf(false) }
    var resultDialogMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val facilityTypes = listOf(
        "Primary Health Centre",
        "District Hospital",
        "Community Health Centre",
        "Sub-Centre"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Register Health Facility / PHC",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = PrimaryTeal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Facility Details & Grid Integration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Text(
                    text = "Register a new hospital or PHC to start tracking medical inventory and capacity dynamically.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Facility / Hospital Name") },
                    placeholder = { Text("e.g. Jaipur PHC 5") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District Scope") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Facility Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(facilityTypes) { type ->
                        FilterChip(
                            selected = facilityType == type,
                            onClick = { facilityType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = totalBeds,
                        onValueChange = { totalBeds = it },
                        label = { Text("Total Beds") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = occupiedBeds,
                        onValueChange = { occupiedBeds = it },
                        label = { Text("Occupied Beds") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            resultDialogMessage = "Please enter a valid facility name."
                            return@Button
                        }
                        isSubmitting = true
                        scope.launch {
                            val res = facilityRepository?.registerFacility(
                                name = name,
                                district = district,
                                facilityType = facilityType,
                                latitude = latitude.toDoubleOrNull() ?: 26.9124,
                                longitude = longitude.toDoubleOrNull() ?: 75.7873,
                                totalBeds = totalBeds.toIntOrNull() ?: 30,
                                occupiedBeds = occupiedBeds.toIntOrNull() ?: 10
                            )
                            isSubmitting = false
                            resultDialogMessage = res?.message ?: "Facility registered successfully!"
                        }
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isSubmitting) "Registering Facility..." else "Submit Facility Registration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    resultDialogMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { resultDialogMessage = null },
            title = { Text("Registration Status") },
            text = { Text(msg) },
            confirmButton = {
                Button(
                    onClick = {
                        val wasSuccess = msg.contains("successfully", ignoreCase = true)
                        resultDialogMessage = null
                        if (wasSuccess) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegisterFacilityScreenPreview() {
    MaterialTheme {
        RegisterFacilityScreen(
            facilityRepository = null,
            onNavigateBack = {}
        )
    }
}
