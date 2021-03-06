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
import com.minorProject.cloudGallery.databinding.FAuthLoginScreenBinding
import com.minorProject.cloudGallery.model.bindingClass.LoginBinderClass
import com.minorProject.cloudGallery.view.adapters.OnSwipeTouchListener
import com.minorProject.cloudGallery.view.adapters.SwipeTouchListener
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.viewModels.MyViewModelFactory
import kotlinx.android.synthetic.main.f_auth_login_screen.*

/**
 * Login fragment
 */
class LoginScreenFragment : Fragment(), View.OnClickListener, SwipeTouchListener {
    private var navController: NavController? = null
    private val authViewModel by lazy {
        ViewModelProvider(requireActivity(), MyViewModelFactory()).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FAuthLoginScreenBinding = DataBindingUtil.inflate(
            inflater, R.layout.f_auth_login_screen, container, false
        )

        // f_auth_login_screen layout
        binding.fragment = this
        binding.viewModel = authViewModel
        binding.binder = LoginBinderClass(this)
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
