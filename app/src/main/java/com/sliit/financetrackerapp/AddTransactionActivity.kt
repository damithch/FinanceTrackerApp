package com.sliit.financetrackerapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var btnSave: Button

    private var editIndex: Int = -1 // to track if we're editing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        etTitle = findViewById(R.id.et_title)
        etAmount = findViewById(R.id.et_amount)
        etCategory = findViewById(R.id.et_category)
        etDate = findViewById(R.id.et_date)
        rgType = findViewById(R.id.rg_type)
        rbIncome = findViewById(R.id.rb_income)
        rbExpense = findViewById(R.id.rb_expense)
        btnSave = findViewById(R.id.btn_save)

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val typeToken = object : TypeToken<MutableList<Transaction>>() {}.type
        val existingJson = sharedPreferences.getString("transactions", null)
        val transactionList: MutableList<Transaction> = if (existingJson != null)
            gson.fromJson(existingJson, typeToken)
        else mutableListOf()

        // 🟡 Check if we're editing an existing transaction
        editIndex = intent.getIntExtra("editIndex", -1)
        val editJson = intent.getStringExtra("editTransaction")
        if (editIndex != -1 && editJson != null) {
            val transaction = gson.fromJson(editJson, Transaction::class.java)
            etTitle.setText(transaction.title)
            etAmount.setText(transaction.amount.toString())
            etCategory.setText(transaction.category)
            etDate.setText(transaction.date)
            if (transaction.type == "Income") rbIncome.isChecked = true else rbExpense.isChecked = true
            btnSave.text = "Update Transaction"
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull()
            val category = etCategory.text.toString()
            val date = etDate.text.toString()
            val type = if (rbIncome.isChecked) "Income" else "Expense"

            if (title.isEmpty() || amount == null || category.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newTransaction = Transaction(title, amount, category, date, type)

            if (editIndex != -1) {
                transactionList[editIndex] = newTransaction
                Toast.makeText(this, "Transaction updated!", Toast.LENGTH_SHORT).show()
            } else {
                transactionList.add(newTransaction)
                Toast.makeText(this, "$type saved successfully!", Toast.LENGTH_SHORT).show()
            }

            val updatedJson = gson.toJson(transactionList)
            sharedPreferences.edit().putString("transactions", updatedJson).apply()
            finish()
        }
    }
}
