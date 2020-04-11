package com.minorproject.cloudgallery.screens.auth

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.components.OnSwipeTouchListener
import com.minorproject.cloudgallery.components.SwipeTouchListener
import com.minorproject.cloudgallery.screens.HomePage
import kotlinx.android.synthetic.main.fragment_login_screen.*
import kotlinx.android.synthetic.main.fragment_login_screen.password_EditText
import kotlinx.android.synthetic.main.fragment_login_screen.password_toggle
import kotlinx.android.synthetic.main.fragment_login_screen.register_title
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginScreen : Fragment(), View.OnClickListener, SwipeTouchListener {
    private var navController: NavController? = null
    private lateinit var mAuth: FirebaseAuth

    private var mProgressBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        register_title.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)
        login_Button.setOnClickListener(this)
        mProgressBar = ProgressDialog(view.context)
        password_toggle.setOnTouchListener(View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> password_EditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                MotionEvent.ACTION_DOWN -> password_EditText.inputType = InputType.TYPE_CLASS_TEXT
            }
            true
        })

        view.setOnTouchListener(OnSwipeTouchListener(view.context, this))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.register_title -> navController?.navigate(
                R.id.action_loginScreen3_to_registration
            )
            R.id.forgotPassword_title -> navController?.navigate(
                R.id.action_loginScreen3_to_forgetPassword
            )
            R.id.login_Button -> {
                if (TextUtils.isEmpty(email_EditText_login.text)) {
                    email_EditText_login.error = getString(R.string.empty_email)
                } else if (!validEmail(email_EditText_login.text.toString())) {
                    email_EditText_login.error = getString(R.string.invaild_email)
                } else if (TextUtils.isEmpty(password_EditText.text)) {
                    password_EditText.error = getString(R.string.empty_password)
                } else if (!(validPassword(password_EditText.text.toString())
                            && password_EditText.text.length >= 6)
                ) {
                    password_EditText.error = getString(R.string.invaild_password)
                } else {
                    mProgressBar!!.setMessage(getString(R.string.progress_login))
                    mProgressBar!!.show()
                    mAuth.signInWithEmailAndPassword(
                            email_EditText_login.text.toString(),
                            password_EditText.text.toString()
                        )
                        .addOnCompleteListener { task ->
                            mProgressBar!!.hide()
                            if (task.isSuccessful) {
                                // Sign in success, update UI with signed-in user's information
                                Log.d(TAG, getString(R.string.user_email_success))
                                updateUI()
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, getString(R.string.user_email_failure), task.exception)
                                view?.snack(task.exception?.message.toString())
                            }
                        }
                }
            }
        }
    }

    /**
     * function to validate email pattern
     */
    private fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    /**
     * function to validate password pattern
     */
    private fun validPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    /**
     * function to updateUI
     */
    private fun updateUI() {
        val intent = Intent(this.context, HomePage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity?.finish()
    }

    override fun onSwipeRight() {

    }

    override fun onSwipeLeft() {
        navController?.navigate(
            R.id.action_loginScreen3_to_registration
        )
    }

    override fun onSwipeTop() {
    }

    override fun onSwipeBottom() {
    }
}
