package com.minorproject.cloudgallery.views.pages.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.databinding.FragmentUserProfileBinding
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image
import com.minorproject.cloudgallery.viewmodels.CategoryViewModel
import com.minorproject.cloudgallery.viewmodels.UserDetailBinderClass
import com.minorproject.cloudgallery.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.collapse_toolbar.*
import kotlinx.android.synthetic.main.collapse_toolbar.toolbar
import kotlinx.android.synthetic.main.fragment_category_detail_page.*
import java.math.RoundingMode
import java.text.DecimalFormat


class UserProfile : Fragment() {
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentUserProfileBinding

    companion object {
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 102
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(UserViewModel::class.java)

        categoryViewModel = ViewModelProviders.of(activity!!)
            .get(CategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_profile, container, false
        )
        binding.userDetail.context = this
        binding.userDetail.viewModel = viewModel
        binding.userDetail.binder = UserDetailBinderClass(activity!!)

        binding.collapseToolbar.context = this

        binding.collapseToolbar.context = this
        binding.collapseToolbar.viewModel = viewModel
        binding.collapseToolbar.binder = UserDetailBinderClass(activity!!)

        binding.progressMenu.context = this
        binding.progressMenu.viewModel = viewModel
        binding.progressMenu.binder = UserDetailBinderClass(activity!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userMutableLiveData.observe(
            requireActivity(),
            Observer { user ->

                binding.userDetail.user = user
                binding.collapseToolbar.user = user
                binding.progressMenu.user = user

            }
        )

        categoryViewModel.allCategories.observe(
            requireActivity(),
            Observer { category ->
                val size = getTotalStorageSize(category)
                val totalSize = "%.2f".format(size).toDouble().toString() + " MB / 1024 MB"

                binding.progressMenu.size = totalSize

                var progress = 0.0
                if (size != null) {
                    progress = (size / 1024) * 100
                }

                binding.progressMenu.progress = progress.toInt()
            })

        avatar.setOnClickListener {
            selectImageInAlbum()
        }
    }

    private fun getTotalStorageSize(category: ArrayList<Category>?): Double? {
        var size = 0.0
        category?.forEach { item ->
            if (item.ImagesList != null) {
                item.ImagesList.forEach { image ->
                    size += image.size
                }
                size /= (1024 * 1024)
            }
        }
        return size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            viewModel.setProfilePic(data?.data!!)
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
}
