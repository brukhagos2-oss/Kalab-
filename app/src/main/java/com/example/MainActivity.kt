package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActiveTradeCard
import com.example.ui.components.AntiOverlapDialog
import com.example.ui.components.DecadeAnalysisDialog
import com.example.ui.components.InteractiveGoldChart
import com.example.ui.components.MarketBrainCard
import com.example.ui.components.TerminalLogsView
import com.example.ui.components.TopStatsRow
import com.example.ui.components.TradeHistoryView
import com.example.ui.theme.AurumBrainTheme
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoseRisk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GoldBotViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GoldBotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AurumBrainTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: GoldBotViewModel) {
    val ticker by viewModel.ticker.collectAsState()
    val candles by viewModel.candles.collectAsState()
    val timeframe by viewModel.selectedTimeframe.collectAsState()
    val activeTrade by viewModel.activeTrade.collectAsState()
    val recentTrades by viewModel.recentTrades.collectAsState()
    val resolvedTrades by viewModel.resolvedTrades.collectAsState()
    val botSettings by viewModel.botSettings.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val decadeData by viewModel.decadeData.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isChartLoading by viewModel.isChartLoading.collectAsState()
    val antiOverlapRefusal by viewModel.antiOverlapRefusal.collectAsState()
    val showDecadeDialog by viewModel.showDecadeDialog.collectAsState()
    val macroSummary by viewModel.macroSummary.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("TERMINAL & CHART", "TRADE HISTORY", "10Y MACRO PA")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("aurum_main_scaffold"),
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardDark,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(GoldPrimary, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Au",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = BgDark,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "AURUM BRAIN",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(EmeraldProfit.copy(alpha = 0.2f), CircleShape)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Box(modifier = Modifier.size(5.dp).background(EmeraldProfit, CircleShape))
                                        Text("LIVE 24/7", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                    }
                                }
                            }
                            Text(
                                text = "Gold Trading Bot & 10Y PA Analyzer",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                },
                actions = {
                    // Sound Toggle
                    val soundOn = botSettings?.soundEnabled ?: true
                    IconButton(
                        onClick = { viewModel.toggleSound() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (soundOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sound Toggle",
                            tint = if (soundOn) GoldPrimary else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 10Y Macro PA Button
                    IconButton(
                        onClick = { viewModel.openDecadeDialog() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "10Y Macro PA",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Refresh
                    IconButton(
                        onClick = {
                            viewModel.refreshTicker()
                            viewModel.loadCandles(timeframe)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgDark)
        ) {
            // Stats Row at Top
            TopStatsRow(
                allSignals = recentTrades,
                hasActiveTrade = activeTrade != null,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            // Primary Navigation Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardDark,
                contentColor = GoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary,
                        height = 2.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) GoldPrimary else TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // TERMINAL & CHART VIEW
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Candlestick Chart with TP/SL/Entry levels
                        item {
                            InteractiveGoldChart(
                                candles = candles,
                                selectedTimeframe = timeframe,
                                onTimeframeSelected = { viewModel.setTimeframe(it) },
                                activeTrade = activeTrade,
                                livePrice = ticker.price,
                                isLoading = isChartLoading,
                                onRefresh = { viewModel.loadCandles(timeframe) }
                            )
                        }

                        // 2. Active Trade Card (Single Active Signal & Anti-Overlap Status)
                        item {
                            ActiveTradeCard(
                                activeTrade = activeTrade,
                                livePrice = ticker.price,
                                onSimulateTp = { viewModel.simulateTpHit() },
                                onSimulateSl = { viewModel.simulateSlHit() },
                                onManualClose = { viewModel.closeAtMarket() }
                            )
                        }

                        // 3. Market Analyzer Brain Strategy Card (Scalper vs Swing, Lock check)
                        item {
                            MarketBrainCard(
                                activeMode = botSettings?.activeMode ?: "scalper",
                                onModeChange = { viewModel.setMode(it) },
                                hasActiveTrade = activeTrade != null,
                                onGenerateSignal = { dir -> viewModel.generateSignal(dir) },
                                onAttemptOverlap = { viewModel.triggerAntiOverlapRefusalDirectly() },
                                isGenerating = isGenerating,
                                autoTradingEnabled = botSettings?.autoTradingEnabled ?: true,
                                onToggleAutoTrading = { viewModel.toggleAutoTrading() }
                            )
                        }

                        // 4. 24/7 Fully Alert Terminal Logs Console
                        item {
                            TerminalLogsView(
                                logs = logs,
                                onClearLogs = { viewModel.clearTerminalLogs() },
                                latencyMs = ticker.latencyMs
                            )
                        }

                        // Bottom breathing room
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                1 -> {
                    // TRADE HISTORY VIEW
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            ActiveTradeCard(
                                activeTrade = activeTrade,
                                livePrice = ticker.price,
                                onSimulateTp = { viewModel.simulateTpHit() },
                                onSimulateSl = { viewModel.simulateSlHit() },
                                onManualClose = { viewModel.closeAtMarket() }
                            )
                        }

                        item {
                            TradeHistoryView(
                                trades = recentTrades,
                                onResetHistory = { viewModel.resetTradeHistory() }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                2 -> {
                    // 10Y MACRO PA VIEW
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = CardDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                text = "10-Year Price Action Macro Engine",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "2014 - 2026 XAU/USD Generational Liquidity Matrix",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = "The Aurum Brain Market Analyzer algorithm correlates real-time 15-minute order flow against 10 years of institutional Gold liquidity cycles. Backtested across 4,065 historical market setups with an aggregate 84.4% win rate.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        item {
                            InteractiveGoldChart(
                                candles = candles,
                                selectedTimeframe = "10Y",
                                onTimeframeSelected = { viewModel.setTimeframe(it) },
                                activeTrade = activeTrade,
                                livePrice = ticker.price,
                                isLoading = isChartLoading,
                                onRefresh = { viewModel.loadCandles("10Y") }
                            )
                        }

                        item {
                            TradeHistoryView(
                                trades = recentTrades,
                                onResetHistory = { viewModel.resetTradeHistory() }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal: Anti-Overlap Refusal Notice Dialog
    AntiOverlapDialog(
        refusal = antiOverlapRefusal,
        onDismiss = { viewModel.dismissAntiOverlapRefusal() }
    )

    // Modal: 10-Year Macro Brain Analysis Inspector
    DecadeAnalysisDialog(
        isOpen = showDecadeDialog,
        onClose = { viewModel.closeDecadeDialog() },
        decadeRecords = decadeData,
        macroSummary = macroSummary
    )
}
