package com.example.listbuyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class CategoriesAdapter(private var category: List<CategoriesList>) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesAdapter.CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_categories_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holderCategory: CategoriesAdapter.CategoryViewHolder, position: Int) {
        val Category = category[position]
        holderCategory.categoriesName.text = Category.category
        Picasso.get().load(Category.img_url).into(holderCategory.imageCategories)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoriesName: TextView = view.findViewById(R.id.textNameCategory)
        val imageCategories: CircleImageView = view.findViewById(R.id.imageCategory)
    }
}
