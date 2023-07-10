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

        setSupportActionBar(binding.toolbarItemsList)
        supportActionBar?.title = List.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarItemsList.setNavigationOnClickListener {
            finish()
        }
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)

        recycleritems()

    }

    private fun recycleritems(){
        adapter = ItemsAdapter(cargarlista())
        binding.recyclerViewitemlist.adapter = adapter
        binding.recyclerViewitemlist.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarlista () : MutableList<ItemListUser>{
        val listItems = mutableListOf<ItemListUser>()
        listItems.add(ItemListUser("asasas","Tomate","150","2",false))
        listItems.add(ItemListUser("asasas","Cebolla","250","7",false))
        listItems.add(ItemListUser("asasas","Leche","400","4",false))
        listItems.add(ItemListUser("asasas","Refresco","25","2",false))
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