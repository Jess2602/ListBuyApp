package com.example.listbuyapp

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

    override fun onBindViewHolder(holderitem: ItemViewHolder, position: Int) {
        val item = items[position]
        holderitem.itemName.text = item.name_item
        holderitem.amount.text = item.amount_item.toString()

        holderitem.checkedlottie.setOnClickListener {
            if (!item.checked_item) {
                holderitem.checkedlottie.speed = 2f;
                holderitem.checkedlottie.playAnimation();
                item.checked_item = true;
            } else {
                holderitem.checkedlottie.speed = -2f;
                holderitem.checkedlottie.playAnimation();
                item.checked_item = false;
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
            Toast.makeText(
                activity,
                "Se Elimino ${item.name_item}",
                Toast.LENGTH_SHORT
            ).show()
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
