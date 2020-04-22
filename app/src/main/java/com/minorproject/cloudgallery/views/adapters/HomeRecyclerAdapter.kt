package com.minorproject.cloudgallery.views.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.HomeItemBinding
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.views.interfaces.HomeItemClick


class HomeRecyclerAdapter internal constructor(
    private var category: ArrayList<Category>,
    private val homeItemClick: HomeItemClick
) :
    RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder>() {

    private companion object {
        private const val TAG = "HomeRecyclerAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: HomeItemBinding = HomeItemBinding.inflate(
            layoutInflater, parent, false
        )
        return MyViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
    class MyViewHolder(private val binding: HomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            category: Category,
            homeItemClick: HomeItemClick,
            position: Int
        ) {
            binding.category = category
            binding.homeItemClick = homeItemClick
            binding.position = position
        }
    }
}