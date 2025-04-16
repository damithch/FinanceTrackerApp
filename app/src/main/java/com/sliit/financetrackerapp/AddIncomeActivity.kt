package com.sliit.financetrackerapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)

        etAmount = findViewById(R.id.et_income_amount)
        btnSave = findViewById(R.id.btn_save_income)

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()

            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString("transactions", null)
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val transactionList: MutableList<Transaction> = if (json != null) {
                gson.fromJson(json, type)
            } else {
                mutableListOf()
            }

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val transaction = Transaction(
                title = "Income",
                amount = amount,
                category = "Income",
                date = date,
                type = "Income"
            )

            transactionList.add(transaction)
            sharedPreferences.edit().putString("transactions", gson.toJson(transactionList)).apply()

            Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
