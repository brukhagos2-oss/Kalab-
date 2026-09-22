package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * High-performance Web-Audio style synthesized sound generator for trading terminal alerts.
 * Generates clean audio tones using Android AudioTrack without requiring any external audio assets.
 */
class SoundEffects {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    /**
     * Take Profit (TP Hit) Sound: Joyful ascending chime
     */
    fun playTpHit() {
        if (!isEnabled) return
        scope.launch {
            try {
                // Ascending sequence: D5 (587Hz) -> A5 (880Hz) -> D6 (1175Hz) -> F#6 (1480Hz)
                val sampleRate = 44100
                val durationMs = 600
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)

                    // Frequency ramps
                    val freq1 = 587.0 + progress * 600.0
                    val freq2 = 880.0 + progress * 600.0

                    // Envelope: fast attack, exponential decay
                    val envelope = if (progress < 0.08) {
                        progress / 0.08
                    } else {
                        (1.0 - progress).coerceAtLeast(0.0)
                    }

                    val sample1 = sin(2 * PI * freq1 * t)
                    val sample2 = sin(2 * PI * freq2 * t) * 0.5
                    val mixed = (sample1 + sample2) * envelope * 0.4
                    buffer[i] = (mixed * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Ignore audio failure
            }
        }
    }

    /**
     * Stop Loss (SL Hit) Sound: Descending warning tone
     */
    fun playSlHit() {
        if (!isEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 450
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)

                    // Descending sawtooth/frequency from 340Hz down to 160Hz
                    val freq = 340.0 - progress * 180.0
                    val envelope = if (progress < 0.05) progress / 0.05 else (1.0 - progress).coerceAtLeast(0.0)

                    // Sawtooth shape
                    val phase = (freq * t) % 1.0
                    val sample = (2.0 * phase - 1.0) * envelope * 0.35
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Ignore audio failure
            }
        }
    }

    /**
     * New Signal Generated Sound: High-tech radar chirp
     */
    fun playNewSignal() {
        if (!isEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 280
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)

                    // Chirp: 700Hz -> 1300Hz
                    val freq = 700.0 + progress * 600.0
                    val envelope = if (progress < 0.08) progress / 0.08 else (1.0 - progress).coerceAtLeast(0.0)

                    val sample = sin(2 * PI * freq * t) * envelope * 0.35
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Ignore audio failure
            }
        }
    }

    /**
     * Anti-Overlap Refusal Sound: Sharp double reject buzz
     */
    fun playRefusalSound() {
        if (!isEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 280
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)

                    // Double pulsed square wave around 180Hz
                    val freq = if (progress < 0.5) 190.0 else 150.0
                    val pulseEnvelope = if ((progress in 0.0..0.4) || (progress in 0.55..0.95)) 1.0 else 0.0

                    val sample = (if (sin(2 * PI * freq * t) > 0) 0.3 else -0.3) * pulseEnvelope
                    buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Ignore audio failure
            }
        }
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
    }
}
