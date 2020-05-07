package com.minorProject.cloudGallery.model.bindingClass

import android.annotation.SuppressLint
import android.text.InputType
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.Observer
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.snack
import com.minorProject.cloudGallery.util.HelperClass.validEmail
import com.minorProject.cloudGallery.util.HelperClass.validPassword
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import com.minorProject.cloudGallery.views.fragments.auth.RegistrationFragment
import kotlinx.android.synthetic.main.f_auth_registration.view.*

/**
 * Registration dataBinding class
 */
class RegistrationBinderClass(private val registrationFragment: RegistrationFragment) :
    BaseObservable() {
    private var user: User =
        User()

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
                view.username_EditText.requestFocus()
            }
            TextUtils.isEmpty(email) -> {
                view.email_EditText.error = context.getString(R.string.empty_email)
                view.email_EditText.requestFocus()
            }
            !validEmail(email!!) -> {
                view.email_EditText.error = context.getString(R.string.invaild_email)
                view.email_EditText.requestFocus()
            }
            TextUtils.isEmpty(pass) -> {
                view.password_EditText.error = context.getString(R.string.empty_password)
                view.password_EditText.requestFocus()
            }
            !(validPassword(pass) && pass?.length!! >= 6) -> {
                view.password_EditText.error = context.getString(R.string.invaild_password)
                view.password_EditText.requestFocus()
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