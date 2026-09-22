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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeHistoryView(
    trades: List<SignalEntity>,
    onResetHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "TP_HIT", "SL_HIT", "SCALPER", "SWING")

    val filteredTrades = remember(trades, selectedFilter) {
        when (selectedFilter) {
            "TP_HIT" -> trades.filter { it.status == "TP_HIT" }
            "SL_HIT" -> trades.filter { it.status == "SL_HIT" }
            "SCALPER" -> trades.filter { it.mode == "scalper" }
            "SWING" -> trades.filter { it.mode == "swing" }
            else -> trades
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trade_history_view"),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Live Trade Outcomes Log",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .background(CardDarkElevated, RoundedCornerShape(4.dp))
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${trades.size} Records",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(
                    onClick = onResetHistory,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset History",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                filters.forEach { f ->
                    val isSel = f == selectedFilter
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSel) GoldPrimary else CardDarkElevated,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when (f) {
                                "TP_HIT" -> "TP Hits"
                                "SL_HIT" -> "SL Hits"
                                "SCALPER" -> "Scalper"
                                "SWING" -> "Swing"
                                else -> "All"
                            },
                            fontSize = 8.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) BgDark else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trades List
            if (filteredTrades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No trades matching filter.", color = TextMuted, fontSize = 11.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredTrades, key = { it.id }) { trade ->
                        val isBuy = trade.direction == "BUY"
                        val isTp = trade.status == "TP_HIT"
                        val isSl = trade.status == "SL_HIT"
                        val isActive = trade.status == "ACTIVE"
                        val pnl = trade.finalPnl ?: 0.0
                        val pips = trade.finalPnlPips ?: 0.0

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isActive) GoldPrimary.copy(alpha = 0.1f) else BgDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isActive) GoldPrimary.copy(alpha = 0.4f) else BorderDark
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "#${trade.id}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Icon(
                                                if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                contentDescription = null,
                                                tint = if (isBuy) EmeraldProfit else RoseRisk,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "${trade.direction} (${trade.mode.uppercase()})",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isBuy) EmeraldProfit else RoseRisk
                                            )
                                        }
                                    }

                                    // Status Badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                when {
                                                    isTp -> EmeraldDark
                                                    isSl -> RoseDark
                                                    isActive -> GoldPrimary.copy(alpha = 0.3f)
                                                    else -> CardDarkElevated
                                                },
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isTp -> "TP HIT"
                                                isSl -> "SL HIT"
                                                isActive -> "RUNNING"
                                                else -> "CLOSED"
                                            },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when {
                                                isTp -> EmeraldLight
                                                isSl -> RoseLight
                                                isActive -> GoldPrimary
                                                else -> TextSecondary
                                            },
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Entry $${"%.2f".format(trade.entryPrice)} -> TP $${"%.2f".format(trade.takeProfitPrice)} | SL $${"%.2f".format(trade.stopLossPrice)}",
                                        fontSize = 8.sp,
                                        color = TextMuted,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    if (!isActive) {
                                        val sign = if (pnl >= 0) "+" else ""
                                        Text(
                                            text = "$sign$${"%.2f".format(pnl)} ($sign${"%.1f".format(pips)} p)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (pnl >= 0) EmeraldProfit else RoseRisk,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    } else {
                                        Text("Tracking live...", fontSize = 9.sp, color = GoldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
