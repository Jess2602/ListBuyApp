package com.example.listbuyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.listbuyapp.databinding.FragmentNewListSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewListSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewListSheetBinding
    private lateinit var listViewModel: ListViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        listViewModel = ViewModelProvider(activity).get(ListViewModel::class.java)
        binding.saveButton.setOnClickListener {

            saveAction()

        }
    }

    private fun saveAction() {
        listViewModel.listName.value = binding.listName.text.toString()
        listViewModel.listCategory.value = binding.listCategory.text.toString()
        binding.listName.setText("")
        binding.listCategory.setText("")
        dismiss()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewListSheetBinding.inflate(inflater, container, false)

        return binding.root
    }


}