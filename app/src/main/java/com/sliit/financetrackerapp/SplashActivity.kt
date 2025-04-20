package com.sliit.financetrackerapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    companion object {
        /** How long the splash is visible (milliseconds) */
        private const val SPLASH_DELAY: Long = 1500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Full‑screen look
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (isLoggedIn()) MainActivity::class.java
            else LoginActivity::class.java
            startActivity(Intent(this, next))
            finish()                 // don’t return to splash when back is pressed
        }, SPLASH_DELAY)
    }

    /** Reads the shared‑prefs flag set after a successful login */
    private fun isLoggedIn(): Boolean =
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
            .getBoolean("loggedIn", false)
}
