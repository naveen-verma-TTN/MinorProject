package com.minorProject.cloudGallery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorProject.cloudGallery.views.HomePageActivity
import com.minorProject.cloudGallery.views.auth.AuthHomeFragment
import kotlinx.android.synthetic.main.splashscreen_layout.*

/**
 * SplashScreen Activity (First Page)
 */
class SplashScreenActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_TIME_OUT: Long = 1000
    }

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen_layout)

        // work for logout
        val value = intent?.extras?.getString("Key")

        Handler().postDelayed({
            if (mAuth.currentUser == null) {
                Handler().postDelayed({
                    main_logo_layout.visibility = View.GONE
                    if (value == "LOGOUT") {
                        splashScreen_layout.snack(getString(R.string.logout_message))
                    }
                }, 5)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_up,
                    R.anim.slide_down
                )
                fragmentTransaction
                    .replace(
                        R.id.nav_host_fragment,
                        AuthHomeFragment()
                    )
                    .commit()
            } else {
                startActivity(Intent(this, HomePageActivity::class.java))
                overridePendingTransition(
                    R.anim.enter,
                    R.anim.exit
                )
                finish()
            }
        },
            SPLASH_TIME_OUT
        )
    }

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }
}
