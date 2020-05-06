package com.minorProject.cloudGallery.views.fragments.auth

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.databinding.FAuthLoginScreenBinding
import com.minorProject.cloudGallery.model.bindingClass.LoginBinderClass
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.views.adapters.OnSwipeTouchListener
import com.minorProject.cloudGallery.views.adapters.SwipeTouchListener
import kotlinx.android.synthetic.main.f_auth_login_screen.*

/**
 * Login fragment
 */
class LoginScreenFragment : Fragment(), View.OnClickListener, SwipeTouchListener {
    private var navController: NavController? = null
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(requireActivity())
            .get(AuthViewModel::class.java)
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
