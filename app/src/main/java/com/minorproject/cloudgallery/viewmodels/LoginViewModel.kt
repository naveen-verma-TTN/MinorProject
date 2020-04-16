package com.minorproject.cloudgallery.viewmodels

import android.app.ProgressDialog
import android.content.Intent
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.BR
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.views.HomePageActivity
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.*
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_EditText
import kotlinx.android.synthetic.main.fragment_auth_login_screen.view.password_toggle
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginViewModel : BaseObservable() {
    private var user: User = User()
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private const val TAG: String = "LoginViewModel"
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


    fun onLoginClicked(view: View) {
        val email = getUserEmail()
        val pass = getUserPassword()
        val context = view.context
        val progressBar = ProgressDialog(context)
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
            mAuth = FirebaseAuth.getInstance()
            progressBar.setMessage(context.getString(R.string.progress_login))
            progressBar.show()
            mAuth.signInWithEmailAndPassword(
                    email.toString(),
                    pass.toString()
                )
                .addOnCompleteListener { task ->
                    progressBar.dismiss()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, context.getString(R.string.user_email_success))
                        updateUI(view)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, context.getString(R.string.user_email_failure), task.exception)
                        view.snack(task.exception?.message.toString())
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
    private fun updateUI(view : View) {
        val intent = Intent(view.context, HomePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        view.context.startActivity(intent)
    }
}