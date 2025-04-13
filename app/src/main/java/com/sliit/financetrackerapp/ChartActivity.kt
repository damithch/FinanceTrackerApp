package com.sliit.financetrackerapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
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

        if (categoryTotals.isEmpty()) {
            pieChart.centerText = "No Expenses Yet"
            pieChart.setCenterTextSize(16f)
            pieChart.setEntryLabelColor(Color.DKGRAY)
            pieChart.invalidate()
            return
        }

        val entries = categoryTotals.map { PieEntry(it.value, it.key) }

        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.colors = getColorPalette()
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.setCenterText("Expenses Breakdown")
        pieChart.setCenterTextSize(18f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.animateY(1000)

        pieChart.invalidate()
    }

    private fun getColorPalette(): List<Int> {
        return listOf(
            Color.parseColor("#FF6F61"), // Food
            Color.parseColor("#6B5B95"), // Transport
            Color.parseColor("#88B04B"), // Bills
            Color.parseColor("#F7CAC9"), // Entertainment
            Color.parseColor("#92A8D1"), // Health
            Color.parseColor("#955251"), // Education
            ColorTemplate.getHoloBlue()
        )
    }
}
