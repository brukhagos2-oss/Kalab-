package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asset: String = "XAU/USD (PAXG)",
    val mode: String = "scalper", // "scalper" | "swing"
    val direction: String = "BUY", // "BUY" | "SELL"
    val entryPrice: Double,
    val takeProfitPrice: Double,
    val stopLossPrice: Double,
    val riskDollars: Double,
    val rewardDollars: Double,
    val riskPips: Double,
    val rewardPips: Double,
    val riskRewardRatio: String, // "1:3" or "1:5"
    val status: String = "ACTIVE", // "ACTIVE" | "TP_HIT" | "SL_HIT" | "MANUALLY_CLOSED"
    val winRateProjection: Double,
    val currentPrice: Double,
    val exitPrice: Double? = null,
    val finalPnl: Double? = null,
    val finalPnlPips: Double? = null,
    val smcStructure: String = "",
    val macro10YearPhase: String = "",
    val liquiditySweep: String = "",
    val reasoning: String = "",
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)

@Entity(tableName = "bot_settings")
data class BotSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val autoTradingEnabled: Boolean = true,
    val activeMode: String = "scalper", // "scalper" | "swing"
    val soundEnabled: Boolean = true,
    val minConfidence: Double = 82.50,
    val simulatedBalance: Double = 10000.00,
    val contractLots: Double = 1.00,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "historical_decade_data")
data class HistoricalDecadeEntity(
    @PrimaryKey
    val year: Int,
    val period: String,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closePrice: Double,
    val annualChangePercent: Double,
    val marketRegime: String,
    val keyCatalyst: String,
    val algorithmicWinRate: Double,
    val totalTrades: Int,
    val profitFactor: Double,
    val maxDrawdown: Double,
    val institutionalBias: String
)

@Entity(tableName = "bot_logs")
data class BotLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String, // "INFO" | "SIGNAL" | "TP_HIT" | "SL_HIT" | "REFUSAL" | "BRAIN"
    val message: String
)
