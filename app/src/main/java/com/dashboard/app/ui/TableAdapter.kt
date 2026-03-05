package com.dashboard.app.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dashboard.app.R
import com.dashboard.app.model.TableRow

class TableAdapter : ListAdapter<TableRow, TableAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView     = view.findViewById(R.id.cellId)
        val name: TextView   = view.findViewById(R.id.cellName)
        val value: TextView  = view.findViewById(R.id.cellValue)
        val status: TextView = view.findViewById(R.id.cellStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_table_row, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val row = getItem(pos)
        h.id.text    = row.id
        h.name.text  = row.name
        h.value.text = row.value.toString()
        h.status.text = row.status.uppercase()

        val (bg, fg) = when (row.status) {
            "ok"   -> "#1a3a30" to "#00f5c4"
            "warn" -> "#3a3010" to "#ffd166"
            else   -> "#3a1020" to "#ff4d6d"
        }
        h.status.setBackgroundColor(Color.parseColor(bg))
        h.status.setTextColor(Color.parseColor(fg))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TableRow>() {
            override fun areItemsTheSame(a: TableRow, b: TableRow) = a.id == b.id
            override fun areContentsTheSame(a: TableRow, b: TableRow) = a == b
        }
    }
}
