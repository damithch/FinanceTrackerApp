package com.sliit.financetrackerapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class  BudgetActivity : AppCompatActivity() {

    private lateinit var etBudgetAmount: EditText
    private lateinit var btnSaveBudget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        etBudgetAmount = findViewById(R.id.et_budget_amount)
        btnSaveBudget = findViewById(R.id.btn_save_budget)

        // Load existing budget if available
        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val existingBudget = sharedPreferences.getFloat("monthlyBudget", 0f)
        if (existingBudget > 0f) {
            etBudgetAmount.setText(existingBudget.toString())
        }

        btnSaveBudget.setOnClickListener {
            val budgetText = etBudgetAmount.text.toString()
            val budget = budgetText.toFloatOrNull()

            if (budget == null || budget <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit().putFloat("monthlyBudget", budget).apply()
            Toast.makeText(this, "Budget saved: Rs. %.2f".format(budget), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
