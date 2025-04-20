package com.sliit.financetrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    // ── Summary TextViews ─────────────────────────────────────
    private lateinit var tvBalance: TextView
    private lateinit var tvIncome:  TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBudgetWarning: TextView

    // ── Action Buttons ───────────────────────────────────────
    private lateinit var btnAddIncome: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnViewChart: Button
    private lateinit var btnTransactionHistory: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /* Edge‑to‑edge padding:  use the real root id -> @+id/main */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Bind views
        tvBalance       = findViewById(R.id.tv_balance)
        tvIncome        = findViewById(R.id.tv_income)
        tvExpense       = findViewById(R.id.tv_expense)
        tvBudgetWarning = findViewById(R.id.tv_budget_warning)

        btnAddIncome           = findViewById(R.id.btn_add_income)
        btnAddExpense          = findViewById(R.id.btn_add_expense)
        btnViewChart           = findViewById(R.id.btn_view_chart)
        btnTransactionHistory  = findViewById(R.id.btn_transaction_history)
        btnSetBudget           = findViewById(R.id.btn_set_budget)
        btnLogout              = findViewById(R.id.btn_logout)

        // Navigation
        btnAddIncome.setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
        }
        btnAddExpense.setOnClickListener {
            startActivity(
                Intent(this, AddTransactionActivity::class.java)
                    .putExtra("transactionType", "Expense")
            )
        }
        btnViewChart.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }
        btnTransactionHistory.setOnClickListener {
            startActivity(Intent(this, TransactionHistoryActivity::class.java))
        }
        btnSetBudget.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }

        btnLogout.setOnClickListener { doLogout() }
    }

    override fun onResume() {
        super.onResume()
        loadAndDisplayData()
    }

    /** Reads totals from SharedPreferences and updates the dashboard. */
    private fun loadAndDisplayData() {
        val prefs = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson  = Gson()
        val json  = prefs.getString("transactions", null)
        val list: List<Transaction> = if (json != null) {
            gson.fromJson(json, object : TypeToken<List<Transaction>>() {}.type)
        } else emptyList()

        var income = 0.0
        var expense = 0.0
        list.forEach { if (it.type == "Income") income += it.amount else expense += it.amount }

        val balance = income - expense
        val budget  = prefs.getFloat("monthlyBudget", 0f)

        tvIncome.text  = "Income: Rs. %.2f".format(income)
        tvExpense.text = "Expense: Rs. %.2f".format(expense)
        tvBalance.text = "Balance: Rs. %.2f".format(balance)

        if (budget > 0f && expense > budget) {
            tvBudgetWarning.apply {
                text = "⚠️ You have exceeded your budget of Rs. %.2f!".format(budget)
                setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                visibility = TextView.VISIBLE
            }
        } else {
            tvBudgetWarning.visibility = TextView.GONE
        }
    }

    /** Logs the user out and returns to LoginActivity. */
    private fun doLogout() {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
            .edit().putBoolean("loggedIn", false).apply()

        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
        finish()
    }
}
