package com.minorProject.cloudGallery.views.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minorProject.cloudGallery.databinding.CategoryPageItemBinding
import com.minorProject.cloudGallery.model.Category

/**
 * CategoryPage Adapter class
 */
class CategoryPageAdapter internal constructor(
    private var category: ArrayList<Category>,
    private val categoryPageItemClick: CategoryPageItemClick
) :
    RecyclerView.Adapter<CategoryPageAdapter.MyViewHolder>() {

    private companion object {
        private const val TAG = "CategoryPageAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: CategoryPageItemBinding = CategoryPageItemBinding.inflate(
            layoutInflater, parent, false
        )
        return MyViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(category[position], categoryPageItemClick, position)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    fun setList(category: ArrayList<Category>) {
        this.category = category
    }


    /**
     * Inner ViewHolder class
     */
    class MyViewHolder(private val binding: CategoryPageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            category: Category,
            categoryPageItemClick: CategoryPageItemClick,
            position: Int
        ) {
            binding.category = category
            binding.categoryPageItemClick = categoryPageItemClick
            binding.position = position
        }
    }
}

/**
 * CategoryPageItemClick interface
 */
interface CategoryPageItemClick {
    fun onItemClicked(category: Category, position: Int)
}