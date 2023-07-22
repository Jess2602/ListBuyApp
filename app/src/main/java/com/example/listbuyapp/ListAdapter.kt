package com.example.listbuyapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.listbuyapp.data.History
import com.example.listbuyapp.data.HistoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ListAdapter(private var list: List<ListUser>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    private lateinit var mHistoryViewModel: HistoryViewModel
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListAdapter.ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAdapter.ListViewHolder, position: Int) {
        val item = list[position]
        holder.nameList.text = item.name_list
        holder.category.text = item.category
        Picasso.get().load(item.img_category).into(holder.categoryImage)
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val collectionRef = db.collection("Users").document(user?.email.toString())
            .collection("List").document(item.id_list).collection("ItemListUser")

        collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                var checkedCount = 0

                var uncheckedCount = 0

                for (document in snapshot.documents) {

                    val checked = document.getBoolean("checked_item") ?: false

                    if (checked) {
                        checkedCount++

                    } else {
                        uncheckedCount++
                    }

                }
                val totalItems = checkedCount + uncheckedCount
                val progress = (checkedCount.toDouble() / totalItems.toDouble()) * 100
                holder.progress.progress = progress.toInt()
                holder.amountItems.text = "$checkedCount/$totalItems"
            }
        }


        holder.listBackground.setOnLongClickListener {
            val editListSheet = EditListSheet()
            val bundle = Bundle()
            bundle.putString("List", item.name_list)
            bundle.putString("Id_List", item.id_list)
            editListSheet.arguments = bundle
            editListSheet.show(
                (holder.itemView.context as FragmentActivity).supportFragmentManager,
                "editListTag"
            )
       true
       }

        holder.listBackground.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val intent = Intent(activity, ItemsListActivity::class.java).apply {
                val bundle = Bundle()
                bundle.putString("List", item.name_list)
                bundle.putString("Id_List", item.id_list)
                putExtras(bundle)
            }
            activity.startActivity(intent)
        }

        holder.deleteImage.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val activity = it.context as AppCompatActivity
            val alertDialogBuilder = AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("Desea Eliminar la Lista?")
            alertDialogBuilder.setMessage("Eliminar")
            alertDialogBuilder.setPositiveButton("Si") { dialog, _ ->
                val progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Eliminando Lista..")
                progressDialog.setCancelable(false)
                progressDialog.show()
                progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                mHistoryViewModel = ViewModelProvider(activity)[HistoryViewModel::class.java]
                val history = History(0, item.name_list, item.category, "Fecha")
                mHistoryViewModel.addHistory(history)

                db.collection("Users").document(user?.email.toString()).collection("List")
                    .document(item.id_list).delete().addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "Se Elimino ${item.name_list}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "No se pudo eliminar ${item.name_list}",
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
        return list.size
    }

    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameList: TextView = view.findViewById(R.id.textNameList)
        val category: TextView = view.findViewById(R.id.textViewCategory)
        val amountItems: TextView = view.findViewById(R.id.textViewAmountItems)
        val categoryImage: CircleImageView = view.findViewById(R.id.categoryImage)
        val deleteImage: ImageView = view.findViewById(R.id.deleteList)
        val listBackground: LinearLayout = view.findViewById(R.id.backgroundList)
        val progress: ProgressBar = view.findViewById(R.id.progressBarItemsList)
    }
}