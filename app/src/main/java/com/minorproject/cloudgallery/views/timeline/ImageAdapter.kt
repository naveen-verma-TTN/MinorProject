package com.minorproject.cloudgallery.views.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.RecyclerRowBinding
import com.minorproject.cloudgallery.views.interfaces.ItemClickListener
import com.minorproject.cloudgallery.model.Image


class ImageAdapter(
    private val ImageList: List<Image>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RecyclerRowBinding = RecyclerRowBinding.inflate(
            layoutInflater, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = ImageList[position]
        holder.bind(image, itemClickListener, position)
    }

    override fun getItemCount(): Int = ImageList.size


    class ViewHolder(private val binding: RecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var viewForeground:RelativeLayout? = null
        fun bind(image: Image, itemClickListener: ItemClickListener, position: Int) {
            binding.image = image
            binding.position = position
            binding.itemClick = itemClickListener

            viewForeground = binding.viewForeground
        }
    }
}