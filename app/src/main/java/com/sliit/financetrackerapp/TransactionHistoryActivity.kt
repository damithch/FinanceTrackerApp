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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        transactions = if (json != null) gson.fromJson(json, type) else mutableListOf()

        adapter = TransactionAdapter(transactions,
            onDelete = { index ->
                transactions.removeAt(index)
                sharedPreferences.edit().putString("transactions", gson.toJson(transactions)).apply()
                adapter.notifyItemRemoved(index)
            },
            onEdit = { index ->
                val intent = Intent(this, AddTransactionActivity::class.java)
                intent.putExtra("editIndex", index)
                intent.putExtra("editTransaction", gson.toJson(transactions[index]))
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        // ✅ Export JSON
        val btnExportJson = findViewById<Button>(R.id.btn_export_json)
        btnExportJson.setOnClickListener {
            exportToJson()
        }

        // ✅ Export TXT
        val btnExportTxt = findViewById<Button>(R.id.btn_export_txt)
        btnExportTxt.setOnClickListener {
            exportToTxt()
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        val updatedList: MutableList<Transaction> = if (json != null) gson.fromJson(json, type) else mutableListOf()

        transactions.clear()
        transactions.addAll(updatedList)
        adapter.notifyDataSetChanged()
    }

    private fun exportToJson() {
        val gson = Gson()
        val jsonString = gson.toJson(transactions)

        try {
            val fileName = "transactions_backup.json"
            val file = File(filesDir, fileName)
            file.writeText(jsonString)

            Toast.makeText(this, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun exportToTxt() {
        try {
            val fileName = "transactions_backup.txt"
            val file = File(filesDir, fileName)

            val content = StringBuilder()
            for (txn in transactions) {
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
            e.printStackTrace()
        }
    }
}
