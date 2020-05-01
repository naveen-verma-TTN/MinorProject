package com.minorProject.cloudGallery.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.views.fragments.auth.AuthHomeFragment
import kotlinx.android.synthetic.main.a_splashscreen.*

/**
 * SplashScreen Activity (First Page)
 */
class SplashScreenActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_TIME_OUT: Long = 1000
        private val TAG: String = SplashScreenActivity::class.java.name
    }

    private lateinit var authViewModel: AuthViewModel

    override fun onStart() {
        super.onStart()
        authViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(AuthViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_splashscreen)

        // work for logout
        val value = intent?.extras?.getString("Key")
        Handler().postDelayed(
            {
                authViewModel.checkIfUserSignInOrNot().observe(this,
                    Observer { isSignIn ->
                        if (isSignIn) {
                            startActivity(Intent(this, HomePageActivity::class.java))
                            overridePendingTransition(
                                R.anim.enter,
                                R.anim.exit
                            )
                            finish()
                        } else {
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
                        }
                    })
            },
            SPLASH_TIME_OUT
        )
    }

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }
}
