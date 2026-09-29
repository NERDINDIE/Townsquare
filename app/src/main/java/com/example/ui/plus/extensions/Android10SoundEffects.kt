package com.example.ui.plus.extensions

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Android10SoundEffects {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 80)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    /**
     * Classic Android 1.0 Trackball Click Beep
     */
    fun playTrackballClick() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
            } catch (e: Exception) {
                // Ignore audio hardware exception
            }
        }
    }

    /**
     * Retro App Launch Chime
     */
    fun playAppLaunchSound() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 45)
            } catch (e: Exception) {
                // Ignore audio hardware exception
            }
        }
    }

    /**
     * Pull-Down Notifications Shade Slide Sound
     */
    fun playShadeSlide() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 35)
                delay(40)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_8, 35)
            } catch (e: Exception) {
                // Ignore audio hardware exception
            }
        }
    }

    /**
     * Iconic Android 1.0 Ascending Notification Chime
     */
    fun playNotificationChime() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 60)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_6, 60)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 90)
            } catch (e: Exception) {
                // Ignore audio hardware exception
            }
        }
    }

    /**
     * Retro 14.4k Dial-up Modem Handshake Sound
     */
    fun playModemDialupSound() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 100)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 100)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 150)
                delay(180)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 400)
            } catch (e: Exception) {
                // Ignore audio hardware exception
            }
        }
    }
}
