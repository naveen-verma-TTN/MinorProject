package com.minorproject.cloudgallery.views.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.minorproject.cloudgallery.databinding.CategoryDetailPageItemBinding
import com.minorproject.cloudgallery.model.Image
import kotlinx.android.synthetic.main.category_detail_page_item.view.*
import kotlinx.android.synthetic.main.category_page_item.view.*

class CategoryPageDetailAdapter internal constructor(
    private var image: ArrayList<Image>,
    private val categoryPageDetailItemClick: CategoryPageDetailItemClick
) : RecyclerView.Adapter<CategoryPageDetailAdapter.CategoryViewHolder>() {

    init {
        setHasStableIds(true)
    }

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
        holder.bind(image[position], categoryPageDetailItemClick, position)

        if (tracker!!.isSelected(position.toLong())) {
            holder.itemView.detail_imageView.setColorFilter(Color.parseColor("#86000000"))
        } else {
            holder.itemView.detail_imageView.colorFilter = null
        }
    }


    override fun getItemCount(): Int {
        return image.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setList(image: ArrayList<Image>) {
        this.image = image
    }

    private var tracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    /**
     * Inner ViewHolder class
     */
    class CategoryViewHolder(private val binding: CategoryDetailPageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            image: Image,
            categoryPageDetailItemClick: CategoryPageDetailItemClick,
            position: Int
        ) {
            binding.image = image
            binding.itemClick = categoryPageDetailItemClick
            binding.position = position
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {

                override fun getSelectionKey(): Long? = itemId

                override fun getPosition(): Int = adapterPosition

            }
    }

    class MyLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent)
                : ItemDetails<Long>? {

            val view = rv.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (rv.getChildViewHolder(view) as CategoryViewHolder)
                    .getItemDetails()
            }
            return null

        }
    }
}

interface CategoryPageDetailItemClick {
    fun onItemClicked(imageUrl: String, position: Int)
}