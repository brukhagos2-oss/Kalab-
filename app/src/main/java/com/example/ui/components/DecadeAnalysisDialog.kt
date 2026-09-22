package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HistoricalDecadeEntity
import com.example.data.model.MacroSummary
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.CyanEntry
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RoseDark
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoseRisk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DecadeAnalysisDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    decadeRecords: List<HistoricalDecadeEntity>,
    macroSummary: MacroSummary
) {
    if (!isOpen) return

    var selectedYear by remember { mutableStateOf(2025) }
    val selectedRecord = decadeRecords.find { it.year == selectedYear } ?: decadeRecords.lastOrNull()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("decade_analysis_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderGold)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarkElevated)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = "10-Year Macro PA Brain (2014 - 2026)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Decade liquidity cycle backtest (>80% win rate threshold)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Summary Stat Cards Grid
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("10Y WIN RATE", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                    Text("${macroSummary.overallDecadeWinRate}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldProfit)
                                    Text("Decade Verified", fontSize = 8.sp, color = EmeraldLight)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("PA TRADES", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                    Text("${macroSummary.totalTradesAnalyzed}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Decade Sample", fontSize = 8.sp, color = TextSecondary)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("SCALPER / SWING", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                    Text("83.8% / 85.6%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("1:3 vs 1:5 R:R", fontSize = 8.sp, color = TextSecondary)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("LOW / HIGH", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                    Text("$1046 / $4369", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanEntry)
                                    Text("+${macroSummary.decadeGainPercent}% Gain", fontSize = 8.sp, color = CyanEntry)
                                }
                            }
                        }
                    }

                    // 2. Selected Year Highlight Card
                    if (selectedRecord != null) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = BgDark,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderGold)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(GoldPrimary, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "YEAR ${selectedRecord.year} PROFILE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BgDark,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        Text(
                                            text = "Win Rate: ${selectedRecord.algorithmicWinRate}% | Profit Factor: ${selectedRecord.profitFactor}x",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Text(
                                        text = selectedRecord.marketRegime,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Text(
                                        text = "Macro Catalyst: ${selectedRecord.keyCatalyst}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardDarkElevated, RoundedCornerShape(6.dp))
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Open: $${selectedRecord.openPrice}", fontSize = 9.sp, color = TextSecondary)
                                        Text("High: $${selectedRecord.highPrice}", fontSize = 9.sp, color = EmeraldProfit)
                                        Text("Low: $${selectedRecord.lowPrice}", fontSize = 9.sp, color = RoseRisk)
                                        Text("Close: $${selectedRecord.closePrice}", fontSize = 9.sp, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // 3. Year by Year Table
                    item {
                        Text(
                            text = "DECADE HISTORICAL LOG (TAP YEAR TO INSPECT):",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }

                    items(decadeRecords) { record ->
                        val isSel = record.year == selectedYear
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedYear = record.year },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) GoldPrimary.copy(alpha = 0.15f) else CardDarkElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) GoldPrimary else BorderDark
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "${record.year}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary,
                                            fontSize = 11.sp
                                        )
                                        val chg = record.annualChangePercent
                                        Text(
                                            text = "${if (chg >= 0) "+" else ""}$chg%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (chg >= 0) EmeraldProfit else RoseRisk
                                        )
                                    }
                                    Text(
                                        text = record.marketRegime,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${record.algorithmicWinRate}% Win",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldProfit,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${record.totalTrades} Trades | ${record.profitFactor}x PF",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarkElevated)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Close & Return to Terminal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BgDark)
                    }
                }
            }
        }
    }
}
