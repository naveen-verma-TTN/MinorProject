package com.minorproject.cloudgallery.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.CategoryDetailPageItemBinding
import com.minorproject.cloudgallery.model.Image
import com.minorproject.cloudgallery.views.interfaces.HomeItemClick

class CategoryPageDetailAdapter internal constructor(
    private var image: ArrayList<Image>,
    private val homeItemClick: HomeItemClick
)
    : RecyclerView.Adapter<CategoryPageDetailAdapter.CategoryViewHolder>() {

    private companion object {
        private const val TAG = "CategoryPageDetailAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: CategoryDetailPageItemBinding = CategoryDetailPageItemBinding.inflate(
                layoutInflater, parent, false
            )
            return CategoryViewHolder(
                binding
            )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(image[position], homeItemClick, position)
    }


    override fun getItemCount(): Int {
        return image.size
    }

    fun setList(image: ArrayList<Image>) {
        this.image = image
    }


    /**
     * Inner ViewHolder class
     */
    class CategoryViewHolder(private val binding: CategoryDetailPageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            image: Image,
            homeItemClick: HomeItemClick,
            position: Int
        ) {
            binding.image = image
            binding.itemClick = homeItemClick
            binding.position = position
        }
    }
}