package com.example.listbuyapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.FragmentHomeBinding
import com.example.listbuyapp.databinding.FragmentNewListSheetBinding

class homeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: listAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addListButton.setOnClickListener {
            NewListSheet().show(childFragmentManager, "newListTag")
        }

        binding.recyclerlistname.setOnClickListener {
            val intent = Intent(requireContext(), itemsListActivity::class.java)
            requireActivity().startActivity(intent)
        }


        binding.recyclerlistname.layoutManager = LinearLayoutManager(requireContext())

        val nombres = listOf("Super", "Ferreteria", "Escuela")

        adapter = listAdapter(nombres)
        binding.recyclerlistname.adapter = adapter


    }
}
