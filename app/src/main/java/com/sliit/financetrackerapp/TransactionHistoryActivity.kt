package com.sliit.financetrackerapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    }

    override fun onResume() {
        super.onResume()

        // ✅ Reload data from SharedPreferences after editing
        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<MutableList<Transaction>>() {}.type
        val updatedList: MutableList<Transaction> = if (json != null) gson.fromJson(json, type) else mutableListOf()

        transactions.clear()
        transactions.addAll(updatedList)
        adapter.notifyDataSetChanged()
    }
}
