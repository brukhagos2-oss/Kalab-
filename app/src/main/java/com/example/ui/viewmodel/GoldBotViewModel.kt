package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AurumDatabase
import com.example.data.model.BotLogEntity
import com.example.data.model.BotSettingsEntity
import com.example.data.model.Candle
import com.example.data.model.HistoricalDecadeEntity
import com.example.data.model.MacroSummary
import com.example.data.model.MarketTicker
import com.example.data.model.SignalEntity
import com.example.domain.engine.AntiOverlapException
import com.example.domain.engine.GoldEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GoldBotViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AurumDatabase.getInstance(application)
    val engine = GoldEngine(database)

    val activeTrade: StateFlow<SignalEntity?> = engine.activeSignalFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val recentTrades: StateFlow<List<SignalEntity>> = engine.recentTradesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val resolvedTrades: StateFlow<List<SignalEntity>> = engine.resolvedTradesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val botSettings: StateFlow<BotSettingsEntity?> = engine.botSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BotSettingsEntity()
    )

    val logs: StateFlow<List<BotLogEntity>> = engine.logsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val decadeData: StateFlow<List<HistoricalDecadeEntity>> = engine.decadeDataFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _ticker = MutableStateFlow(MarketTicker())
    val ticker: StateFlow<MarketTicker> = _ticker.asStateFlow()

    private val _candles = MutableStateFlow<List<Candle>>(emptyList())
    val candles: StateFlow<List<Candle>> = _candles.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("15m")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isChartLoading = MutableStateFlow(false)
    val isChartLoading: StateFlow<Boolean> = _isChartLoading.asStateFlow()

    private val _antiOverlapRefusal = MutableStateFlow<AntiOverlapException?>(null)
    val antiOverlapRefusal: StateFlow<AntiOverlapException?> = _antiOverlapRefusal.asStateFlow()

    private val _showDecadeDialog = MutableStateFlow(false)
    val showDecadeDialog: StateFlow<Boolean> = _showDecadeDialog.asStateFlow()

    private val _macroSummary = MutableStateFlow(MacroSummary())
    val macroSummary: StateFlow<MacroSummary> = _macroSummary.asStateFlow()

    private var tickerPollingJob: Job? = null
    private var tradeTrackingJob: Job? = null

    init {
        viewModelScope.launch {
            AurumDatabase.seedDatabase(database)
            loadCandles(_selectedTimeframe.value)
            refreshTicker()
            startRealTimeLoops()
        }
    }

    private fun startRealTimeLoops() {
        // Ticker loop (every 1.5s)
        tickerPollingJob?.cancel()
        tickerPollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val updated = engine.fetchLiveTicker()
                    _ticker.value = updated
                } catch (e: Exception) {
                    // Ignore transient network errors
                }
                delay(1500)
            }
        }

        // Active Trade Tracking Loop (every 1.8s)
        tradeTrackingJob?.cancel()
        tradeTrackingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val currentLivePrice = _ticker.value.price
                    engine.evaluateActiveTrade(currentLivePrice)
                } catch (e: Exception) {
                    // Ignore
                }
                delay(1800)
            }
        }
    }

    fun setTimeframe(tf: String) {
        _selectedTimeframe.value = tf
        loadCandles(tf)
    }

    fun loadCandles(tf: String = _selectedTimeframe.value) {
        viewModelScope.launch {
            _isChartLoading.value = true
            try {
                val list = engine.fetchCandles(tf, _ticker.value.price)
                _candles.value = list
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isChartLoading.value = false
            }
        }
    }

    fun refreshTicker() {
        viewModelScope.launch {
            try {
                val updated = engine.fetchLiveTicker()
                _ticker.value = updated
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun generateSignal(directionOverride: String? = null) {
        viewModelScope.launch {
            val currentActive = activeTrade.value
            if (currentActive != null) {
                // Enforce Anti-Overlap
                val entry = currentActive.entryPrice
                val isBuy = currentActive.direction == "BUY"
                val liveP = _ticker.value.price
                val pnlDollars = if (isBuy) (liveP - entry) else (entry - liveP)
                val pnlPips = pnlDollars * 10.0
                val sign = if (pnlDollars >= 0) "+" else ""

                val refusalMsg = "NO. Signal generation denied: Anti-Overlap Protocol strictly enforces the Single Active Signal rule. Trade #${currentActive.id} (${currentActive.direction} at $${"%.2f".format(entry)}) is currently RUNNING on XAU/USD. Current PnL: $sign$${"%.2f".format(pnlDollars)} ($sign$${"%.1f".format(pnlPips)} pips). New signals are blocked until this trade hits TP ($${"%.2f".format(currentActive.takeProfitPrice)}) or SL ($${"%.2f".format(currentActive.stopLossPrice)})."

                _antiOverlapRefusal.value = AntiOverlapException(
                    refusalMessage = refusalMsg,
                    activeTrade = currentActive,
                    currentPrice = liveP,
                    currentPnlDollars = Math.round(pnlDollars * 100.0) / 100.0,
                    currentPnlPips = Math.round(pnlPips * 10.0) / 10.0
                )
                engine.soundEffects.playRefusalSound()
                return@launch
            }

            _isGenerating.value = true
            try {
                val mode = botSettings.value?.activeMode ?: "scalper"
                engine.generateSignal(mode, directionOverride)
                loadCandles(_selectedTimeframe.value)
            } catch (e: AntiOverlapException) {
                _antiOverlapRefusal.value = e
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun triggerAntiOverlapRefusalDirectly() {
        val currentActive = activeTrade.value ?: return
        val entry = currentActive.entryPrice
        val isBuy = currentActive.direction == "BUY"
        val liveP = _ticker.value.price
        val pnlDollars = if (isBuy) (liveP - entry) else (entry - liveP)
        val pnlPips = pnlDollars * 10.0
        val sign = if (pnlDollars >= 0) "+" else ""

        val refusalMsg = "NO. Signal generation denied: Anti-Overlap Protocol strictly enforces the Single Active Signal rule. Trade #${currentActive.id} (${currentActive.direction} at $${"%.2f".format(entry)}) is currently RUNNING on XAU/USD. Current PnL: $sign$${"%.2f".format(pnlDollars)} ($sign$${"%.1f".format(pnlPips)} pips). New signals are blocked until this trade hits TP ($${"%.2f".format(currentActive.takeProfitPrice)}) or SL ($${"%.2f".format(currentActive.stopLossPrice)})."

        _antiOverlapRefusal.value = AntiOverlapException(
            refusalMessage = refusalMsg,
            activeTrade = currentActive,
            currentPrice = liveP,
            currentPnlDollars = Math.round(pnlDollars * 100.0) / 100.0,
            currentPnlPips = Math.round(pnlPips * 10.0) / 10.0
        )
        engine.soundEffects.playRefusalSound()
    }

    fun dismissAntiOverlapRefusal() {
        _antiOverlapRefusal.value = null
    }

    fun setMode(mode: String) {
        viewModelScope.launch {
            engine.updateSettings(mode = mode)
        }
    }

    fun toggleAutoTrading() {
        viewModelScope.launch {
            val current = botSettings.value?.autoTradingEnabled ?: true
            engine.updateSettings(autoTrading = !current)
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            val current = botSettings.value?.soundEnabled ?: true
            engine.updateSettings(sound = !current)
        }
    }

    fun simulateTpHit() {
        viewModelScope.launch {
            engine.simulateTpHit()
        }
    }

    fun simulateSlHit() {
        viewModelScope.launch {
            engine.simulateSlHit()
        }
    }

    fun closeAtMarket() {
        viewModelScope.launch {
            engine.manualClose()
        }
    }

    fun resetTradeHistory() {
        viewModelScope.launch {
            engine.resetHistory()
        }
    }

    fun clearTerminalLogs() {
        viewModelScope.launch {
            engine.clearLogs()
        }
    }

    fun openDecadeDialog() {
        _showDecadeDialog.value = true
    }

    fun closeDecadeDialog() {
        _showDecadeDialog.value = false
    }
}
