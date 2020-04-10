package com.minorproject.cloudgallery.screens.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorproject.cloudgallery.R
import kotlinx.android.synthetic.main.fragment_login_screen.*


class LoginScreen : Fragment(), View.OnClickListener {
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        register_title.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)
        login_Button.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.register_title -> navController?.navigate(
                R.id.action_loginScreen3_to_registration
            )
            R.id.forgotPassword_title -> navController?.navigate(
                R.id.action_loginScreen3_to_forgetPassword
            )
            R.id.login_Button -> {
                Toast.makeText(view!!.context, "login", Toast.LENGTH_LONG).show()
            }
        }
    }
}
