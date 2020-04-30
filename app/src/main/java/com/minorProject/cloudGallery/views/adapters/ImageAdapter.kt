package com.minorProject.cloudGallery.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.minorProject.cloudGallery.databinding.CustomTimelineRowItemBinding
import com.minorProject.cloudGallery.model.bean.Image

/**
 * Image Adapter class
 */
class ImageAdapter(
    private var imageList: List<Image>,
    private val itemClickListener: ImageItemClickListener
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: CustomTimelineRowItemBinding = CustomTimelineRowItemBinding.inflate(
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

    /**
     * ViewHolder inner class
     */
    class ViewHolder(private val binding: CustomTimelineRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var viewForeground:RelativeLayout? = null
        fun bind(image: Image, itemClickListener: ImageItemClickListener, position: Int) {
            binding.image = image
            binding.position = position
            binding.itemClick = itemClickListener

//            viewForeground = binding.viewForeground
        }
    }
}

/**
 * ImageItemClickListener interface
 */
interface ImageItemClickListener {
    fun onItemClicked(position: Int)
}