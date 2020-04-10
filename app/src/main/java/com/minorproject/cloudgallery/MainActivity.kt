package com.minorproject.cloudgallery

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            main_logo_layout.visibility = View.GONE
        }, SPLASH_TIME_OUT+40)

        Handler().postDelayed({
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            fragmentTransaction
                .replace(R.id.nav_host_fragment, MainFragment())
                .commit()
        }, SPLASH_TIME_OUT)
    }
}
