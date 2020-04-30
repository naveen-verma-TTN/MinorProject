package com.minorProject.cloudGallery.model.bindingClass

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
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass
import com.minorProject.cloudGallery.util.HelperClass.snack
import com.minorProject.cloudGallery.util.HelperClass.validEmail
import com.minorProject.cloudGallery.util.HelperClass.validPassword
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.views.fragments.auth.RegistrationFragment
import kotlinx.android.synthetic.main.f_auth_login_screen.view.*
import kotlinx.android.synthetic.main.f_auth_login_screen.view.password_EditText
import kotlinx.android.synthetic.main.f_auth_login_screen.view.password_toggle
import kotlinx.android.synthetic.main.f_auth_registration.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Registration dataBinding class
 */
class RegistrationBinderClass(private val registrationFragment: RegistrationFragment) :
    BaseObservable() {
    private var user: User =
        User()

    companion object {
        private val TAG: String = RegistrationBinderClass::class.java.name
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
    fun onRegisterClicked(authViewModel: AuthViewModel, view: View) {
        val username = getUserName()
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context

        when {
            TextUtils.isEmpty(username) -> {
                view.username_EditText.error = context.getString(R.string.empty_username)
            }
            TextUtils.isEmpty(email) -> {
                view.email_EditText.error = context.getString(R.string.empty_email)
            }
            !validEmail(email!!) -> {
                view.email_EditText.error = context.getString(R.string.invaild_email)
            }
            TextUtils.isEmpty(pass) -> {
                view.password_EditText.error = context.getString(R.string.empty_password)
            }
            !(validPassword(pass) && pass?.length!! >= 6) -> {
                view.password_EditText.error = context.getString(R.string.invaild_password)
            }
            else -> {
                view.progressbar_register.visibility = View.VISIBLE
                authViewModel.onRegisterClicked(username!!, email, pass)
                    ?.observe(registrationFragment,
                        Observer { response ->
                            when (response) {
                                is Success -> {
                                    Toast.makeText(
                                        view.context,
                                        view.context.getString(R.string.success_verification_mail) + email,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view.progressbar_register.visibility = View.GONE
                                    LoginBinderClass.updateUI(view)
                                }
                                is Failure -> {
                                    view.progressbar_register.visibility = View.GONE
                                    view.snack(response.e.cause?.message.toString())
                                }
                            }
                        })
            }
        }
    }
}