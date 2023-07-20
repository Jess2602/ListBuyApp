package com.example.listbuyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.ActivityItemsListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsListBinding
    private lateinit var itemslist: ArrayList<ItemListUser>
    private lateinit var adapter: ItemsAdapter
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val List = bundle?.getString("List")
        val Id_list = bundle?.getString("Id_List")
        binding.addNewItemButton.setOnClickListener {
            val newItemSheet = NewItemSheet()

            val bundle = Bundle()
            bundle.putString("List", List)
            bundle.putString("Id_List", Id_list)

            newItemSheet.arguments = bundle
            newItemSheet.show(supportFragmentManager, "newItemTag")
        }

        setSupportActionBar(binding.toolbarItemsList)
        supportActionBar?.title = List.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarItemsList.setNavigationOnClickListener {
            finish()
        }
        showRecyclerItemsList(List.toString(), Id_list.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showRecyclerItemsList(list: String, id_list: String) {
        itemslist = ArrayList()
        adapter = ItemsAdapter(itemslist)

        db.collection("Users").document(user?.email.toString()).collection("List")
            .document(id_list).collection("ItemListUser")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                itemslist.clear()

                if (snapshot != null) {
                    for (document in snapshot) {
                        val listItems = document.toObject(ItemListUser::class.java)
                        listItems.name_item = document["name_item"].toString()
                        listItems.id_item = document["id_item"].toString()
                        listItems.id_list = document["id_list"].toString()
                        listItems.price_item = document["price_item"].toString().toDouble()
                        listItems.amount_item = document["amount_item"].toString().toInt()
                        listItems.checked_item = document["checked_item"].toString().toBoolean()
                        itemslist.add(listItems)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.notifyDataSetChanged()
                }
            }


        binding.recyclerViewitemlist.adapter = adapter
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)

    }
}

