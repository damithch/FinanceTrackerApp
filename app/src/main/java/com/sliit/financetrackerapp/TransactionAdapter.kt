package com.sliit.financetrackerapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactionList: MutableList<Transaction>,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (Int, String) -> Unit // 🔁 Added type parameter
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_details)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.tvTitle.text = transaction.title
        holder.tvDetails.text = "${transaction.category} | Rs.${transaction.amount} | ${transaction.date}"

        // 🟢 Set color for Income / 🔴 for Expense
        if (transaction.type == "Income") {
            holder.tvTitle.setTextColor(Color.parseColor("#4CAF50")) // green
        } else {
            holder.tvTitle.setTextColor(Color.parseColor("#F44336")) // red
        }

        // 🛠 Edit click with type info
        holder.btnEdit.setOnClickListener {
            onEdit(position, transaction.type)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = transactionList.size
}
