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

    private lateinit var tvBalance: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBudgetWarning: TextView

    private lateinit var btnAddTransaction: Button
    private lateinit var btnViewChart: Button
    private lateinit var btnTransactionHistory: Button
    private lateinit var btnSetBudget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind UI elements
        tvBalance = findViewById(R.id.tv_balance)
        tvIncome = findViewById(R.id.tv_income)
        tvExpense = findViewById(R.id.tv_expense)
        tvBudgetWarning = findViewById(R.id.tv_budget_warning)

        btnAddTransaction = findViewById(R.id.btn_add_transaction)
        btnViewChart = findViewById(R.id.btn_view_chart)
        btnTransactionHistory = findViewById(R.id.btn_transaction_history)
        btnSetBudget = findViewById(R.id.btn_set_budget)

        // Navigate to Add Transaction
        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Navigate to Spending Chart
        btnViewChart.setOnClickListener {
            startActivity(Intent(this, ChartActivity::class.java))
        }

        // Navigate to Transaction History
        btnTransactionHistory.setOnClickListener {
            startActivity(Intent(this, TransactionHistoryActivity::class.java))
        }

        // Navigate to Budget Setup
        btnSetBudget.setOnClickListener {
            startActivity(Intent(this, BudgetActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadAndDisplayData()
    }

    private fun loadAndDisplayData() {
        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        val transactionList: List<Transaction> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }

        var income = 0.0
        var expense = 0.0

        for (txn in transactionList) {
            if (txn.type == "Income") {
                income += txn.amount
            } else {
                expense += txn.amount
            }
        }

        val balance = income - expense
        val budget = sharedPreferences.getFloat("monthlyBudget", 0f)

        tvIncome.text = "Income: Rs. %.2f".format(income)
        tvExpense.text = "Expense: Rs. %.2f".format(expense)
        tvBalance.text = "Balance: Rs. %.2f".format(balance)

        // Show budget warning if exceeded
        if (budget > 0f && expense > budget) {
            tvBudgetWarning.text = "⚠️ You have exceeded your budget of Rs. %.2f!".format(budget)
            tvBudgetWarning.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            tvBudgetWarning.visibility = TextView.VISIBLE
        } else {
            tvBudgetWarning.visibility = TextView.GONE
        }
    }
}
