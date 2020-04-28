package com.minorproject.cloudgallery.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.databinding.FragmentAuthRegistrationBinding
import com.minorproject.cloudgallery.viewmodels.RegistrationBinderClass
import com.minorproject.cloudgallery.views.adapters.OnSwipeTouchListener
import com.minorproject.cloudgallery.views.adapters.SwipeTouchListener
import kotlinx.android.synthetic.main.fragment_auth_forget_password.login_title
import kotlinx.android.synthetic.main.fragment_auth_registration.*


class Registration : Fragment(), View.OnClickListener,
    SwipeTouchListener {
    var navController: NavController? = null
    private lateinit var mAuth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAuthRegistrationBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_auth_registration, container, false
        )
        binding.fragment = this
        binding.viewModel = RegistrationBinderClass()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        login_title.setOnClickListener(this)
        register_Button.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)

        view.setOnTouchListener(
            OnSwipeTouchListener(
                view.context,
                this
            )
        )
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

