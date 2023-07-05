package com.example.listbuyapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class itemsAdapter(private val nombres: List<String>) : RecyclerView.Adapter<itemsAdapter.NombreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NombreViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_layout, parent, false)
        return NombreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NombreViewHolder, position: Int) {
        val nombre = nombres[position]
        holder.bind(nombre)
    }

    override fun getItemCount(): Int {
        return nombres.size
    }

    inner class NombreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNombre: TextView = itemView.findViewById(R.id.textNameItem)

        fun bind(nombre: String) {
            itemNombre.text = nombre
        }
    }


}