package com.example.data.model

data class MarketTicker(
    val symbol: String = "PAXG/USD (XAU)",
    val assetName: String = "Tokenized Physical Gold (1:1 Pegged to XAU/USD)",
    val price: Double = 4338.50,
    val bid: Double = 4338.30,
    val ask: Double = 4338.70,
    val spread: Double = 0.40,
    val change24h: Double = 34.50,
    val change24hPercent: Double = 0.81,
    val high24h: Double = 4369.50,
    val low24h: Double = 4296.50,
    val volume24h: Double = 184500.0,
    val source: String = "Kraken Live PAXG / Vault Peg",
    val latencyMs: Long = 14,
    val timestamp: Long = System.currentTimeMillis(),
    val points: Double = 4338.50,
    val pips: Double = 43385.0
)

data class Candle(
    val time: Long, // timestamp in seconds
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

data class BrainAnalysis(
    val timestamp: Long,
    val asset: String,
    val currentPrice: Double,
    val recommendedMode: String, // "scalper" | "swing"
    val recommendedDirection: String, // "BUY" | "SELL"
    val winRateProjection: Double, // e.g. 86.4
    val riskRewardRatio: String, // "1:3" or "1:5"
    val riskDollars: Double, // 5.0 or 20.0
    val rewardDollars: Double, // 15.0 or 100.0
    val riskPips: Double, // 50.0 or 200.0
    val rewardPips: Double, // 150.0 or 1000.0
    val entryPrice: Double,
    val takeProfitPrice: Double,
    val stopLossPrice: Double,
    val macro10YearPhase: String,
    val smcStructure: String,
    val liquiditySweep: String,
    val fairValueGap: String,
    val orderBlockZone: String,
    val orderFlowDelta: String,
    val volatilityAtr: Double,
    val confluenceFactors: List<String>,
    val algorithmConviction: String,
    val reasoning: String
)

data class MacroSummary(
    val timeframe: String = "10-Year Historical Price Action (2014 - 2026)",
    val totalTradesAnalyzed: Int = 4065,
    val overallDecadeWinRate: Double = 84.4,
    val decadeLow: Double = 1046.20,
    val decadeHigh: Double = 4369.27,
    val currentLivePrice: Double = 4338.50,
    val currentPegAsset: String = "PAXG / USD (Tokenized Real Gold)",
    val decadeGainPercent: Double = 314.7,
    val scalper10YearWinRate: Double = 83.8,
    val swing10YearWinRate: Double = 85.6,
    val sharpeRatio: Double = 2.84,
    val profitFactor: Double = 3.84
)
