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

class NewListSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewListSheetBinding
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser


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

        val numberAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listcat)

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
            val listRef = db.collection("Users").document(user?.email.toString()).collection("List")
                .whereEqualTo("name_list", binding.listName.text.toString())
            listRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        val categoriesCollection = db.collection("Categories")
                        val categoryDocument =
                            categoriesCollection.document(binding.listCategory.text.toString())

                        categoryDocument.get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val listCollection = db.collection("Users")
                                        .document(user?.email.toString())
                                        .collection("List")

                                    val newListDocument = listCollection.document()
                                    val newListDocumentId = newListDocument.id

                                    val listData = hashMapOf(
                                        "name_list" to binding.listName.text.toString(),
                                        "category" to binding.listCategory.text.toString(),
                                        "id_list" to newListDocumentId
                                    )

                                    val imgUrl = snapshot.getString("img_url")
                                    listData["img_category"] = imgUrl.toString()

                                    listCollection.document(newListDocumentId)
                                        .set(listData)
                                        .addOnSuccessListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                context,
                                                "Lista Creada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            cerrar()
                                        }
                                        .addOnFailureListener { exception ->
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                context,
                                                "Error al Crear la Lista: ${exception.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            cerrar()
                                        }

                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Categoría no encontrada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    context,
                                    "Error al obtener la categoría",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Lista Existente", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Error al consultar la colección: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } else {
            Toast.makeText(context, "Faltan Datos por rellenar", Toast.LENGTH_SHORT).show()
        }
    }


}