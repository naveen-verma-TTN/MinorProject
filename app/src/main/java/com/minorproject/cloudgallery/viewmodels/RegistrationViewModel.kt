package com.minorproject.cloudgallery.viewmodels

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.minorproject.cloudgallery.BR
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.views.HomePageActivity
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_EditText
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_toggle
import kotlinx.android.synthetic.main.fragment_auth_registration.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistrationViewModel : BaseObservable() {
    private var user: User = User()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressDialog

    companion object {
        private const val TAG: String = "RegistrationViewModel"
    }

    fun setUserName(name: String?) {
        user.UserName = name!!
        notifyPropertyChanged(BR.userEmail)
    }

    @Bindable
    fun getUserName(): String? {
        return user.UserName
    }

    fun setUserEmail(email: String?) {
        user.UserEmail = email!!
        notifyPropertyChanged(BR.userEmail)
    }

    @Bindable
    fun getUserEmail(): String? {
        return user.UserEmail
    }

    @Bindable
    fun getUserPassword(): String? {
        return user.UserPassword
    }

    fun setUserPassword(password: String?) {
        user.UserPassword = password!!
        notifyPropertyChanged(BR.userPassword)
    }

    fun passToggle(view: View) {
        view.password_toggle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> view.password_EditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                MotionEvent.ACTION_DOWN -> view.password_EditText.inputType =
                    InputType.TYPE_CLASS_TEXT
            }
            true
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun onRegisterClicked(view: View) {
        val username = getUserName()
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context
        val progressBar = ProgressDialog(context)
        if (TextUtils.isEmpty(username)) {
            view.username_EditText.error = context.getString(R.string.empty_username)
        } else if (TextUtils.isEmpty(email)) {
            view.email_EditText.error = context.getString(R.string.empty_email)
        } else if (!validEmail(email.toString())) {
            view.email_EditText.error = context.getString(R.string.invaild_email)
        } else if (TextUtils.isEmpty(pass)) {
            view.password_EditText.error = context.getString(R.string.empty_password)
        } else if (!(validPassword(pass.toString())
                    && pass.toString().length >= 6)
        ) {
            view.password_EditText.error = context.getString(R.string.invaild_password)
        } else {
            progressBar.setMessage(context.getString(R.string.progress_registration))
            progressBar.show()
            mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(
                    email.toString(),
                    pass.toString()
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        val profileUpdates: UserProfileChangeRequest =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(username.toString()).build()

                        firebaseUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener {
                                if (task.isSuccessful) {
                                    Log.d(TAG, context.getString(R.string.success_update_profile))
                                }
                            }
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, context.getString(R.string.success_register_user))
                        val userId = mAuth.currentUser!!.uid
                        //Verify Email

                        //update user profile information

                        val data = HashMap<String, Any>()

                        data["UserId"] = userId
                        data["UserName"] = username.toString()
                        data["UserEmail"] = email.toString()
                        data["AccountCreatedOn"] =  SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            .format(Date())

                        FirebaseFirestore.getInstance().collection("UserDetails").document(userId)
                            .set(data)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                progressBar.dismiss()
                                verifyEmail(view)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error writing document", e)
                                progressBar.dismiss()
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, context.getString(R.string.error_register_user), task.exception)
                        progressBar.dismiss()
                        view.snack(task.exception?.message.toString())
                    }
                }
        }
    }

    /**
     * verification mail to verify user
     */
    private fun verifyEmail(view : View) {
        val mUser = mAuth.currentUser
        mUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        view.context,
                        view.context.getString(R.string.success_verification_mail) + mUser.email,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(ContentValues.TAG, view.context.getString(R.string.error_verification_mail), task.exception)
                    Toast.makeText(
                        view.context,
                        view.context.getString(R.string.error_verification_mail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                updateUserInfoAndUI(view)
            }
    }

    /**
     * function to updateUI
     */
    private fun updateUserInfoAndUI(view : View) {
        val intent = Intent(view.context, HomePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        view.context.startActivity(intent)
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
    private fun updateUI(view: View) {
        val intent = Intent(view.context, HomePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        view.context.startActivity(intent)
    }
}