package com.minorproject.cloudgallery.views.interfaces

import com.minorproject.cloudgallery.model.Category

interface HomeItemClick {
    fun onItemClicked(category: Category)
}