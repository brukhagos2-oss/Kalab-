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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.BotLogEntity
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldAccent
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
fun TerminalLogsView(
    logs: List<BotLogEntity>,
    onClearLogs: () -> Unit,
    latencyMs: Long,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "TP_HIT", "SL_HIT", "SIGNAL", "REFUSAL", "INFO")
    val listState = rememberLazyListState()

    val filteredLogs = remember(logs, selectedFilter) {
        if (selectedFilter == "ALL") logs else logs.filter { it.level == selectedFilter }
    }

    LaunchedEffect(filteredLogs.size) {
        if (filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("terminal_logs_view"),
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
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(EmeraldProfit, CircleShape)
                    )
                    Text(
                        text = "24/7 Fully Alert Terminal",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                    Text(
                        text = "(${latencyMs}ms)",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontSize = 9.sp
                    )
                }

                IconButton(
                    onClick = onClearLogs,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Clear Logs",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filter Pills
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
                                "SIGNAL" -> "Signals"
                                "REFUSAL" -> "Blocks"
                                "INFO" -> "Info"
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

            // Logs Output Viewport
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(10.dp),
                color = BgDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
            ) {
                if (filteredLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        Text("Awaiting system events...", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredLogs, key = { it.id }) { log ->
                            val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
                            val timeStr = sdf.format(Date(log.timestamp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardDarkElevated.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "[$timeStr]",
                                    fontSize = 8.sp,
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace
                                )

                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (log.level) {
                                                "TP_HIT" -> EmeraldDark
                                                "SL_HIT" -> RoseDark
                                                "SIGNAL" -> GoldPrimary.copy(alpha = 0.3f)
                                                "REFUSAL" -> RoseDark
                                                else -> BorderDark
                                            },
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = log.level,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (log.level) {
                                            "TP_HIT" -> EmeraldLight
                                            "SL_HIT" -> RoseLight
                                            "SIGNAL" -> GoldAccent
                                            "REFUSAL" -> RoseLight
                                            else -> TextSecondary
                                        },
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = log.message,
                                    fontSize = 9.sp,
                                    color = when (log.level) {
                                        "TP_HIT" -> EmeraldLight
                                        "SL_HIT" -> RoseLight
                                        "SIGNAL" -> GoldAccent
                                        "REFUSAL" -> RoseLight
                                        else -> TextPrimary
                                    },
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
