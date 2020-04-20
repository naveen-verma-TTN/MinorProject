package com.minorproject.cloudgallery.views.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * GlideBindingAdapter
 */
object GlideBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(view: ImageView?, imageUrl: String) {
        Glide.with(view!!.context).load(imageUrl.trim()).into(view)
    }
}