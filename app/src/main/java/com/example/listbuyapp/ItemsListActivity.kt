package com.example.listbuyapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.ActivityItemsListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
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

        binding.shareItems.setOnClickListener {
            share(collectionRef, List.toString())
        }
        binding.deleteAllItems.setOnClickListener {
            deleteAll(Id_list.toString())
        }
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
                    val subtotalItems =
                        document.getDouble("price_item") ?: 0.0 // Precio individual de un ítem
                    val cantidadItems =
                        document.getDouble("amount_item") ?: 0.0 // Cantidad de ítems
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

    private fun deleteAll(idlist: String) {
        val itemListUserRef = db.collection("Users").document(user?.email.toString())
            .collection("List").document(idlist)
            .collection("ItemListUser")

        // First, check if there are any items to delete
        itemListUserRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Toast.makeText(this, "No hay items agregados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Show the AlertDialog to confirm deletion
                val alertDialogBuilder = android.app.AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Desea Eliminar la Lista?")
                    .setMessage("¿Estás seguro que deseas eliminar todos los items de la lista?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val progressDialog = ProgressDialog(this)
                        progressDialog.setMessage("Eliminando Lista..")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                        for (document in snapshot) {
                            document.reference.delete()
                        }

                        progressDialog.dismiss()

                        // Mostrar Snackbar de éxito
                        val rootView = findViewById<View>(android.R.id.content)
                        val snackbar = Snackbar.make(
                            rootView,
                            Html.fromHtml("<b>Items Eliminados</b>"),
                            Snackbar.LENGTH_LONG
                        )
                        showSnackbar(snackbar)

                    }
                    .setNegativeButton("Cancelar", null)
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

                val positiveButton =
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
                val negativeButton =
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))

                alertDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
            }
        }.addOnFailureListener { e ->
            // Mostrar Snackbar de error
            val rootView = findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(
                rootView,
                Html.fromHtml("<b>Error ${e.message}</b>"),
                Snackbar.LENGTH_LONG
            )
            showSnackbar(snackbar)
        }
    }

    private fun showSnackbar(snackbar: Snackbar) {
        snackbar.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        snackbar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.error))
        val drawableFondo =
            ContextCompat.getDrawable(applicationContext, R.drawable.background_dialog)
        snackbar.view.background = drawableFondo
        snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }

    private fun share(colec: CollectionReference, listTitle: String) {
        colec.get().addOnSuccessListener { snapshot ->
            if (!isFinishing) {
                val items = mutableListOf<Pair<String, Boolean>>()

                for (document in snapshot) {
                    val item = document.getString("name_item")
                    val checked = document.getBoolean("checked_item") ?: false
                    item?.let { items.add(it to checked) }
                }

                val listWithBullets = buildString {
                    append("Lista $listTitle :\n")
                    for ((item, checked) in items) {
                        if (checked) {
                            append("✓ $item \n") // Agregar línea en medio si el elemento está marcado
                        } else {
                            append("• $item\n")
                        }
                    }
                }

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, listWithBullets)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "Compartir $listTitle")
                }
                val shareIntent = Intent.createChooser(intent, null)
                startActivity(shareIntent)
            }
        }
    }


    private fun showRecyclerItemsList(list: String, id_list: String) {
        itemslist = ArrayList()
        adapter = ItemsAdapter(itemslist)

        db.collection("Users").document(user?.email.toString()).collection("List")
            .document(id_list).collection("ItemListUser")
            .orderBy("checked_item", Query.Direction.ASCENDING)
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

