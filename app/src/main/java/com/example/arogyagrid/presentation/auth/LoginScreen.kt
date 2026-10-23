package com.example.arogyagrid.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.core.UserRole
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted


@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole, String?) -> Unit
) {
    var username by remember { mutableStateOf("officer_jaipur") }
    var selectedRole by remember { mutableStateOf(UserRole.DISTRICT_OFFICER) }
    var district by remember { mutableStateOf("Jaipur") }

    val roles = listOf(
        UserRole.STATE_ADMIN to "State Admin",
        UserRole.DISTRICT_OFFICER to "District Officer",
        UserRole.FACILITY_MANAGER to "Facility Manager",
        UserRole.PHARMACIST_OPERATOR to "Pharmacist"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SmartHealth AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryTeal
            )
            Text(
                text = "Healthcare Resource Intelligence Platform",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Command Center Access",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Officer Role", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(roles) { (role, label) ->
                            FilterChip(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role },
                                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District Scope") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onLoginSuccess(selectedRole, district) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Sign In to Command Center", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
@Preview (showSystemUi = true)
@Composable
fun LoginScreen ()
{
    MaterialTheme {
        LoginScreen(onLoginSuccess = { _, _ -> })
    }
}

