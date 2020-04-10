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
import kotlinx.android.synthetic.main.fragment_forget_password.*

class ForgetPassword : Fragment(), View.OnClickListener {
    var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        login_title.setOnClickListener(this)
        register_title.setOnClickListener(this)
        submit_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_loginScreen3
            )
            R.id.register_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_registration
            )
            R.id.submit_button -> Toast.makeText(
                view?.context,
                "submit forgetPassword",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
