package com.example.listbuyapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        val adapter = HistoricalAdapter(list)
        val recyclerView = binding.historicalRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historicalViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        val emptyTextView = binding.emptyTextViewHistory
        var isScreenOpened = false

        fun showShimmer() {
            if (!isScreenOpened) {
                binding.shimmerViewHistory.startShimmer()
                binding.shimmerViewHistory.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
        fun hideShimmer() {
            binding.shimmerViewHistory.stopShimmer()
            binding.shimmerViewHistory.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        historicalViewModel.readAllData.observe(viewLifecycleOwner, Observer { history ->
            if (history.isEmpty()) {
                hideShimmer()
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                if (!isScreenOpened) {
                    isScreenOpened = true
                }
                showShimmer()
                Handler(Looper.getMainLooper()).postDelayed({
                    adapter.setData(history)
                    hideShimmer()
                }, 800)
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        })
    }
}
