package com.example.listbuyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.ActivityItemsListBinding

class itemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsListBinding
    private lateinit var adapter: itemsAdapter

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

        val nombres = listOf("Tomate", "Cebolla", "Leche") // Aquí puedes agregar tus nombres

        adapter = itemsAdapter(nombres)
        binding.recyclerViewitemlist.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}