package com.minorproject.cloudgallery.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minorproject.cloudgallery.R
import kotlinx.android.synthetic.main.main_page_layout.*

class MainPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)

        menu.setItemSelected(R.id.home, true)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
