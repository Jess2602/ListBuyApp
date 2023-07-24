package com.example.listbuyapp

import android.R
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.listbuyapp.databinding.FragmentEditListItemBinding
import com.example.listbuyapp.databinding.FragmentEditListSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditListItemFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditListItemBinding
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditListItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val Id_list = arguments?.getString("Id_List")
        val Id_Item = arguments?.getString("Id_Item")
        val Name_Item = arguments?.getString("Name_Item")
        val Unit_Item = arguments?.getString("Unit_Item")
        val Price_Item = arguments?.getDouble("Price_Item")
        val Amount_Item = arguments?.getInt("Amount_Item")

        binding.editNameItem.setText(Name_Item)
        binding.editUnitItem.setText(Unit_Item, false)
        binding.editPriceItem.setText(Price_Item.toString())
        binding.editAmountItem.setText(Amount_Item.toString())

        val listUnit = ArrayList<String>()
        listUnit.add("pz")
        listUnit.add("kg")
        listUnit.add("L")
        listUnit.add("g")
        listUnit.add("lb")
        listUnit.add("ml")

        binding.editSumaItem.setOnClickListener {
            addOneToAmount()
        }

        binding.editRestItem.setOnClickListener {
            subtractOneFromAmount()
        }

        val numberAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, listUnit)
        binding.editUnitItem.setAdapter(numberAdapter)


        binding.saveEditItemButton.setOnClickListener {
            if (Price_Item != null && Amount_Item != null) {
                editAction(
                    Name_Item.toString().trim(),
                    Id_Item.toString().trim(),
                    Id_list.toString().trim(),
                    Price_Item.toDouble(),
                    Amount_Item.toInt(),
                    Unit_Item.toString().trim(),
                )
            }
        }
    }

    private fun close() {
        dismiss()
    }

    private fun editAction(
        Item: String,
        Id_Item: String,
        Id_List: String,
        price: Double,
        amount: Int,
        unit: String
    ) {

        if (binding.editNameItem.text.toString()
                .isNotEmpty() && binding.editAmountItem.text.toString()
                .isNotEmpty() && binding.editPriceItem.text.toString().isNotEmpty()
        ) {
            if (binding.editNameItem.text.toString().trim() != Item ||
                binding.editPriceItem.text.toString().trim() != price.toString() ||
                binding.editAmountItem.text.toString().trim() != amount.toString() ||
                binding.editUnitItem.text.toString().trim() != unit
            ) {

                val validationRef = db.collection("Users")
                    .document(user?.email.toString())
                    .collection("List").document(Id_List).collection("ItemListUser")
                    .whereEqualTo("name_item", binding.editNameItem.text.toString().trim())

                validationRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            val userRef = db.collection("Users")
                                .document(user?.email.toString())
                                .collection("List").document(Id_List).collection("ItemListUser")
                                .document(Id_Item)

                            val priceItem =
                                binding.editPriceItem.text.toString().trim().toDoubleOrNull() ?: 0.0
                            val amountItem =
                                binding.editAmountItem.text.toString().trim().toIntOrNull() ?: 0

                            val newData = hashMapOf(
                                "name_item" to binding.editNameItem.text.toString().trim(),
                                "price_item" to priceItem,
                                "amount_item" to amountItem,
                                "unit_item" to binding.editUnitItem.text.toString().trim()
                            )
                            userRef.update(newData as Map<String, Any>)
                                .addOnSuccessListener {
                                    val rootView =
                                        requireActivity().findViewById<View>(android.R.id.content)
                                    val snackbar = Snackbar.make(
                                        rootView,
                                        Html.fromHtml(
                                            "<b>${
                                                binding.editNameItem.text.toString().trim()
                                            } Actualizada</b>"
                                        ),
                                        Snackbar.LENGTH_SHORT
                                    )
                                    snackbar.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            com.example.listbuyapp.R.color.black
                                        )
                                    )
                                    snackbar.setBackgroundTint(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            com.example.listbuyapp.R.color.botonbase
                                        )
                                    )
                                    val drawableFondo = ContextCompat.getDrawable(
                                        requireContext(),
                                        com.example.listbuyapp.R.drawable.background_dialog
                                    )
                                    snackbar.view.background = drawableFondo
                                    snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    snackbar.show()
                                    close()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        context,
                                        "Error al actualizar el item: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    close()
                                }
                        } else {
                            Toast.makeText(context, "Ese item ya existe", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(context, "El Nombre del item es el mismo", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(context, "Faltan Datos por rellenar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOneToAmount() {
        val currentAmount = binding.editAmountItem.text.toString().toIntOrNull() ?: 0
        val newAmount = currentAmount + 1
        if (newAmount <= 999) {
            binding.editAmountItem.setText(newAmount.toString())
        }
    }

    private fun subtractOneFromAmount() {
        val currentAmount = binding.editAmountItem.text.toString().toIntOrNull() ?: 0
        val newAmount = currentAmount - 1
        if (newAmount >= 1) {
            binding.editAmountItem.setText(newAmount.toString())
        }


    }

}