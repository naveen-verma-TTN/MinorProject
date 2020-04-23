package com.minorproject.cloudgallery.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.HomeDetailItemBinding
import com.minorproject.cloudgallery.databinding.HomeItemBinding
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.views.interfaces.HomeItemClick

class HomeDetailAdapter internal constructor(
    private var category: ArrayList<Category>,
    private val homeItemClick: HomeItemClick
)
    : RecyclerView.Adapter<HomeDetailAdapter.CategoryViewHolder>() {

    private companion object {
        private const val TAG = "HomeDetailAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: HomeDetailItemBinding = HomeDetailItemBinding.inflate(
                layoutInflater, parent, false
            )
            return CategoryViewHolder(
                binding
            )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(category[position], homeItemClick, position)
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
    class CategoryViewHolder(private val binding: HomeDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            category: Any?,
            homeItemClick: HomeItemClick,
            position: Int
        ) {
            binding.category = category as Category?
            binding.itemClick = homeItemClick
            binding.position = position
        }
    }
}