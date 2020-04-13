package com.minorproject.cloudgallery.screens.auth

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.components.OnSwipeTouchListener
import com.minorproject.cloudgallery.components.SwipeTouchListener
import com.minorproject.cloudgallery.screens.MainPage
import kotlinx.android.synthetic.main.fragment_auth_forget_password.login_title
import kotlinx.android.synthetic.main.fragment_auth_registration.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Registration : Fragment(), View.OnClickListener, SwipeTouchListener {
    var navController: NavController? = null
    private lateinit var mAuth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private var mProgressBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        login_title.setOnClickListener(this)
        register_Button.setOnClickListener(this)
        forgotPassword_title.setOnClickListener(this)
        mProgressBar = ProgressDialog(view.context)
        password_toggle.setOnTouchListener(OnTouchListener { v, event ->
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
            R.id.login_title -> navController?.navigate(
                R.id.action_registration_to_loginScreen3
            )
            R.id.forgotPassword_title -> navController?.navigate(
                R.id.action_registration_to_forgetPassword
            )
            R.id.register_Button -> {
                if (TextUtils.isEmpty(username_EditText.text)) {
                    username_EditText.error = getString(R.string.empty_username)
                } else if (TextUtils.isEmpty(email_EditText.text)) {
                    email_EditText.error = getString(R.string.empty_email)
                } else if (!validEmail(email_EditText.text.toString())) {
                    email_EditText.error = getString(R.string.invaild_email)
                } else if (TextUtils.isEmpty(password_EditText.text)) {
                    password_EditText.error = getString(R.string.empty_password)
                } else if (!(validPassword(password_EditText.text.toString())
                            && password_EditText.text.length >= 6)
                ) {
                    password_EditText.error = getString(R.string.invaild_password)
                } else {
                    mProgressBar!!.setMessage(getString(R.string.progress_registration))
                    mProgressBar!!.show()
                    mAuth.createUserWithEmailAndPassword(
                            email_EditText.text.toString(),
                            password_EditText.text.toString()
                        )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? =
                                    FirebaseAuth.getInstance().currentUser
                                val profileUpdates: UserProfileChangeRequest =
                                    UserProfileChangeRequest.Builder()
                                        .setDisplayName(username_EditText.text.toString()).build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener {
                                        if (task.isSuccessful) {
                                            Log.d(TAG, getString(R.string.success_update_profile))
                                        }
                                    }
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, getString(R.string.success_register_user))
                                val userId = mAuth.currentUser!!.uid
                                //Verify Email
                                verifyEmail()
                                //update user profile information
                                val currentUserDb = mDatabaseReference!!.child(userId.trim())
                                currentUserDb.child("UserId").setValue(userId.trim())
                                currentUserDb.child("UserName")
                                    .setValue(username_EditText.text.toString().trim())
                                currentUserDb.child("UserEmail")
                                    .setValue(email_EditText.text.toString().trim())
                                currentUserDb.child("AccountCreatedOn")
                                    .setValue(getCurrentDateTime().trim())
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, getString(R.string.error_register_user), task.exception)
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
     * verification mail to verify user
     */
    private fun verifyEmail() {
        val mUser = mAuth.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        view?.context,
                        getString(R.string.success_verification_mail) + mUser.email,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, getString(R.string.error_verification_mail), task.exception)
                    Toast.makeText(
                        view?.context,
                        getString(R.string.error_verification_mail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                updateUserInfoAndUI()
            }
    }

    /**
     * function to updateUI
     */
    private fun updateUserInfoAndUI() {
        mProgressBar!!.hide()
        val intent = Intent(this.context, MainPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity?.finish()
    }

    /**
     * function to get current date and time
     */
    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
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

    override fun onSwipeRight() {
        navController?.navigate(
            R.id.action_registration_to_loginScreen3
        )
    }

    override fun onSwipeLeft() {
        navController?.navigate(
            R.id.action_registration_to_forgetPassword
        )
    }

    override fun onSwipeTop() {
    }

    override fun onSwipeBottom() {
    }
}

