package com.minorproject.cloudgallery.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.TimelineRowItemBinding
import com.minorproject.cloudgallery.views.interfaces.ItemClickListener
import com.minorproject.cloudgallery.model.TimeImage


class ImageAdapter(
    private val ImageList: List<TimeImage>,
    private val itemClickListener: ItemClickListener
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
        val image = ImageList[position]
        holder.bind(image, itemClickListener, position)
    }

    override fun getItemCount(): Int = ImageList.size


    class ViewHolder(private val binding: TimelineRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var viewForeground:RelativeLayout? = null
        fun bind(image: TimeImage, itemClickListener: ItemClickListener, position: Int) {
            binding.image = image
            binding.position = position
            binding.itemClick = itemClickListener

            viewForeground = binding.viewForeground
        }
    }
}