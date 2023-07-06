package com.example.listbuyapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class listAdapter(private val nombres: List<String>) : RecyclerView.Adapter<listAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val nombre = nombres[position]
        holder.bind(nombre)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, itemsListActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return nombres.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listNombre: TextView = itemView.findViewById(R.id.textNameList)

        fun bind(nombre: String) {
            listNombre.text = nombre
        }
    }


}
