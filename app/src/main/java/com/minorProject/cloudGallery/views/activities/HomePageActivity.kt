package com.minorProject.cloudGallery.views.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.views.fragments.category.CategoryPage
import com.minorProject.cloudGallery.views.fragments.profile.UserProfile
import com.minorProject.cloudGallery.views.fragments.timeline.Timeline
import kotlinx.android.synthetic.main.a_home_page.*

/**
 * HomePage Activity -- After Login or register
 */
@RequiresApi(Build.VERSION_CODES.O)
class HomePageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_home_page)

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
            PagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        viewpager.currentItem = 1
        viewpager.addOnPageChangeListener(this)
    }

    override fun onBackPressed() {
        if(viewpager.currentItem == 1){
            super.onBackPressed()
        }
        else{
            viewpager.currentItem = 1
        }
    }

    /**
     * Pager Adapter class
     */
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
                    CategoryPage()
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


