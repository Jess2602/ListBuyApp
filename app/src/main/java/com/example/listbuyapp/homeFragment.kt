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


class homeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ListAdapter
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
        showRecyclerList()
    }

    private var isFirstLoad = true

    private fun showRecyclerList() {
        list = ArrayList()
        adapter = ListAdapter(list)
        binding.shimmerView.startShimmer()
        db.collection("Users").document(user?.email.toString()).collection("List")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                list.clear()

                if (snapshot != null) {
                    for (document in snapshot) {
                        val listItems = document.toObject(ListUser::class.java)
                        listItems.name_list = document["name_list"].toString()
                        listItems.id_list = document["id_list"].toString()
                        listItems.category = document["category"].toString()
                        listItems.img_category = document["img_category"].toString()
                        list.add(listItems)
                    }
                    if (list.isNotEmpty()) {
                        binding.emptyTextView.visibility = View.GONE
                    } else {
                        binding.emptyTextView.visibility = View.VISIBLE
                    }

                    if (isFirstLoad) {
                        if (list.isNotEmpty()) {
                            binding.emptyTextView.visibility = View.GONE
                            Handler().postDelayed({
                                binding.shimmerView.stopShimmer()
                                binding.shimmerView.visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }, 800)
                        } else {
                            binding.shimmerView.visibility = View.GONE
                            binding.emptyTextView.visibility = View.VISIBLE
                            adapter.notifyDataSetChanged()
                        }
                        isFirstLoad = false
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    adapter.notifyDataSetChanged()
                }
            }

        binding.recyclerlistname.adapter = adapter
        binding.recyclerlistname.layoutManager = LinearLayoutManager(requireContext())
    }


}
