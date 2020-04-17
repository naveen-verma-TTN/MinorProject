package com.minorproject.cloudgallery.views.profile

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
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.viewmodels.UserViewModel
import com.minorproject.cloudgallery.views.SplashScreenActivity
import kotlinx.android.synthetic.main.collapse_toolbar.*
import kotlinx.android.synthetic.main.collapse_toolbar.view.*
import kotlinx.android.synthetic.main.progress_menu.*


class UserProfile : Fragment() {
    private lateinit var viewModel: UserViewModel
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(UserViewModel::class.java)
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

        logout_button.setOnClickListener {
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                val intent = Intent(view.context, SplashScreenActivity::class.java)
                val bundle = Bundle()
                bundle.putString("Key", "LOGOUT")
                intent.putExtras(bundle)
                mAuth.signOut()
                startActivity(intent)
                activity!!.finish()
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
