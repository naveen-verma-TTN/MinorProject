package com.minorproject.cloudgallery.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.views.home.Home
import com.minorproject.cloudgallery.views.profile.UserProfile
import com.minorproject.cloudgallery.views.timeline.Timeline
import kotlinx.android.synthetic.main.home_page_layout.*

class HomePageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page_layout)

        menu.setItemSelected(R.id.home, true)

        menu.setOnItemSelectedListener {
            when (it) {
                R.id.timeline -> {
                    viewpager.currentItem = 0
                }
                R.id.home -> {
                    viewpager.currentItem = 1
                }
                R.id.profile -> {
                    viewpager.currentItem = 2
                }
            }
        }

        viewpager.adapter =
            PagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewpager.currentItem = 1
        viewpager.addOnPageChangeListener(this)
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    internal class PagerAdapter(
        fragmentManager: FragmentManager?,
        behaviorResumeOnlyCurrentFragment: Int
    ) : FragmentStatePagerAdapter(fragmentManager!!, behaviorResumeOnlyCurrentFragment) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    Timeline()
                }
                1 -> {
                    Home()
                }
                else -> {
                    UserProfile()
                }
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> {
                    "Timeline"
                }
                1 -> {
                    "Home"
                }
                else -> {
                    "UserProfile"
                }
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                menu.setItemSelected(R.id.timeline, true)
            }
            1 -> {
                menu.setItemSelected(R.id.home, true)
            }
            else -> {
                menu.setItemSelected(R.id.profile, true)
            }
        }
    }
}


