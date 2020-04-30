package com.minorProject.cloudGallery.views.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.databinding.FAuthForgetPasswordBinding
import com.minorProject.cloudGallery.model.bindingClass.ForgotPasswordBinderClass
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.viewModels.CategoryViewModel
import com.minorProject.cloudGallery.viewModels.UserViewModel
import com.minorProject.cloudGallery.views.adapters.OnSwipeTouchListener
import com.minorProject.cloudGallery.views.adapters.SwipeTouchListener
import kotlinx.android.synthetic.main.f_auth_forget_password.*
import kotlinx.android.synthetic.main.f_auth_forget_password.forgotPassword_title
import kotlinx.android.synthetic.main.f_auth_forget_password.login_title
import kotlinx.android.synthetic.main.f_auth_forget_password.register_title
import kotlinx.android.synthetic.main.f_auth_login_screen.*

/**
 * ForgotPassword fragment
 */
class ForgotPasswordFragment : Fragment(), View.OnClickListener,
    SwipeTouchListener {
    private var navController: NavController? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(activity!!)
            .get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FAuthForgetPasswordBinding = DataBindingUtil.inflate(
            inflater, R.layout.f_auth_forget_password, container, false
        )
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.binder = ForgotPasswordBinderClass(this)
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

        setUpListeners()
    }

    private fun setUpListeners() {
        login_title.setOnClickListener(this)
        register_title.setOnClickListener(this)
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

    /**
     * Swipe CallBacks
     */

    override fun onSwipeRight() {
        navController?.navigate(
            R.id.action_forgetPassword_to_registration
        )
    }

    override fun onSwipeLeft() {}

    override fun onSwipeTop() {}

    override fun onSwipeBottom() {}
}
