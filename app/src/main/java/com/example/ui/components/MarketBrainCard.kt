package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanEntry
import com.example.ui.theme.CyanLight
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoseRisk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MarketBrainCard(
    activeMode: String,
    onModeChange: (String) -> Unit,
    hasActiveTrade: Boolean,
    onGenerateSignal: (String?) -> Unit,
    onAttemptOverlap: () -> Unit,
    isGenerating: Boolean,
    autoTradingEnabled: Boolean,
    onToggleAutoTrading: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDirection by remember { mutableStateOf<String?>("AUTO") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_analyzer_brain_card"),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Brain",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Market Analyzer Brain",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "10Y Macro PA Matrix & Smart Money Order Flow",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                // 24/7 Auto Execution Toggle Pill
                Box(
                    modifier = Modifier
                        .background(
                            if (autoTradingEnabled) EmeraldDark else CardDarkElevated,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (autoTradingEnabled) EmeraldProfit else BorderDark,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onToggleAutoTrading() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (autoTradingEnabled) EmeraldLight else TextMuted, CircleShape)
                        )
                        Text(
                            text = if (autoTradingEnabled) "24/7 AUTO" else "SEMI-AUTO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (autoTradingEnabled) EmeraldLight else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selection: Scalper vs Swing
            Text(
                text = "TRADING MODE & RISK-TO-REWARD RATIO:",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scalper Mode Option
                val isScalper = activeMode == "scalper"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeChange("scalper") }
                        .testTag("mode_scalper_button"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isScalper) GoldPrimary.copy(alpha = 0.15f) else CardDarkElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isScalper) GoldPrimary else BorderDark
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SCALPER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isScalper) GoldPrimary else TextSecondary
                            )
                            Box(
                                modifier = Modifier
                                    .background(GoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("1:3 R:R", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            }
                        }
                        Text(
                            "$5 Risk   $15 TP",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "50 / 150 pips   83.8% Win",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }

                // Swing Mode Option
                val isSwing = activeMode == "swing"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeChange("swing") }
                        .testTag("mode_swing_button"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSwing) EmeraldProfit.copy(alpha = 0.15f) else CardDarkElevated,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSwing) EmeraldProfit else BorderDark
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SWING",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = if (isSwing) EmeraldLight else TextSecondary
                            )
                            Box(
                                modifier = Modifier
                                    .background(EmeraldProfit.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("1:5 R:R", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                            }
                        }
                        Text(
                            "$20 Risk   $100 TP",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "200 / 1000 pips   85.6% Win",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SMC & Macro Confluence Grid
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = BgDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10Y Macro Structural Bias:", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text("Supercycle Wave 5", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldProfit)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Smart Money Order Flow:", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text("Institutional Absorption", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanEntry)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Anti-Overlap Protocol:", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text(
                            if (hasActiveTrade) "LOCKED (Single Active Trade)" else "READY (Pipeline Idle)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasActiveTrade) RoseRisk else EmeraldProfit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direction Preference Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("AUTO", "BUY", "SELL").forEach { dir ->
                    val isSel = selectedDirection == dir
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSel) {
                                    when (dir) {
                                        "BUY" -> EmeraldDark
                                        "SELL" -> RoseDark
                                        else -> GoldPrimary.copy(alpha = 0.2f)
                                    }
                                } else CardDarkElevated,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isSel) {
                                    when (dir) {
                                        "BUY" -> EmeraldProfit
                                        "SELL" -> RoseRisk
                                        else -> GoldPrimary
                                    }
                                } else BorderDark,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedDirection = dir }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dir == "AUTO") "AUTO (AI)" else if (dir == "BUY") "LONG (BUY)" else "SHORT (SELL)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) TextPrimary else TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Execution Button: STRICT ANTI-OVERLAP ENFORCED!
            if (hasActiveTrade) {
                Button(
                    onClick = onAttemptOverlap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("overlap_locked_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDark),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, RoseRisk)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = RoseLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "ANTI-OVERLAP LOCK (TAP FOR REFUSAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = RoseLight,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        val dir = if (selectedDirection == "AUTO") null else selectedDirection
                        onGenerateSignal(dir)
                    },
                    enabled = !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("generate_signal_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = BgDark,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "ANALYZING 10Y MACRO MATRIX...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = BgDark
                        )
                    } else {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = BgDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "SCAN GOLD & ISSUE ${activeMode.uppercase()} SIGNAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = BgDark,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
