package com.minorProject.cloudGallery.view.fragments.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.databinding.FUserProfileBinding
import com.minorProject.cloudGallery.model.bindingClass.UserDetailBinderClass
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.viewModels.MyViewModelFactory
import com.minorProject.cloudGallery.viewModels.UserViewModel
import kotlinx.android.synthetic.main.custom_collapse_toolbar.*

/**
 * UserProfile fragment
 */
class UserProfile : Fragment() {
    private lateinit var binding: FUserProfileBinding

    private val userViewModel by lazy {
        ViewModelProvider(requireActivity(), MyViewModelFactory()).get(UserViewModel::class.java)
    }

    private val categoriesViewModel by lazy {
        ViewModelProvider(requireActivity(), MyViewModelFactory()).get(CategoriesViewModel::class.java)
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE_IN_ALBUM = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.f_user_profile, container, false
        )

        //f_user_profile layout
        binding.userDetail.context = this
        binding.userDetail.viewModel = userViewModel
        binding.userDetail.binder =
            UserDetailBinderClass(
                requireActivity()
            )

        //custom_collapse_toolbar layout
        binding.collapseToolbar.context = this
        binding.collapseToolbar.viewModel = userViewModel
        binding.collapseToolbar.binder =
            UserDetailBinderClass(
                requireActivity()
            )

        //custom_user_page_progress_bar_menu layout
        binding.progressMenu.context = this
        binding.progressMenu.viewModel = userViewModel
        binding.progressMenu.binder =
            UserDetailBinderClass(
                requireActivity()
            )

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

        setUpObservers()

    }

    /**
     * fun for set up Listeners
     */
    private fun setUpListeners() {
        avatar.setOnClickListener {
            selectImageInAlbum()
        }
    }

    /**
     * fun for setting up observers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpObservers() {
        // Observer to observe the change in userMutableLiveData and update the ui
        userViewModel.getUserDetails().observe(
            requireActivity(),
            Observer { user ->
                binding.userDetail.user = user
                binding.collapseToolbar.user = user
                binding.progressMenu.user = user
            }
        )
        // Observer to observe the change in allCategories and update the size and progress-bar
        categoriesViewModel.getTotalSize().observe(
            requireActivity(),
            Observer { size ->
                val totalSize = "%.2f".format(size).toDouble().toString() + " MB / 500 MB"

                binding.progressMenu.size = totalSize

                var progress = 0.0
                if (size != null) {
                    progress = (size / 500) * 100
                }

                binding.progressMenu.progress = progress.toInt()
            })
    }

    /**
     * select image from gallery
     */
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            userViewModel.setProfilePic(data?.data!!)
        }
    }
}
