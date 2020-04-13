package com.minorproject.cloudgallery.screens.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.viewmodels.UserProfileViewModel
import kotlinx.android.synthetic.main.collapse_toolbar.*
import kotlinx.android.synthetic.main.collapse_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_auth_registration.view.*
import kotlinx.android.synthetic.main.main_page_layout.*


class UserProfile : Fragment() {
    private lateinit var navController: NavController
    private lateinit var viewModel: UserProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(UserProfileViewModel::class.java)
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

        val viewPager = view.findViewById(R.id.viewpager) as ViewPager
        viewPager.adapter = PagerAdapter(activity?.supportFragmentManager)

        val tabLayout = view.findViewById(R.id.tab_layout) as TabLayout

        tabLayout.setupWithViewPager(viewPager)

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


    internal class PagerAdapter(fragmentManager: FragmentManager?) :
        FragmentStatePagerAdapter(fragmentManager!!) {
        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                UserDetails()
            } else {
                EditDetails()
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position == 0) {
                "User Details"
            } else {
                "Edit Details"
            }
        }
    }
}
