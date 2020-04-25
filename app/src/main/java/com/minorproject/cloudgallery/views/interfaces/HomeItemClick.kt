package com.minorproject.cloudgallery.views.interfaces

import com.minorproject.cloudgallery.model.Category

interface HomeItemClick {
    fun onItemClicked(category: Category, position: Int)
    fun onItemClicked(imageUrl: String, position: Int)
}