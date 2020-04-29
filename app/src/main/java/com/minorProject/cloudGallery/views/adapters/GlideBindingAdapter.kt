package com.minorProject.cloudGallery.views.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.minorProject.cloudGallery.R

/**
 * GlideBindingAdapter
 */
object GlideBindingAdapter {
    /**
     * setting up image ------------ with different place holder (icon)
     */
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageResource(view: ImageView?, imageUrl: String?) {
        if(imageUrl != null){
            Glide.with(view!!.context).load(imageUrl.trim()).placeholder(R.drawable.icon).into(view)
        }
    }

    /**
     * setting up user profile image ------------ with different place holder (user_icon)
     */
    @JvmStatic
    @BindingAdapter("userProfileImageUrl")
    fun setUserProfileImage(view: ImageView?, imageUrl: String?) {
        if(imageUrl != null){
            Glide.with(view!!.context).load(imageUrl.trim()).placeholder(R.drawable.user_icon).into(view)
        }
    }
}