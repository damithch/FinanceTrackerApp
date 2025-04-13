package com.sliit.financetrackerapp

data class Transaction(
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val type: String // "Income" or "Expense"
)
