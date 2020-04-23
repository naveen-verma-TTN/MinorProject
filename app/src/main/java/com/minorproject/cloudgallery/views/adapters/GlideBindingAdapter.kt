package com.minorproject.cloudgallery.views.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.Placeholder
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.minorproject.cloudgallery.R

/**
 * GlideBindingAdapter
 */
object GlideBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(view: ImageView?, imageUrl: String?) {
        if(imageUrl != null){
            Glide.with(view!!.context).load(imageUrl.trim()).placeholder(R.drawable.icon).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("userProfileImageUrl")
    fun setUserProfileImage(view: ImageView?, imageUrl: String?) {
        Log.e("sd: ",imageUrl.toString())
        if(imageUrl != null){
            Glide.with(view!!.context).load(imageUrl.trim()).placeholder(R.drawable.user_icon).into(view)
        }
    }
}