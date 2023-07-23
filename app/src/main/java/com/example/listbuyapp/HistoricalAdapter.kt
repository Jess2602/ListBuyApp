package com.example.listbuyapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listbuyapp.data.History
import com.example.listbuyapp.databinding.HistoricalFragmentLayoutBinding


class HistoricalAdapter(var historicalList: List<History>) :
    RecyclerView.Adapter<HistoricalAdapter.HistoricalViewHolder>() {

    class HistoricalViewHolder(val binding: HistoricalFragmentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricalAdapter.HistoricalViewHolder {
        val binding = HistoricalFragmentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoricalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historicalList.size
    }

    override fun onBindViewHolder(holder: HistoricalAdapter.HistoricalViewHolder, position: Int) {
        val currentHistoricalList = historicalList[position]
        val binding = holder.binding

        binding.textNameListHistorical.text = currentHistoricalList.listName_history
        binding.textViewCategoryHistorical.text = currentHistoricalList.listCategory_history
        binding.textViewDateHistorical.text = currentHistoricalList.listDate_history
    }

    fun setData(history: List<History>) {
        this.historicalList = history
        notifyDataSetChanged()
    }
}