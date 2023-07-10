package com.example.listbuyapp

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.FragmentCategoriesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class categoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var adapter: CategoriesAdapter
    private lateinit var catgories: ArrayList<CategoriesList>
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRecyclerCategories()

    }
    private var isFirstView = true

    private fun showRecyclerCategories() {
        catgories = ArrayList()
        adapter = CategoriesAdapter(catgories)
        binding.shimmerViewCategories.startShimmer()
        db.collection("Categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                catgories.clear()

                if (snapshot != null) {
                    for (document in snapshot) {
                        val categoriesItems = document.toObject(CategoriesList::class.java)
                        categoriesItems.img_url = document["img_url"].toString()
                        categoriesItems.category = document.id
                        catgories.add(categoriesItems)
                    }
                    if (isFirstView) {
                        if (catgories.isNotEmpty()) {
                            Handler().postDelayed({
                                binding.shimmerViewCategories.stopShimmer()
                                binding.shimmerViewCategories.visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }, 800)
                        } else {
                            binding.shimmerViewCategories.visibility = View.GONE
                            adapter.notifyDataSetChanged()
                        }
                        isFirstView = false
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        binding.recyclerCategories.adapter = adapter
        binding.recyclerCategories.layoutManager = LinearLayoutManager(requireContext())
    }


}