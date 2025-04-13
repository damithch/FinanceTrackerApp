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
    private lateinit var etDate: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var rgType: RadioGroup
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var btnSave: Button

    private var editIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        etTitle = findViewById(R.id.et_title)
        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        spinnerCategory = findViewById(R.id.spinner_category)
        rgType = findViewById(R.id.rg_type)
        rbIncome = findViewById(R.id.rb_income)
        rbExpense = findViewById(R.id.rb_expense)
        btnSave = findViewById(R.id.btn_save)

        // Set up spinner with predefined categories
        val categories = listOf("Food", "Transport", "Bills", "Entertainment", "Health", "Educational")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val typeToken = object : TypeToken<MutableList<Transaction>>() {}.type
        val existingJson = sharedPreferences.getString("transactions", null)
        val transactionList: MutableList<Transaction> = if (existingJson != null)
            gson.fromJson(existingJson, typeToken)
        else mutableListOf()

        // If editing
        editIndex = intent.getIntExtra("editIndex", -1)
        val editJson = intent.getStringExtra("editTransaction")
        if (editIndex != -1 && editJson != null) {
            val transaction = gson.fromJson(editJson, Transaction::class.java)
            etTitle.setText(transaction.title)
            etAmount.setText(transaction.amount.toString())
            etDate.setText(transaction.date)

            val spinnerPosition = categories.indexOf(transaction.category)
            if (spinnerPosition != -1) {
                spinnerCategory.setSelection(spinnerPosition)
            }

            if (transaction.type == "Income") rbIncome.isChecked = true else rbExpense.isChecked = true
            btnSave.text = "Update Transaction"
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem.toString()
            val date = etDate.text.toString()
            val type = if (rbIncome.isChecked) "Income" else "Expense"

            if (title.isEmpty() || amount == null || date.isEmpty()) {
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
