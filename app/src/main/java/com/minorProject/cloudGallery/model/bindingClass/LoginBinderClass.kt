package com.minorProject.cloudGallery.model.bindingClass

import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
import com.minorProject.cloudGallery.views.activities.HomePageActivity
import com.minorProject.cloudGallery.views.activities.SplashScreenActivity
import com.minorProject.cloudGallery.views.fragments.auth.LoginScreenFragment
import kotlinx.android.synthetic.main.f_auth_login_screen.view.*

/**
 * Login dataBinding class
 */
class LoginBinderClass(private val loginScreenFragment: LoginScreenFragment) : BaseObservable() {
    private var user: User =
        User()

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
    fun passToggle(view: View){
        view.password_toggle.setOnTouchListener{ _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> view.password_EditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                MotionEvent.ACTION_DOWN -> view.password_EditText.inputType = InputType.TYPE_CLASS_TEXT
            }
            true
        }
    }

    /**
     * click-handler for Login
     */
    fun onLoginClicked(authViewModel: AuthViewModel, view: View) {
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context
        when {
            TextUtils.isEmpty(email) -> {
                view.email_EditText_login.error = context.getString(R.string.empty_email)
            }
            !validEmail(email.toString()) -> {
                view.email_EditText_login.error = context.getString(R.string.invaild_email)
            }
            TextUtils.isEmpty(pass) -> {
                view.password_EditText.error = context.getString(R.string.empty_password)
            }
            !(validPassword(pass.toString()) && pass.toString().length >= 6) -> {
                view.password_EditText.error = context.getString(R.string.invaild_password)
            }
            else -> {
                view.progressbar.visibility = View.VISIBLE
                authViewModel.onLoginClicked(email!!, pass!!).observe(loginScreenFragment,
                    Observer { response ->
                        when (response) {
                            is Success -> {
                                Log.d(TAG, context.getString(R.string.user_email_success))
                                view.progressbar.visibility = View.GONE
                                updateUI(
                                    view
                                )
                            }
                            is Failure -> {
                                Log.w(
                                    TAG,
                                    context.getString(R.string.user_email_failure),
                                    response.e
                                )
                                view.progressbar.visibility = View.GONE
                                view.snack(response.e.cause?.message.toString())
                            }
                        }
                    })
            }
        }
    }


    companion object {
        private val TAG: String = LoginBinderClass::class.java.name

        /**
         * function to updateUI
         */
        fun updateUI(view : View) {
            val intent = Intent(view.context, HomePageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            view.context.startActivity(intent)
            (view.context as SplashScreenActivity).finish()
        }
    }


}