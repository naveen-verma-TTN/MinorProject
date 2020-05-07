package com.minorProject.cloudGallery.view.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minorProject.cloudGallery.databinding.CustomCategoryPageItemBinding
import com.minorProject.cloudGallery.model.bean.Category

/**
 * CategoryPage Adapter class
 */
class CategoryPageAdapter internal constructor(
    private var category: ArrayList<Category>,
    private val categoryPageItemClick: CategoryPageItemClick
) :
    RecyclerView.Adapter<CategoryPageAdapter.MyViewHolder>() {

    private companion object {
        private val TAG: String = CategoryPageAdapter::class.java.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: CustomCategoryPageItemBinding = CustomCategoryPageItemBinding.inflate(
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
    class MyViewHolder(private val binding: CustomCategoryPageItemBinding) :
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