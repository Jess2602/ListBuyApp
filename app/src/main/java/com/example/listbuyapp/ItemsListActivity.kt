package com.example.listbuyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private val totalItems: Int = 100

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

        val collectionRef = db.collection("Users").document(user?.email.toString())
            .collection("List").document(Id_list.toString()).collection("ItemListUser")

        collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                var checkedCount = 0
                var totalPrice = 0.0
                var totalPriceChecked = 0.0
                var uncheckedCount = 0

                for (document in snapshot.documents) {
                    val subtotalItems = document.getDouble("price_item") ?: 0.0 // Precio individual de un ítem
                    val cantidadItems = document.getDouble("amount_item") ?: 0.0 // Cantidad de ítems
                    val checked = document.getBoolean("checked_item") ?: false

                    if (checked) {
                        checkedCount++
                        val totalItems = subtotalItems * cantidadItems
                        totalPriceChecked += totalItems
                    } else {
                        uncheckedCount++
                    }

                    totalPrice += subtotalItems * cantidadItems
                }

                // Aquí tienes los totales con la multiplicación de la cantidad
                // También tienes el precio total de todos los elementos (totalPrice)
                // Y el precio total de los elementos marcados (totalPriceChecked)

                // Ejemplo: Actualizar el ProgressBar
                val totalItems = checkedCount + uncheckedCount
                val progress = (checkedCount.toDouble() / totalItems.toDouble()) * 100
                binding.progressBaritems.progress = progress.toInt()


                // Ejemplo: Mostrar los totales en TextViews
                binding.itemsMax.text = "$checkedCount/$totalItems"
                binding.totalPrice.text = "MX$$totalPrice"
                binding.totalPriceChecked.text = "MX$$totalPriceChecked"


            } else {
                adapter.notifyDataSetChanged()
            }

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
                    if (itemslist.isNotEmpty()) {
                        binding.emptyTextViewItem.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    } else {
                        binding.emptyTextViewItem.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    adapter.notifyDataSetChanged()
                }
            }


        binding.recyclerViewitemlist.adapter = adapter
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)

    }
}

