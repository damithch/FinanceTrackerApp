package com.sliit.financetrackerapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChartActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        pieChart = findViewById(R.id.pieChart)

        val sharedPreferences = getSharedPreferences("FinanceData", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("transactions", null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        val transactions: List<Transaction> = gson.fromJson(json, type) ?: emptyList()

        val categoryTotals = mutableMapOf<String, Float>()
        for (txn in transactions) {
            if (txn.type == "Expense") {
                categoryTotals[txn.category] = (categoryTotals[txn.category] ?: 0f) + txn.amount.toFloat()
            }
        }

        val entries = categoryTotals.map { PieEntry(it.value, it.key) }
        val dataSet = PieDataSet(entries, "Expenses by Category")
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }
}
