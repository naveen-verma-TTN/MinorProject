package com.minorproject.cloudgallery.screens.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.SplashScreen
import com.minorproject.cloudgallery.viewmodels.UserProfileViewModel
import kotlinx.android.synthetic.main.collapse_toolbar.*
import kotlinx.android.synthetic.main.collapse_toolbar.view.*
import kotlinx.android.synthetic.main.main_page_layout.*
import kotlinx.android.synthetic.main.progress_menu.*


class UserProfile : Fragment() {
    private lateinit var navController: NavController
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(UserProfileViewModel::class.java)
        mAuth = FirebaseAuth.getInstance();
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        logout_button.setOnClickListener {
            Toast.makeText(view.context, "Logging out..", Toast.LENGTH_LONG).show()
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                mAuth.signOut()
                startActivity(Intent(this.context, SplashScreen::class.java))
                activity!!.finish()
            }
        }

        activity!!.menu.setOnItemSelectedListener {
            when (it) {
                R.id.home -> navController.navigate(R.id.action_userProfile_to_home2)
                R.id.timeline -> navController.navigate(R.id.action_userProfile_to_timeline2)
            }
        }

        viewModel.getUserData().observe(
            requireActivity(),
            Observer { user ->
                view.username_textView.text = user?.UserName
                view.userEmail_textView.text = user?.UserEmail
            })
    }
}
