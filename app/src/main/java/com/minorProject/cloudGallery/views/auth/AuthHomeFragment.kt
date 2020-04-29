package com.minorProject.cloudGallery.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minorProject.cloudGallery.R

/**
 * AuthHome Fragment (Main class)
 */
class AuthHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_home_screen, container, false)
    }
}