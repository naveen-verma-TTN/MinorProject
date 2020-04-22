package com.minorproject.cloudgallery.views.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.databinding.FragmentUserDetailsBinding
import com.minorproject.cloudgallery.viewmodels.UserDetailBinderClass
import com.minorproject.cloudgallery.viewmodels.UserViewModel
import org.jetbrains.anko.view


class UserDetails : Fragment() {

    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentUserDetailsBinding

    companion object {
        private const val TAG: String = "UserDetails"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!)
            .get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_details, container, false
        )
        binding.context = this
        binding.viewModel = viewModel
        binding.binder = UserDetailBinderClass(activity!!)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userMutableLiveData.observe(requireActivity(), Observer { user ->
            binding.user = user
        })
    }


}