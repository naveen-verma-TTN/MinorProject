package com.minorProject.cloudGallery.view.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.databinding.FAuthRegistrationBinding
import com.minorProject.cloudGallery.model.bindingClass.RegistrationBinderClass
import com.minorProject.cloudGallery.view.adapters.OnSwipeTouchListener
import com.minorProject.cloudGallery.view.adapters.SwipeTouchListener
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.viewModels.MyViewModelFactory
import kotlinx.android.synthetic.main.f_auth_forget_password.login_title
import kotlinx.android.synthetic.main.f_auth_registration.*
import kotlinx.android.synthetic.main.f_auth_registration.forgotPassword_title

/**
 * Registration fragment
 */
class RegistrationFragment : Fragment(), View.OnClickListener,
    SwipeTouchListener {
    private var navController: NavController? = null
    private val authViewModel by lazy {
        ViewModelProvider(requireActivity(), MyViewModelFactory()).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FAuthRegistrationBinding = DataBindingUtil.inflate(
            inflater, R.layout.f_auth_registration, container, false
        )

        // f_auth_registration layout
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.binder = RegistrationBinderClass(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setUpListeners()
    }

    /**
     * fun for set up Listeners
     */
    private fun setUpListeners() {
        view?.setOnTouchListener(
            OnSwipeTouchListener(
                requireView().context,
                this
            )
        )
        login_title.setOnClickListener(this)
        register_Button.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_title -> navController?.navigate(
                R.id.action_registration_to_loginScreen3
            )
            R.id.forgotPassword_title -> navController?.navigate(
                R.id.action_registration_to_forgetPassword
            )
        }
    }

    /**
     * Swipe CallBacks
     */

    override fun onSwipeRight() {
        navController?.navigate(
            R.id.action_registration_to_loginScreen3
        )
    }

    override fun onSwipeLeft() {
        navController?.navigate(
            R.id.action_registration_to_forgetPassword
        )
    }

    override fun onSwipeTop() {}

    override fun onSwipeBottom() {}
}

