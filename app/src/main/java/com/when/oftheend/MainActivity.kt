
package com.when.oftheend

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("when_end", MODE_PRIVATE)
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerText: TextView
    private lateinit var statusText: TextView

    private val limit = 48L * 60 * 60 * 1000

    private fun lastCheckIn(): Long =
        prefs.getLong("last_checkin", 0L)

    private val ticker = object : Runnable {
        override fun run() {
            updateTimer()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (lastCheckIn() == 0L) {
            prefs.edit()
                .putLong("last_checkin", System.currentTimeMillis())
                .apply()
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "WHEN OF THE END"
            textSize = 26f
            gravity = Gravity.CENTER
        }

        timerText = TextView(this).apply {
            textSize = 36f
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
        }

        val checkInButton = Button(this).apply {
            text = "GUA MASIH HIDUP"
            setOnClickListener {
                prefs.edit()
                    .putLong(
                        "last_checkin",
                        System.currentTimeMillis()
                    )
                    .apply()
                updateTimer()
            }
        }

        layout.addView(title)
        layout.addView(timerText)
        layout.addView(statusText)
        layout.addView(checkInButton)

        setContentView(layout)
    }

    private fun updateTimer() {
        val remaining =
            (lastCheckIn() + limit - System.currentTimeMillis())
                .coerceAtLeast(0L)

        val h = remaining / 3_600_000
        val m = (remaining / 60_000) % 60
        val s = (remaining / 1000) % 60

        timerText.text = "%02d:%02d:%02d".format(h, m, s)

        statusText.text = if (remaining == 0L) {
            "BATAS WAKTU TERLEWAT - PERLU PEMERIKSAAN"
        } else {
            "Check-in terakhir tersimpan"
        }
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    override fun onPause() {
        handler.removeCallbacks(ticker)
        super.onPause()
    }
}
