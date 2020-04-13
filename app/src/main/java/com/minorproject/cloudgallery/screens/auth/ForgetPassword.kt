package com.minorproject.cloudgallery.screens.auth

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.components.OnSwipeTouchListener
import com.minorproject.cloudgallery.components.SwipeTouchListener
import kotlinx.android.synthetic.main.fragment_auth_forget_password.*
import kotlinx.android.synthetic.main.fragment_auth_forget_password.login_title
import kotlinx.android.synthetic.main.fragment_auth_forget_password.register_title
import java.util.regex.Pattern

class ForgetPassword : Fragment(), View.OnClickListener, SwipeTouchListener {
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
        return inflater.inflate(R.layout.fragment_auth_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        login_title.setOnClickListener(this)
        register_title.setOnClickListener(this)
        submit_button.setOnClickListener(this)
        mProgressBar = ProgressDialog(view.context)

        view.setOnTouchListener(OnSwipeTouchListener(view.context, this))
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_loginScreen3
            )
            R.id.register_title -> navController!!.navigate(
                R.id.action_forgetPassword_to_registration
            )
            R.id.submit_button -> {
                if (TextUtils.isEmpty(email_EditText_forgot.text)) {
                    email_EditText_forgot.error = getString(R.string.empty_email)
                } else if (!validEmail(email_EditText_forgot.text.toString())) {
                    email_EditText_forgot.error = getString(R.string.invaild_email)
                } else {
                    mProgressBar!!.setMessage(getString(R.string.forgotPassword_progressBar))
                    mProgressBar!!.show()
                    mAuth.sendPasswordResetEmail(email_EditText_forgot.text.toString())
                        .addOnCompleteListener { task ->
                            mProgressBar!!.hide()
                            if (task.isSuccessful) {
                                val message = "Email sent to ${email_EditText_forgot.text}."
                                Log.d(TAG, message)
                                Toast.makeText(view?.context, message, Toast.LENGTH_SHORT).show()
                                updateUI()
                            } else {
                                Log.w(
                                    ContentValues.TAG,
                                    getString(R.string.user_email_failure),
                                    task.exception
                                )
                                view?.snack(task.exception?.message.toString())
                            }
                        }
                }
            }
        }
    }

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    /**
     * function to updateUI
     */
    private fun updateUI() {
        navController!!.navigate(
            R.id.action_forgetPassword_to_loginScreen3
        )
    }

    /**
     * function to validate email pattern
     */
    private fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    override fun onSwipeRight() {
        navController?.navigate(
            R.id.action_forgetPassword_to_registration
        )
    }

    override fun onSwipeLeft() {
    }

    override fun onSwipeTop() {
    }

    override fun onSwipeBottom() {
    }
}
