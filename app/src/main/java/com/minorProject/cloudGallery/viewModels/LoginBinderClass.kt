package com.minorProject.cloudGallery.viewModels

import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.User
import com.minorProject.cloudGallery.views.HomePageActivity
import com.minorProject.cloudGallery.SplashScreenActivity
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Login dataBinding class
 */
class LoginBinderClass : BaseObservable() {
    private var user: User = User()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
    fun onLoginClicked(view: View) {
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context
        if (TextUtils.isEmpty(email)) {
            view.email_EditText_login.error = context.getString(R.string.empty_email)
        } else if (!validEmail(email.toString())) {
            view.email_EditText_login.error = context.getString(R.string.invaild_email)
        } else if (TextUtils.isEmpty(pass)) {
            view.password_EditText.error = context.getString(R.string.empty_password)
        } else if (!(validPassword(pass.toString())
                    && pass.toString().length >= 6)
        ) {
            view.password_EditText.error = context.getString(R.string.invaild_password)
        } else {
            view.progressbar.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(
                    email.toString(),
                    pass.toString()
                )
                .addOnCompleteListener { task ->
                    view.progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Log.d(TAG, context.getString(R.string.user_email_success))
                        updateUI(view)
                    } else {
                        Log.w(TAG, context.getString(R.string.user_email_failure), task.exception)
                        view.snack(task.exception?.message.toString())
                    }
                }
        }
    }

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    companion object {
        private const val TAG: String = "LoginViewModel"

        private const val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        /**
         * function to validate email pattern
         */
        fun validEmail(email: String): Boolean {
            val pattern: Pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }

        /**
         * function to validate password pattern
         */
        fun validPassword(password: String?): Boolean {
            val pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
            val matcher: Matcher = pattern.matcher(password.toString())
            return matcher.matches()
        }

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