package com.example.listbuyapp

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItemsAdapter(private val items: List<ItemListUser>) :
    RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holderitem: ItemViewHolder, position: Int) {
        val item = items[position]
        holderitem.itemName.text = item.name_item
        holderitem.amount.text = item.amount_item.toString()

        if (!item.checked_item) {
            // Código para cuando checked_item es false
            holderitem.checkedlottie.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val activity = it.context as AppCompatActivity
                val ItemListUserCollection = db.collection("Users")
                    .document(user?.email.toString())
                    .collection("List")
                    .document(item.id_list)
                    .collection("ItemListUser").document(item.id_item)

                val newData = hashMapOf(
                    "checked_item" to true,
                )
                ItemListUserCollection.update(newData as Map<String, Any>).addOnSuccessListener {

                }
                    .addOnFailureListener { exception ->

                    }
            }
            holderitem.checkedlottie.progress =
                0f; // Establecer el progreso en 0 para que la animación inicie desde el principio


        } else {
            // Código para cuando checked_item es true
            holderitem.checkedlottie.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val activity = it.context as AppCompatActivity
                val ItemListUserCollection = db.collection("Users")
                    .document(user?.email.toString())
                    .collection("List")
                    .document(item.id_list)
                    .collection("ItemListUser").document(item.id_item)

                val newData = hashMapOf(
                    "checked_item" to false,
                )
                ItemListUserCollection.update(newData as Map<String, Any>).addOnSuccessListener {

                }
                    .addOnFailureListener { exception ->

                    }
            }
            holderitem.checkedlottie.progress =
                1f; // Establecer el progreso en 1 para que la animación muestre su estado final


        }




        holderitem.backgroundItems.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val activity = it.context as AppCompatActivity
            Toast.makeText(
                activity,
                "Se Elimino ${item.name_item}",
                Toast.LENGTH_SHORT
            ).show()
        }

        holderitem.deleteItem.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val activity = it.context as AppCompatActivity
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("¿Desea eliminar este item?")
            alertDialogBuilder.setMessage("Eliminar")
            alertDialogBuilder.setPositiveButton("Si") { dialog, _ ->
                val progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Eliminando Item..")
                progressDialog.setCancelable(false)
                progressDialog.show()
                progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
                db.collection("Users").document(user?.email.toString()).collection("List")
                    .document(item.id_list).collection("ItemListUser").document(item.id_item)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "Se Elimino ${item.name_item}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "No se pudo eliminar ${item.name_item}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }

                dialog.dismiss()
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val positiveButton =
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(activity, R.color.botonbase))
            val negativeButton =
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(activity, R.color.botonbase))

            alertDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.textNameItem)
        val backgroundItems: LinearLayout = itemView.findViewById(R.id.backgroundItemList)
        val checkedlottie: LottieAnimationView = itemView.findViewById(R.id.lottiechecked)
        val amount: TextView = itemView.findViewById(R.id.textAmountItem)
        val deleteItem: ImageView = itemView.findViewById(R.id.deleteItemList)
    }


}
