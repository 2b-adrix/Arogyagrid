package com.example.arogyagrid.presentation.ai

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.*
import com.example.arogyagrid.presentation.viewmodel.AiAssistantViewModel

@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel = viewModel(),
    onNavigateToScreen: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    var inputPrompt by remember { mutableStateOf("") }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputPrompt = spoken
                viewModel.sendMessage(spoken)
            }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val suggestedQuestions = remember {
        listOf(
            "Where can Amer get emergency ORS?",
            "Check cold chain temperature breach",
            "Analyze Amer diarrhea outbreak spike",
            "How many ICU beds and occupancy in Jaipur?",
            "Check Amoxicillin stock across facilities",
            "Summarize Jaipur operations"
        )
    }

    val suggestedQuestionLabels = remember {
        listOf(
            "[आपूर्ति] आमेर ओआरएस पुनर्वितरण",
            "[कोल्ड चेन] वैक्सीन तापमान विचलन",
            "[महामारी] अतिसार प्रकोप व प्रक्षेपण",
            "[शय्या स्थिति] एसएमएस व आईसीयू उपलब्धता",
            "[स्टॉक ऑडिट] एमोक्सिसिलिन व ड्रग्स",
            "[दैनिक रिपोर्ट] जिला स्वास्थ्य ब्रीफ"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SyntheticDataBanner()

        CommandTopBar(
            roleLabel = UserSession.getFormattedRole(),
            districtName = UserSession.userDistrict ?: "Jaipur",
            showSearch = false,
            onSyncClick = {}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Official Masthead for CDSS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TirangaSaffron)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "राष्ट्रीय स्वास्थ्य मिशन • राजस्थान सरकार",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TirangaSaffron,
                            letterSpacing = 0.4.sp
                        )
                    }
                    Text(
                        text = "क्लिनिकल एवं आपूर्ति निर्णय सहायता प्रणाली (CDSS)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GovtNavy
                    )
                }

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = GovtNavyLight,
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Text(
                        text = "e-Aushadhi / IDSP",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtNavy,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Statutory Guardrails Card (Compact Bureaucratic Strip)
            Surface(
                shape = RoundedCornerShape(3.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Guardrail",
                        tint = GovtNavy,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "वैधानिक दिशा-निर्देश: अंतर-संस्थानिक औषधि आवंटन हेतु मुख्य चिकित्सा अधिकारी (CMHO) का डिजिटल अनुमोदन अनिवार्य है।",
                        fontSize = 9.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Official Query Suggestion Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                items(suggestedQuestions.indices.toList()) { index ->
                    val query = suggestedQuestions[index]
                    val label = suggestedQuestionLabels[index]
                    AssistChip(
                        onClick = {
                            viewModel.sendMessage(query)
                            inputPrompt = ""
                        },
                        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        shape = RoundedCornerShape(3.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = GovtNavy
                        ),
                        border = BorderStroke(1.dp, SlateBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Message List (Official Gazette / Memorandum Cards)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { msg ->
                    val isUser = msg.isUser
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) GovtNavy else MaterialTheme.colorScheme.surface
                            ),
                            border = if (!isUser) CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)) else null,
                            elevation = CardDefaults.cardElevation(0.dp),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.96f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!isUser) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(TirangaSaffron)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "एनएचएम राजस्थान • सीडीएसएस परामर्श (NHM-CDSS ADVISORY)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GovtNavy,
                                            letterSpacing = 0.3.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Text(
                                    text = msg.text,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = if (isUser) Color.White else TextDark
                                )

                                // Official Action Directives (e.g. Open Route Map / Review Order)
                                if (msg.actionRoute != null && !isUser) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onNavigateToScreen(msg.actionRoute) },
                                        shape = RoundedCornerShape(3.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(msg.actionLabel ?: "संबंधित मॉड्यूल खोलें →", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (msg.dataSources.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("सत्यापित डेटा स्रोत (Grounded Sources):", fontSize = 8.5.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        items(msg.dataSources) { src ->
                                            Surface(
                                                shape = RoundedCornerShape(2.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
                                            ) {
                                                Text(
                                                    text = src,
                                                    fontSize = 8.sp,
                                                    color = GovtNavy,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = GovtNavy, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("एनएचएम एवं आरएमएससीएल डेटाबेस से सत्यापन जारी...", fontSize = 10.5.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Input Bar with Voice & Send (Official NIC Text Field)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("प्रश्न या आदेश दर्ज करें (उदा. आमेर ओआरएस, कोल्ड चेन)...", fontSize = 10.5.sp, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = GovtNavy,
                        unfocusedBorderColor = SlateBorder
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                // Voice Speech-to-Text Input Button
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "स्वास्थ्य निर्देश बोलें...")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(GovtNavyLight)
                        .border(1.dp, SlateBorder, RoundedCornerShape(3.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = GovtNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank()) {
                            viewModel.sendMessage(inputPrompt)
                            inputPrompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(GovtNavy)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
