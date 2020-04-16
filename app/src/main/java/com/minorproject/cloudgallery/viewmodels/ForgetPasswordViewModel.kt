package com.minorproject.cloudgallery.viewmodels

import android.app.ProgressDialog
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.BR
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.User
import kotlinx.android.synthetic.main.fragment_auth_forget_password.view.*
import java.util.regex.Pattern

class ForgetPasswordViewModel: BaseObservable() {
    private var user: User = User()
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private const val TAG: String = "ForgetPasswordViewModel"
    }

    fun setUserEmail(email: String?) {
        user.UserEmail = email!!
        notifyPropertyChanged(BR.userEmail)
    }

    @Bindable
    fun getUserEmail(): String? {
        return user.UserEmail
    }



    fun onClicked(view: View) {
        val email = getUserEmail()
        val context = view.context
        val progressBar = ProgressDialog(context)
        if (TextUtils.isEmpty(email)) {
            view.email_EditText_forgot.error = context.getString(R.string.empty_email)
        } else if (!validEmail(email.toString())) {
            view.email_EditText_forgot.error = context.getString(R.string.invaild_email)
        } else {
            mAuth = FirebaseAuth.getInstance()
            progressBar.setMessage(context.getString(R.string.forgotPassword_progressBar))
            progressBar.show()
            mAuth.sendPasswordResetEmail(email.toString())
                .addOnCompleteListener { task ->
                    progressBar.hide()
                    if (task.isSuccessful) {
                        val message = "Email sent to ${email}."
                        Log.d(TAG, message)
                        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
                        updateUI(view)
                    } else {
                        Log.w(
                            TAG,
                            context.getString(R.string.user_email_failure),
                            task.exception
                        )
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


    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    /**
     * function to updateUI
     */
    private fun updateUI(view : View) {
        var navController: NavController? = null
        navController = Navigation.findNavController(view)
        navController.navigate(
            R.id.action_forgetPassword_to_loginScreen3
        )
    }
}