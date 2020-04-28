package com.minorproject.cloudgallery.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.databinding.FragmentAuthForgetPasswordBinding
import com.minorproject.cloudgallery.viewmodels.ForgetPasswordBinderClass
import com.minorproject.cloudgallery.views.adapters.OnSwipeTouchListener
import com.minorproject.cloudgallery.views.adapters.SwipeTouchListener
import kotlinx.android.synthetic.main.fragment_auth_forget_password.*
import kotlinx.android.synthetic.main.fragment_auth_forget_password.login_title
import kotlinx.android.synthetic.main.fragment_auth_forget_password.register_title

class ForgetPassword : Fragment(), View.OnClickListener,
    SwipeTouchListener {
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAuthForgetPasswordBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_auth_forget_password, container, false
        )
        binding.fragment = this
        binding.viewModel = ForgetPasswordBinderClass()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        login_title.setOnClickListener(this)
        register_title.setOnClickListener(this)
        submit_button.setOnClickListener(this)

        view.setOnTouchListener(
            OnSwipeTouchListener(
                view.context,
                this
            )
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_loginScreen3
            )
            R.id.register_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_registration
            )
        }
    }


    override fun onSwipeRight() {
        navController?.navigate(
            R.id.action_forgetPassword_to_registration
        )
    }

    override fun onSwipeLeft() {}

    override fun onSwipeTop() {}

    override fun onSwipeBottom() {}
}
