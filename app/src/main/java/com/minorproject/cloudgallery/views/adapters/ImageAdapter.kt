package com.minorproject.cloudgallery.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.TimelineRowItemBinding
import com.minorproject.cloudgallery.model.Image


class ImageAdapter(
    private var imageList: List<Image>,
    private val itemClickListener: ImageItemClickListener
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: TimelineRowItemBinding = TimelineRowItemBinding.inflate(
            layoutInflater, parent, false
        )
        return ViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image, itemClickListener, position)
    }

    override fun getItemCount(): Int = imageList.size

    fun setList(imageList: ArrayList<Image>) {
        this.imageList = imageList
    }

    class ViewHolder(private val binding: TimelineRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var viewForeground:RelativeLayout? = null
        fun bind(image: Image, itemClickListener: ImageItemClickListener, position: Int) {
            binding.image = image
            binding.position = position
            binding.itemClick = itemClickListener

            viewForeground = binding.viewForeground
        }
    }
}

interface ImageItemClickListener {
    fun onItemClicked(position: Int)
}