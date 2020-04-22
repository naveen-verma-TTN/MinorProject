package com.minorproject.cloudgallery.views.auth

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.databinding.FragmentAuthLoginScreenBinding
import com.minorproject.cloudgallery.viewmodels.LoginBinderClass
import com.minorproject.cloudgallery.views.adapters.OnSwipeTouchListener
import com.minorproject.cloudgallery.views.interfaces.SwipeTouchListener
import kotlinx.android.synthetic.main.fragment_auth_login_screen.*


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

        val value = activity?.intent?.extras?.getString("Key")

        Handler().postDelayed({
            if (value == "LOGOUT") {
                view.snack(getString(R.string.logout_message))
            }
        }, 1000)

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

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    override fun onSwipeRight() {}

    override fun onSwipeLeft() {
        navController?.navigate(
            R.id.action_loginScreen3_to_registration
        )
    }

    override fun onSwipeTop() {}

    override fun onSwipeBottom() {}
}
