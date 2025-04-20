package com.sliit.financetrackerapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPwd: EditText
    private lateinit var etPwdConfirm: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Edge‑to‑edge padding
        findViewById<View>(R.id.register_root)?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom); insets
            }
        }

        // Bind views
        etName        = findViewById(R.id.et_name)
        etEmail       = findViewById(R.id.et_email)
        etPwd         = findViewById(R.id.et_password)
        etPwdConfirm  = findViewById(R.id.et_password_confirm)
        btnRegister   = findViewById(R.id.btn_register)

        // Button listener
        btnRegister.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val name  = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pwd   = etPwd.text.toString()
        val pwd2  = etPwdConfirm.text.toString()

        // Basic validation
        when {
            name.isEmpty() || email.isEmpty() || pwd.isEmpty() -> {
                toast("Please fill all fields"); return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                toast("Enter a valid email"); return
            }
            pwd.length < 4 -> {
                toast("Password must be at least 4 characters"); return
            }
            pwd != pwd2 -> {
                toast("Passwords do not match"); return
            }
        }

        // Save credentials
        val prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        prefs.edit()
            .putString("name", name)
            .putString("email", email)
            .putString("password", pwd)
            .putBoolean("loggedIn", false)
            .apply()

        toast("Registration successful! Please log in.")
        // Return to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
