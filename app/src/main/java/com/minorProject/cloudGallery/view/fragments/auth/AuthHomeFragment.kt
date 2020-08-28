package com.minorProject.cloudGallery.view.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minorProject.cloudGallery.R

/**
 * AuthHome Fragment (Main class)
 */
class AuthHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_auth_home_screen, container, false)
    }
}
