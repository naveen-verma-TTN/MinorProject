package com.minorProject.cloudGallery.view.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.view.fragments.category.CategoryPage
import com.minorProject.cloudGallery.view.fragments.profile.UserProfile
import com.minorProject.cloudGallery.view.fragments.timeline.Timeline
import kotlinx.android.synthetic.main.a_home_page.*


/**
 * HomePage Activity<Left-Navigation bar> -- After Login or register
 */
@RequiresApi(Build.VERSION_CODES.O)
class HomePageActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    private lateinit var menu: ChipNavigationBar

    private val container by lazy { findViewById<ViewGroup>(R.id.container) }
    private val button by lazy { findViewById<ImageView>(R.id.expand_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_home_page)

        menu = findViewById(R.id.left_side_menu)

        container.visibility = View.VISIBLE

        val pager = findViewById<ViewPager>(R.id.viewpager)
        val lp = pager.layoutParams as MarginLayoutParams
        lp.leftMargin += 190

        //default selection
        menu.setItemSelected(R.id.home, true)

        button.setOnClickListener {
            if (menu.isExpanded()) {
                button.rotation = 180.0F
                TransitionManager.beginDelayedTransition(container, ChangeBounds())
                menu.collapse()
            } else {
                button.rotation = 0.0F
                TransitionManager.beginDelayedTransition(container, ChangeBounds())
                menu.expand()
            }
        }

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


