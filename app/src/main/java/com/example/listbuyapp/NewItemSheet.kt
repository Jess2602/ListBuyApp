package com.example.listbuyapp

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.listbuyapp.databinding.FragmentEditListSheetBinding
import com.example.listbuyapp.databinding.FragmentNewItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NewItemSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewItemSheetBinding
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewItemSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id_list = arguments?.getString("Id_List")
        val list = arguments?.getString("List")
        binding.saveNewItemButton.setOnClickListener {
            saveNewItemAction(
                list.toString(),
                id_list.toString()
            )
        }

    }

    private fun close() {
        dismiss()
    }

    private fun saveNewItemAction(list: String, id_List: String) {
        if (
            binding.nameItem.text.toString()
                .isNotEmpty() && binding.amountItem.text.toString()
                .isNotEmpty() && binding.priceItem.text.toString()
                .isNotEmpty()
        ) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Guardando Item...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

            val listRef = db.collection("Users").document(user?.email.toString()).collection("List")
                .document(id_List).collection("ItemListUser")
                .whereEqualTo("name_item", binding.nameItem.text.toString())
            listRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        val ItemListUserCollection = db.collection("Users")
                            .document(user?.email.toString())
                            .collection("List")
                            .document(id_List)
                            .collection("ItemListUser")


                        val itemListUserDocument = ItemListUserCollection.document()
                        val newItemListUserId = itemListUserDocument.id

                        val priceItem = binding.priceItem.text.toString().toDoubleOrNull() ?: 0.0
                        val amountItem = binding.amountItem.text.toString().toIntOrNull() ?: 0

                        val listData = hashMapOf(
                            "name_item" to binding.nameItem.text.toString(),
                            "price_item" to priceItem,
                            "amount_item" to amountItem,
                            "checked_item" to false,
                            "id_list" to id_List,
                            "id_item" to newItemListUserId
                        )
                        ItemListUserCollection.document(newItemListUserId)
                            .set(listData)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    context,
                                    "Item creado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                close()
                            }
                            .addOnFailureListener { exception ->
                                progressDialog.dismiss()
                                Toast.makeText(
                                    context,
                                    "Error al crear item ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                close()
                            }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(

                            context,
                            "El item ya Existe",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                .addOnFailureListener { exception ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        context,
                        "Error al consultar la colección: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                context,
                "No se han rellenado todos los datos",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}




