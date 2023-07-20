package com.example.listbuyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.ActivityItemsListBinding

class ItemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsListBinding
    private lateinit var itemslist: ArrayList<ItemListUser>
    private lateinit var adapter: ItemsAdapter

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
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)

        recycleritems()

    }

    private fun recycleritems() {
        adapter = ItemsAdapter(cargarlista())
        binding.recyclerViewitemlist.adapter = adapter
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarlista(): MutableList<ItemListUser> {
        val listItems = mutableListOf<ItemListUser>()
        listItems.add(ItemListUser("asasas", "te amo", "Tomate", 150.0, 2, false))

        return listItems
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}