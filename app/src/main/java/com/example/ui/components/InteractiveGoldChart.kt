package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Candle
import com.example.data.model.SignalEntity
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGold
import com.example.ui.theme.CardDark
import com.example.ui.theme.CardDarkElevated
import com.example.ui.theme.CyanEntry
import com.example.ui.theme.EmeraldProfit
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RoseRisk
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun InteractiveGoldChart(
    candles: List<Candle>,
    selectedTimeframe: String,
    onTimeframeSelected: (String) -> Unit,
    activeTrade: SignalEntity?,
    livePrice: Double,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeframes = listOf("1m", "5m", "15m", "1h", "4h", "1D", "1W", "10Y")
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var selectedCandle by remember { mutableStateOf<Candle?>(null) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .testTag("interactive_chart_card"),
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar: Asset, Live Price, Timeframes, Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDarkElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Asset & Live Price
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(EmeraldProfit, CircleShape)
                    )
                    Text(
                        text = "XAU/USD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Text(
                        text = "$${"%.2f".format(livePrice)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Timeframe Selectors
                Row(
                    modifier = Modifier
                        .background(BgDark, RoundedCornerShape(8.dp))
                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    timeframes.forEach { tf ->
                        val isSelected = tf == selectedTimeframe
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) GoldPrimary else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .pointerInput(tf) {
                                    detectTapGestures { onTimeframeSelected(tf) }
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BgDark else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(3f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.6f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(28.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = GoldPrimary,
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh Chart",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Candle Hover / Inspect Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDark)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (selectedCandle != null) {
                    val c = selectedCandle!!
                    val sdf = SimpleDateFormat(if (selectedTimeframe == "10Y" || selectedTimeframe == "1D" || selectedTimeframe == "1W") "yyyy-MM" else "HH:mm", Locale.getDefault())
                    val dateStr = sdf.format(Date(c.time * 1000))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("T: $dateStr", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                        Text("O: $${"%.1f".format(c.open)}", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontSize = 9.sp)
                        Text("H: $${"%.1f".format(c.high)}", style = MaterialTheme.typography.labelSmall, color = EmeraldProfit, fontSize = 9.sp)
                        Text("L: $${"%.1f".format(c.low)}", style = MaterialTheme.typography.labelSmall, color = RoseRisk, fontSize = 9.sp)
                        Text("C: $${"%.1f".format(c.close)}", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontSize = 9.sp)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1 Pt = $1.00", style = MaterialTheme.typography.labelSmall, color = GoldPrimary, fontSize = 9.sp)
                        Text("1 Pip = $0.10", style = MaterialTheme.typography.labelSmall, color = GoldPrimary, fontSize = 9.sp)
                        Text("Touch chart to inspect OHLC order flow", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 9.sp)
                    }
                }

                if (activeTrade != null) {
                    Text(
                        text = "TP $${"%.2f".format(activeTrade.takeProfitPrice)} | SL $${"%.2f".format(activeTrade.stopLossPrice)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 9.sp
                    )
                }
            }

            // Main Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BgDark)
                    .pointerInput(candles, zoomLevel) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                touchOffset = change.position
                            },
                            onDragEnd = { touchOffset = null; selectedCandle = null },
                            onDragCancel = { touchOffset = null; selectedCandle = null }
                        )
                    }
                    .pointerInput(candles, zoomLevel) {
                        detectTapGestures(
                            onPress = { offset ->
                                touchOffset = offset
                                tryAwaitRelease()
                                touchOffset = null
                                selectedCandle = null
                            }
                        )
                    }
            ) {
                if (candles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading Gold market order flow...", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCandleChart(
                            candles = candles,
                            zoomLevel = zoomLevel,
                            activeTrade = activeTrade,
                            livePrice = livePrice,
                            touchOffset = touchOffset,
                            timeframe = selectedTimeframe,
                            onCandleHovered = { c -> selectedCandle = c }
                        )
                    }
                }

                // Watermark & Legend
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 4.dp)
                        .background(CardDark.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(6.dp, 2.dp).background(CyanEntry))
                        Text("Entry", fontSize = 8.sp, color = CyanEntry, fontFamily = FontFamily.Monospace)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(6.dp, 2.dp).background(EmeraldProfit))
                        Text("TP Target", fontSize = 8.sp, color = EmeraldProfit, fontFamily = FontFamily.Monospace)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(modifier = Modifier.size(6.dp, 2.dp).background(RoseRisk))
                        Text("SL Stop", fontSize = 8.sp, color = RoseRisk, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCandleChart(
    candles: List<Candle>,
    zoomLevel: Float,
    activeTrade: SignalEntity?,
    livePrice: Double,
    touchOffset: Offset?,
    timeframe: String,
    onCandleHovered: (Candle?) -> Unit
) {
    val topMargin = 20f
    val bottomMargin = 28f
    val leftMargin = 10f
    val rightMargin = 70f

    val chartWidth = size.width - leftMargin - rightMargin
    val chartHeight = size.height - topMargin - bottomMargin

    val visibleCount = (min(candles.size, (50 / zoomLevel).toInt())).coerceIn(15, candles.size)
    val visibleCandles = candles.takeLast(visibleCount)

    var minP = visibleCandles.minOfOrNull { it.low } ?: 4300.0
    var maxP = visibleCandles.maxOfOrNull { it.high } ?: 4350.0

    if (livePrice > 0) {
        minP = min(minP, livePrice)
        maxP = max(maxP, livePrice)
    }

    if (activeTrade != null) {
        minP = min(minP, min(activeTrade.stopLossPrice, activeTrade.takeProfitPrice))
        maxP = max(maxP, max(activeTrade.stopLossPrice, activeTrade.takeProfitPrice))
    }

    val padding = (maxP - minP) * 0.12 + 2.0
    val displayMinPrice = minP - padding
    val displayMaxPrice = maxP + padding
    val priceRange = max(displayMaxPrice - displayMinPrice, 1.0)

    fun getY(price: Double): Float {
        return (topMargin + chartHeight - ((price - displayMinPrice) / priceRange) * chartHeight).toFloat()
    }

    fun getPrice(y: Float): Double {
        return displayMinPrice + ((topMargin + chartHeight - y) / chartHeight) * priceRange
    }

    val candleWidth = chartWidth / visibleCandles.size
    fun getX(index: Int): Float {
        return leftMargin + index * candleWidth + candleWidth / 2f
    }

    // 1. Grid lines and Price Axis labels
    val priceSteps = 4
    for (i in 0..priceSteps) {
        val p = displayMinPrice + (priceRange / priceSteps) * i
        val y = getY(p)
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(leftMargin, y),
            end = Offset(leftMargin + chartWidth, y),
            strokeWidth = 1f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "$${"%.1f".format(p)}",
            leftMargin + chartWidth + 6f,
            y + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.argb(180, 148, 163, 184)
                textSize = 20f
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        )
    }

    // 2. Volume Bars
    val maxVol = visibleCandles.maxOfOrNull { it.volume } ?: 1.0
    val volHeight = chartHeight * 0.18f
    visibleCandles.forEachIndexed { idx, c ->
        val x = getX(idx)
        val isUp = c.close >= c.open
        val barH = (c.volume / maxVol * volHeight).toFloat()
        drawRect(
            color = if (isUp) EmeraldProfit.copy(alpha = 0.15f) else RoseRisk.copy(alpha = 0.15f),
            topLeft = Offset(x - candleWidth * 0.35f, topMargin + chartHeight - barH),
            size = Size(candleWidth * 0.7f, barH)
        )
    }

    // 3. Candlesticks
    visibleCandles.forEachIndexed { idx, c ->
        val x = getX(idx)
        val isUp = c.close >= c.open
        val color = if (isUp) EmeraldProfit else RoseRisk

        val openY = getY(c.open)
        val closeY = getY(c.close)
        val highY = getY(c.high)
        val lowY = getY(c.low)

        // Wick
        drawLine(
            color = color,
            start = Offset(x, highY),
            end = Offset(x, lowY),
            strokeWidth = 2f
        )

        // Body
        val bodyY = min(openY, closeY)
        val bodyH = max(abs(openY - closeY), 2.5f)
        val bodyW = max(candleWidth * 0.65f, 3f)

        drawRect(
            color = color,
            topLeft = Offset(x - bodyW / 2f, bodyY),
            size = Size(bodyW, bodyH)
        )
    }

    // 4. DRAW TAKE PROFIT (TP), ENTRY, AND STOP LOSS (SL) LINES
    if (activeTrade != null) {
        val entryY = getY(activeTrade.entryPrice)
        val tpY = getY(activeTrade.takeProfitPrice)
        val slY = getY(activeTrade.stopLossPrice)

        // Profit target zone fill
        val zoneTop = min(entryY, tpY)
        val zoneHeight = abs(entryY - tpY)
        drawRect(
            color = if (activeTrade.direction == "BUY") EmeraldProfit.copy(alpha = 0.06f) else RoseRisk.copy(alpha = 0.06f),
            topLeft = Offset(leftMargin, zoneTop),
            size = Size(chartWidth, zoneHeight)
        )

        // TP Line (Emerald dashed)
        drawLine(
            color = EmeraldProfit,
            start = Offset(leftMargin, tpY),
            end = Offset(leftMargin + chartWidth, tpY),
            strokeWidth = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )
        drawContext.canvas.nativeCanvas.drawText(
            "TP $${"%.2f".format(activeTrade.takeProfitPrice)}",
            leftMargin + chartWidth + 6f,
            tpY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 16, 185, 129)
                textSize = 22f
                isFakeBoldText = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        )

        // SL Line (Rose dashed)
        drawLine(
            color = RoseRisk,
            start = Offset(leftMargin, slY),
            end = Offset(leftMargin + chartWidth, slY),
            strokeWidth = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
        )
        drawContext.canvas.nativeCanvas.drawText(
            "SL $${"%.2f".format(activeTrade.stopLossPrice)}",
            leftMargin + chartWidth + 6f,
            slY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 244, 63, 94)
                textSize = 22f
                isFakeBoldText = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        )

        // Entry Line (Cyan solid)
        drawLine(
            color = CyanEntry,
            start = Offset(leftMargin, entryY),
            end = Offset(leftMargin + chartWidth, entryY),
            strokeWidth = 2.5f
        )
        drawContext.canvas.nativeCanvas.drawText(
            "ENTRY $${"%.2f".format(activeTrade.entryPrice)}",
            leftMargin + chartWidth + 6f,
            entryY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 6, 182, 212)
                textSize = 20f
                isFakeBoldText = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        )
    }

    // 5. Live Price Cursor & Line (Gold Amber)
    if (livePrice > 0) {
        val liveY = getY(livePrice)
        drawLine(
            color = GoldPrimary.copy(alpha = 0.8f),
            start = Offset(leftMargin, liveY),
            end = Offset(leftMargin + chartWidth, liveY),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
        )
        drawCircle(
            color = GoldPrimary,
            radius = 4f,
            center = Offset(leftMargin + chartWidth, liveY)
        )
        drawContext.canvas.nativeCanvas.drawText(
            "$${"%.2f".format(livePrice)}",
            leftMargin + chartWidth + 6f,
            liveY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 245, 158, 11)
                textSize = 22f
                isFakeBoldText = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        )
    }

    // 6. Interactive Crosshair on Touch
    if (touchOffset != null) {
        val tx = touchOffset.x.coerceIn(leftMargin, leftMargin + chartWidth)
        val ty = touchOffset.y.coerceIn(topMargin, topMargin + chartHeight)

        // Vertical line
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(tx, topMargin),
            end = Offset(tx, topMargin + chartHeight),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
        )

        // Horizontal line
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(leftMargin, ty),
            end = Offset(leftMargin + chartWidth, ty),
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
        )

        val idx = ((tx - leftMargin) / candleWidth).toInt().coerceIn(0, visibleCandles.size - 1)
        onCandleHovered(visibleCandles.getOrNull(idx))
    }
}
