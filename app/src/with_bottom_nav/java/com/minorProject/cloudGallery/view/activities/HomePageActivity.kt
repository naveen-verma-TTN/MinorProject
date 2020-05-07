package com.minorProject.cloudGallery.view.activities

import android.R.attr.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.view.fragments.category.CategoryPage
import com.minorProject.cloudGallery.view.fragments.profile.UserProfile
import com.minorProject.cloudGallery.view.fragments.timeline.Timeline
import kotlinx.android.synthetic.main.a_home_page.*


/**
 * HomePage Activity <Bottom-Navigation bar>-- After Login or register
 */
@RequiresApi(Build.VERSION_CODES.O)
class HomePageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var menu: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_home_page)

        menu = findViewById(R.id.bottom_menu)

        bottom_menu_layout.visibility = View.VISIBLE

        val pager = findViewById<ViewPager>(R.id.viewpager)
        val lp = pager.layoutParams as ViewGroup.MarginLayoutParams
        lp.bottomMargin += 170

        //default selection
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

        //setting up adapter
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


