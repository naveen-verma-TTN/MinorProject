package com.minorProject.cloudGallery.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.databinding.FragmentAuthLoginScreenBinding
import com.minorProject.cloudGallery.viewModels.LoginBinderClass
import com.minorProject.cloudGallery.views.adapters.OnSwipeTouchListener
import com.minorProject.cloudGallery.views.adapters.SwipeTouchListener
import kotlinx.android.synthetic.main.fragment_auth_login_screen.*

/**
 * Login fragment
 */
class LoginScreen : Fragment(), View.OnClickListener, SwipeTouchListener {
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAuthLoginScreenBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_auth_login_screen, container, false
        )
        binding.fragment = this
        binding.viewModel = LoginBinderClass()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        view.setOnTouchListener(
            OnSwipeTouchListener(
                view.context,
                this
            )
        )

        register_title.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.register_title -> navController?.navigate(
                R.id.action_loginScreen3_to_registration
            )
            R.id.forgotPassword_title -> navController?.navigate(
                R.id.action_loginScreen3_to_forgetPassword
            )
        }
    }

    /**
     * Swipe CallBacks
     */
    override fun onSwipeRight() {}

    override fun onSwipeLeft() {
        navController?.navigate(
            R.id.action_loginScreen3_to_registration
        )
    }

    override fun onSwipeTop() {}

    override fun onSwipeBottom() {}
}
