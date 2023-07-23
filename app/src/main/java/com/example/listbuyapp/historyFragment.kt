package com.example.listbuyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.data.History
import com.example.listbuyapp.data.HistoryViewModel
import com.example.listbuyapp.databinding.FragmentHistoryBinding

class historyFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historicalViewModel: HistoryViewModel
    private lateinit var list: ArrayList<History>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        list = ArrayList()

        //recyclerview
        val adapter = HistoricalAdapter(list)
        val recyclerView = binding.historicalRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //userViewModel

        historicalViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        historicalViewModel.readAllData.observe(viewLifecycleOwner, Observer { history ->

            adapter.setData(history)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
