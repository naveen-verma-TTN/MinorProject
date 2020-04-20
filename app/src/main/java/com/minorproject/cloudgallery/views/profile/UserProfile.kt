package com.minorproject.cloudgallery.views.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.repo.UploadImageWorker
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
        viewModel = ViewModelProviders.of(this)
            .get(UserViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
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
                user?.UserProfile.let {
                    Glide.with(view.context!!).load(it)
                        .placeholder(ContextCompat.getDrawable(view.context,R.drawable.user_icon)).into(avatar)
                }
            }
        )

        avatar.setOnClickListener {
            selectImageInAlbum()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            viewModel.setProfilePic(data?.data!!)
            Glide.with(view?.context!!).load(data.data)
                .placeholder(ContextCompat.getDrawable(view?.context!!,R.drawable.user_icon)).into(avatar)
        }
    }

    companion object {
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 102
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
}
