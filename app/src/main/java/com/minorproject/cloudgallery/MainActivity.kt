package com.minorproject.cloudgallery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.screens.HomePage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 1000
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance();

        Handler().postDelayed({
            if(mAuth.currentUser==null){
                Handler().postDelayed({
                    main_logo_layout.visibility = View.GONE
                }, 30)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                fragmentTransaction
                    .replace(R.id.nav_host_fragment, MainFragment())
                    .commit()
            }
            else{
                startActivity(Intent(this, HomePage::class.java))
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }
        }, SPLASH_TIME_OUT)
    }
}
