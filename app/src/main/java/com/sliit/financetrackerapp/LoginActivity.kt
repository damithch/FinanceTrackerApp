package com.sliit.financetrackerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Edge‑to‑edge
        findViewById<View>(R.id.login_root)?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
                insets
            }
        }

        // ── Bind views ───────────────────────────────────────
        etEmail    = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin   = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)

        // ── Login button logic ──────────────────────────────
        btnLogin.setOnClickListener { attemptLogin() }

        // ── "Register here" link ────────────────────────────
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val pwd   = etPassword.text.toString()

        if (email.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val savedPwd   = prefs.getString("password", null)

        if (email == savedEmail && pwd == savedPwd) {
            prefs.edit().putBoolean("loggedIn", true).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish() // close LoginActivity
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }
}
