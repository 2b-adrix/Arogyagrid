package com.example.arogyagrid.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusGreenBg
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusOrangeBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg

@Composable
fun RiskBadge(riskLevel: String) {
    val level = riskLevel.uppercase()
    val (textColor, bgColor) = when (level) {
        "CRITICAL" -> StatusRed to StatusRedBg
        "HIGH" -> StatusOrange to StatusOrangeBg
        "MEDIUM" -> StatusOrange to StatusOrangeBg
        "WARNING" -> StatusOrange to StatusOrangeBg
        else -> StatusGreen to StatusGreenBg
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = level,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
@Preview(showSystemUi = true)
@Composable
fun RiskBadge()
{
    MaterialTheme {
        RiskBadge(

        )
    }}
