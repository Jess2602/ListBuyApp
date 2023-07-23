package com.example.listbuyapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
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
import com.google.android.material.snackbar.Snackbar
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
        holderitem.amount.text = "${item.amount_item.toString()} ${item.unit_item.toString()}"

        if (!item.checked_item) {
            holderitem.itemName.text = item.name_item
            holderitem.itemName.paintFlags =
                holderitem.itemName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holderitem.itemName.setTextColor(Color.BLACK)
            holderitem.amount.paintFlags =
                holderitem.amount.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holderitem.amount.setTextColor(Color.BLACK)
            holderitem.backgroundItems.setBackgroundResource(R.drawable.radius_items_list)
        } else {
            holderitem.itemName.text = item.name_item
            holderitem.itemName.paintFlags =
                holderitem.itemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holderitem.itemName.setTextColor(Color.DKGRAY)
            holderitem.amount.paintFlags =
                holderitem.amount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holderitem.amount.setTextColor(Color.DKGRAY)
            holderitem.backgroundItems.setBackgroundResource(R.drawable.searchview_background)
        }
        // Variable para controlar si la animación está en curso
        var animationInProgress = false

        if (!item.checked_item) {
            holderitem.checkedlottie.progress = 0f
            holderitem.checkedlottie.setOnClickListener {
                // Verificar si la animación está en curso, si es así, no hacer nada
                if (animationInProgress) {
                    return@setOnClickListener
                }

                animationInProgress = true
                holderitem.checkedlottie.speed = 4.5f
                holderitem.checkedlottie.playAnimation()

                // Agregar un Listener a la animación
                holderitem.checkedlottie.addAnimatorListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationInProgress = false // La animación ha terminado, permitir otro clic
                        // Código a ejecutar cuando la animación haya terminado
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
                        ItemListUserCollection.update(newData as Map<String, Any>)
                            .addOnSuccessListener {
                                // Operación completada exitosamente
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    activity,
                                    "Error en ${exception}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                })
            }
        } else {
            holderitem.checkedlottie.progress = 1f
            holderitem.checkedlottie.setOnClickListener {
                // Verificar si la animación está en curso, si es así, no hacer nada
                if (animationInProgress) {
                    return@setOnClickListener
                }

                animationInProgress = true
                holderitem.checkedlottie.speed = -4.5f
                holderitem.checkedlottie.playAnimation()

                // Agregar un Listener a la animación
                holderitem.checkedlottie.addAnimatorListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationInProgress = false // La animación ha terminado, permitir otro clic
                        // Código a ejecutar cuando la animación haya terminado
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
                        ItemListUserCollection.update(newData as Map<String, Any>)
                            .addOnSuccessListener {
                                // Operación completada exitosamente
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    activity,
                                    "Error en ${exception}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                })
            }
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
                        val rootView = activity.findViewById<View>(android.R.id.content)
                        val snackbar = Snackbar.make(
                            rootView,
                            Html.fromHtml("<b>Se Elimino ${item.name_item}</b>"),
                            Snackbar.LENGTH_LONG
                        )

                        snackbar.setTextColor(
                            ContextCompat.getColor(
                                activity,
                                R.color.white
                            )
                        )
                        snackbar.setBackgroundTint(
                            ContextCompat.getColor(
                                activity,
                                R.color.error
                            )
                        )

                        val drawableFondo = ContextCompat.getDrawable(
                            activity,
                            R.drawable.background_dialog
                        )
                        snackbar.view.background = drawableFondo
                        snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        snackbar.show()

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
