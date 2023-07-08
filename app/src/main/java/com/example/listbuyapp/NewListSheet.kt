package com.example.listbuyapp

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.listbuyapp.databinding.FragmentNewListSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.returnsreceiver.qual.This

class NewListSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewListSheetBinding
    private val db = Firebase.firestore
    private val User = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewListSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesCollection = db.collection("Categories")
        val listcat = ArrayList<String>()
        categoriesCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val documentId = document.id
                    listcat.add(documentId)
                }
            } else {
                println("Error obteniendo los documentos: ${task.exception}")
            }
        }

        val numberAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listcat)

        binding.listCategory.setAdapter(numberAdapter)

        binding.saveButton.setOnClickListener {
            saveAction()
        }
    }

    private fun cerrar() {
        dismiss()
    }

    private fun saveAction() {
        if (binding.listName.text.toString().isNotEmpty() && binding.listCategory.text.toString()
                .isNotEmpty()
        ) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Guardando Lista..")
            progressDialog.setCancelable(false)
            progressDialog.show()
            progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
            val userRef = db.collection("Users").document(User?.email.toString()).collection("List")
                .document(binding.listName.text.toString())
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Lista Existente", Toast.LENGTH_SHORT).show()
                    } else {
                        val listData = hashMapOf(
                            "name_list" to binding.listName.text.toString(),
                            "category" to binding.listCategory.text.toString()
                        )

                        val categoriesCollection = db.collection("Categories")
                        val categoryDocument = categoriesCollection.document(binding.listCategory.text.toString())

                        categoryDocument.get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val imgUrl = snapshot.getString("img_url")
                                    listData["img_category"] = imgUrl.toString()

                                    db.collection("Users").document(User?.email.toString()).collection("List")
                                        .document(binding.listName.text.toString())
                                        .set(listData)
                                        .addOnSuccessListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(context, "Lista Creada", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(context, "Error al Crear la Lista", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnCompleteListener {
                                            progressDialog.dismiss()
                                            cerrar()
                                        }
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, "Categoría no encontrada", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(context, "Error al obtener la categoría", Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }

        } else {
            Toast.makeText(context, "Faltan Datos por rellenar", Toast.LENGTH_SHORT).show()
        }
    }


}