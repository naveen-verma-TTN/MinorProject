package com.minorproject.cloudgallery.views.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.minorproject.cloudgallery.R
import com.squareup.picasso.Picasso

/**
 * PicassoBindingAdapter
 */
object PicassoBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(view: ImageView?, imageUrl: String) {
        Picasso.get().load(imageUrl.trim()).placeholder(R.mipmap.icon).into(view)
    }
}