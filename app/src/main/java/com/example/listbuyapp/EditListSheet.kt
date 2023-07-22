package com.example.listbuyapp

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.listbuyapp.databinding.FragmentEditListSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditListSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditListSheetBinding
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditListSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id_list = arguments?.getString("Id_List")
        val list = arguments?.getString("List")

        binding.editListName.setText(list)

        binding.editListButton.setOnClickListener {
            editAction(list.toString(), id_list.toString())
        }
    }

    private fun cerrar() {
        dismiss()
    }

    private fun editAction(List: String, Id: String) {
        if (binding.editListName.text.toString().isNotEmpty()) {
            if (binding.editListName.text.toString() != List) {
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Editando Lista..")
                progressDialog.setCancelable(false)
                progressDialog.show()
                progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
                val validationRef = db.collection("Users")
                    .document(user?.email.toString())
                    .collection("List")
                    .whereEqualTo("name_list", binding.editListName.text.toString())

                validationRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            val userRef = db.collection("Users")
                                .document(user?.email.toString())
                                .collection("List")
                                .document(Id)

                            val newData = hashMapOf(
                                "name_list" to binding.editListName.text.toString(),
                            )

                            userRef.update(newData as Map<String, Any>)
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    val rootView = requireActivity().findViewById<View>(android.R.id.content)
                                    val snackbar = Snackbar.make(
                                        rootView,
                                        Html.fromHtml("<b>Lista ${List} Actualizada</b>"),
                                        Snackbar.LENGTH_SHORT
                                    )

                                    snackbar.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.black
                                        )
                                    )
                                    snackbar.setBackgroundTint(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.crear
                                        )
                                    )

                                    val drawableFondo = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.background_dialog
                                    )
                                    snackbar.view.background = drawableFondo
                                    snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    snackbar.show()
                                    cerrar()
                                }
                                .addOnFailureListener { exception ->
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Error al actualizar la lista: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    cerrar()
                                }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(context, "Esta Lista ya existe", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "El Nombre es el Mismo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Faltan Datos por rellenar", Toast.LENGTH_SHORT).show()
        }
    }
}

