package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalEntity
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
import kotlin.math.abs

@Composable
fun ActiveTradeCard(
    activeTrade: SignalEntity?,
    livePrice: Double,
    onSimulateTp: () -> Unit,
    onSimulateSl: () -> Unit,
    onManualClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeTrade == null) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .testTag("active_trade_empty_card"),
            shape = RoundedCornerShape(16.dp),
            color = CardDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(EmeraldProfit.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, EmeraldProfit.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = EmeraldProfit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(EmeraldProfit, CircleShape)
                    )
                    Text(
                        text = "PIPELINE READY   NO ACTIVE SIGNAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Anti-Overlap Guard is idle. Market Analyzer Brain is continuously scanning Gold (XAU/USD) order flow for high-probability setups.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 11.sp
                )
            }
        }
        return
    }

    val currentP = if (livePrice > 0) livePrice else activeTrade.entryPrice
    val entry = activeTrade.entryPrice
    val tp = activeTrade.takeProfitPrice
    val sl = activeTrade.stopLossPrice
    val isBuy = activeTrade.direction == "BUY"

    val diff = if (isBuy) currentP - entry else entry - currentP
    val pnlDollars = diff
    val pnlPips = diff * 10.0

    val distToTp = abs(tp - currentP)
    val pipsToTp = distToTp * 10.0
    val distToSl = abs(currentP - sl)
    val pipsToSl = distToSl * 10.0

    val targetRange = abs(tp - entry)
    val rawProgress = if (targetRange > 0) (diff / targetRange).toFloat() else 0f
    val progressClamped = rawProgress.coerceIn(-1f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressClamped, label = "progress")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_trade_running_card"),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(2.dp, BorderGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: ID, Lock indicator, Mode pill, R:R
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = GoldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ACTIVE #${activeTrade.id}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(GoldPrimary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ANTI-OVERLAP LOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = BgDark,
                            fontSize = 9.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isBuy) EmeraldDark else RoseDark,
                                RoundedCornerShape(6.dp)
                            )
                            .border(1.dp, if (isBuy) EmeraldProfit else RoseRisk, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(
                                if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isBuy) EmeraldLight else RoseLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${activeTrade.direction} (${activeTrade.mode.uppercase()})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isBuy) EmeraldLight else RoseLight
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(6.dp))
                            .border(1.dp, BorderDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "R:R ${activeTrade.riskRewardRatio}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Floating PnL Display Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BgDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LIVE FLOATING P&L",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val sign = if (pnlDollars >= 0) "+" else ""
                            Text(
                                text = "$sign$${"%.2f".format(pnlDollars)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = if (pnlDollars >= 0) EmeraldProfit else RoseRisk
                            )
                            Text(
                                text = "($sign${"%.1f".format(pnlPips)} pips)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (pnlDollars >= 0) EmeraldLight else RoseLight,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CURRENT GOLD SPOT",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "$${"%.2f".format(currentP)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Points: $${"%.2f".format(currentP)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Progress Bar towards TP or SL
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SL $${"%.2f".format(sl)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = RoseRisk
                    )
                    Text(
                        text = "Entry: $${"%.2f".format(entry)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyanEntry
                    )
                    Text(
                        text = "TP $${"%.2f".format(tp)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldProfit
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BgDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                ) {
                    if (animatedProgress >= 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((animatedProgress.coerceIn(0.05f, 1f)))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.horizontalGradient(listOf(CyanEntry, EmeraldProfit)))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((abs(animatedProgress).coerceIn(0.05f, 1f)))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.horizontalGradient(listOf(RoseRisk, Color(0xFFE11D48))))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "To SL: $${"%.2f".format(distToSl)} (${"%.1f".format(pipsToSl)} pips)",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseLight,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "To TP: $${"%.2f".format(distToTp)} (${"%.1f".format(pipsToTp)} pips)",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldLight,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Row: Target Profit, Max Risk, Win Prob
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = CardDarkElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("TARGET PROFIT", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text("+$$" + "%.0f".format(activeTrade.rewardDollars), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldProfit)
                        Text("+${"%.0f".format(activeTrade.rewardPips)} pips", fontSize = 8.sp, color = TextSecondary)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = CardDarkElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("MAX RISK", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text("-$$" + "%.0f".format(activeTrade.riskDollars), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoseRisk)
                        Text("-${"%.0f".format(activeTrade.riskPips)} pips", fontSize = 8.sp, color = TextSecondary)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = CardDarkElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("WIN PROBABILITY", fontSize = 8.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        Text("${"%.1f".format(activeTrade.winRateProjection)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text("10Y Verified", fontSize = 8.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Testing / Operator Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSimulateTp,
                    modifier = Modifier.weight(1f).height(32.dp).testTag("sim_tp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fast TP Hit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                }

                Button(
                    onClick = onSimulateSl,
                    modifier = Modifier.weight(1f).height(32.dp).testTag("sim_sl_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = RoseLight, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fast SL Hit", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoseLight)
                }

                OutlinedButton(
                    onClick = onManualClose,
                    modifier = Modifier.weight(1f).height(32.dp).testTag("manual_close_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Text("Market Close", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}
