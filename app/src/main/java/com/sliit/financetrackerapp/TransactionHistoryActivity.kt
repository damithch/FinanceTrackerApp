package com.sliit.financetrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var transactions: MutableList<Transaction>
    private lateinit var allTransactions: MutableList<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        allTransactions = if (json != null) gson.fromJson(json, type) else mutableListOf()
        transactions = allTransactions.toMutableList()

        adapter = TransactionAdapter(transactions,
            onDelete = { index ->
                val txnToRemove = transactions[index]
                allTransactions.remove(txnToRemove)

                sharedPreferences.edit()
                    .putString("transactions", gson.toJson(allTransactions)).apply()

                transactions.removeAt(index)
                adapter.notifyItemRemoved(index)
            },
            onEdit = { index, txnType ->
                val txn = transactions[index]
                val intent = if (txnType == "Income") {
                    Intent(this, AddIncomeActivity::class.java)
                } else {
                    Intent(this, AddTransactionActivity::class.java)
                }
                intent.putExtra("editIndex", index)
                intent.putExtra("editTransaction", gson.toJson(txn))
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        // Export
        findViewById<Button>(R.id.btn_export_json).setOnClickListener { exportToJson() }
        findViewById<Button>(R.id.btn_export_txt).setOnClickListener { exportToTxt() }

        // Filter
        findViewById<Button>(R.id.btn_filter_income).setOnClickListener {
            transactions.clear()
            transactions.addAll(allTransactions.filter { it.type == "Income" })
            adapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_filter_expense).setOnClickListener {
            transactions.clear()
            transactions.addAll(allTransactions.filter { it.type == "Expense" })
            adapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btn_filter_all).setOnClickListener {
            transactions.clear()
            transactions.addAll(allTransactions)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        allTransactions = if (json != null) gson.fromJson(json, type) else mutableListOf()

        transactions.clear()
        transactions.addAll(allTransactions)
        adapter.notifyDataSetChanged()
    }

    private fun exportToJson() {
        val gson = Gson()
        val jsonString = gson.toJson(allTransactions)

        try {
            val file = File(filesDir, "transactions_backup.json")
            file.writeText(jsonString)
            Toast.makeText(this, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportToTxt() {
        try {
            val file = File(filesDir, "transactions_backup.txt")
            val content = StringBuilder()

            for (txn in allTransactions) {
                content.append("Title: ${txn.title}\n")
                content.append("Amount: Rs. ${txn.amount}\n")
                content.append("Category: ${txn.category}\n")
                content.append("Date: ${txn.date}\n")
                content.append("Type: ${txn.type}\n")
                content.append("-----------------------------\n")
            }

            file.writeText(content.toString())
            Toast.makeText(this, "TXT Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
