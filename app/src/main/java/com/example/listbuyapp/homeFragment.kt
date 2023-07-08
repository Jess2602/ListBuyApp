package com.example.listbuyapp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listbuyapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.os.Handler
import android.widget.Toast


class homeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: listAdapter
    private lateinit var list: ArrayList<ListUser>
    private val db = Firebase.firestore
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addListButton.setOnClickListener {
            NewListSheet().show(childFragmentManager, "newListTag")
        }
        showRecycler()
    }

    private fun showRecycler() {
        list = ArrayList()
        adapter = listAdapter(list)
        binding.shimmerView.startShimmer()
        db.collection("Users").document(user?.email.toString()).collection("List")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                list.clear()

                if (snapshot != null) {
                    for (document in snapshot) {
                        val wallItem = document.toObject(ListUser::class.java)
                        wallItem.name_list = document["name_list"].toString()
                        wallItem.category = document["category"].toString()
                        wallItem.img_category = document["img_category"].toString()
                        list.add(wallItem)
                    }
                    if (list.isNotEmpty()) {
                        binding.emptyTextView.visibility = View.GONE
                        Handler().postDelayed({
                            binding.shimmerView.stopShimmer()
                            binding.shimmerView.visibility = View.GONE
                            adapter.notifyDataSetChanged()
                        }, 500)
                    } else {
                        binding.emptyTextView.visibility = View.VISIBLE
                        binding.shimmerView.stopShimmer()
                        binding.shimmerView.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        binding.recyclerlistname.adapter = adapter
        binding.recyclerlistname.layoutManager = LinearLayoutManager(requireContext())
    }

}
