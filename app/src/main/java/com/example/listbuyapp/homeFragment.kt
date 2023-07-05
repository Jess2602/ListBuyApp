package com.example.listbuyapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.FragmentHomeBinding

class homeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var listViewModel: ListViewModel

    private lateinit var adapter: listAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addListButton.setOnClickListener {
            NewListSheet().show(childFragmentManager, "newListTag")
        }
        listViewModel.listName.observe(viewLifecycleOwner) {
            listViewModel.listName.toString()
        }

        listViewModel.listCategory.observe(viewLifecycleOwner) {
            listViewModel.listCategory.toString()
        }
        binding.recyclerlistname.setOnClickListener {
            val intent = Intent(requireContext(), itemsListActivity::class.java)
            requireActivity().startActivity(intent)
        }


        binding.recyclerlistname.layoutManager = LinearLayoutManager(requireContext())

        val nombres = listOf("Super", "Ferreteria", "Escuela") // Aquí puedes agregar tus nombres

        adapter = listAdapter(nombres)
        binding.recyclerlistname.adapter = adapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}