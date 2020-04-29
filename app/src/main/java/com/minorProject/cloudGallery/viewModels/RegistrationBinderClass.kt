package com.minorProject.cloudGallery.viewModels

import android.annotation.SuppressLint
import android.content.ContentValues
import android.text.InputType
import android.text.TextUtils
import android.util.Log
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
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.User
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.*
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_EditText
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_toggle
import kotlinx.android.synthetic.main.fragment_auth_registration.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Registration dataBinding class
 */
class RegistrationBinderClass : BaseObservable() {
    private var user: User = User()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    /**
     * function to show/hide the password
     */
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

    /**
     * click-handler for Register
     */
    @SuppressLint("SimpleDateFormat")
    fun onRegisterClicked(view: View) {
        val username = getUserName()
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context
        if (TextUtils.isEmpty(username)) {
            view.username_EditText.error = context.getString(R.string.empty_username)
        } else if (TextUtils.isEmpty(email)) {
            view.email_EditText.error = context.getString(R.string.empty_email)
        } else if (!LoginBinderClass.validEmail(email.toString())) {
            view.email_EditText.error = context.getString(R.string.invaild_email)
        } else if (TextUtils.isEmpty(pass)) {
            view.password_EditText.error = context.getString(R.string.empty_password)
        } else if (!(LoginBinderClass.validPassword(pass.toString())
                    && pass.toString().length >= 6)
        ) {
            view.password_EditText.error = context.getString(R.string.invaild_password)
        } else {
            view.progressbar_register.visibility = View.VISIBLE
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
                        Log.d(TAG, context.getString(R.string.success_register_user))
                        val userId = mAuth.currentUser!!.uid

                        //update user profile information
                        val data = HashMap<String, Any>()

                        data["UserId"] = userId
                        data["UserName"] = username.toString()
                        data["UserEmail"] = email.toString()
                        data["AccountCreatedOn"] = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            .format(Date())

                        FirebaseFirestore.getInstance().collection("UserDetails").document(userId)
                            .set(data)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                view.progressbar_register.visibility = View.GONE
                                verifyEmail(view)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error writing document", e)
                                view.progressbar.visibility = View.GONE
                            }
                    } else {
                        Log.w(TAG, context.getString(R.string.error_register_user), task.exception)
                        view.progressbar.visibility = View.GONE
                        view.snack(task.exception?.message.toString())
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
    private fun verifyEmail(view: View) {
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
                    Log.e(
                        ContentValues.TAG,
                        view.context.getString(R.string.error_verification_mail),
                        task.exception
                    )
                    Toast.makeText(
                        view.context,
                        view.context.getString(R.string.error_verification_mail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                LoginBinderClass.updateUI(view)
            }
    }
}