package com.minorProject.cloudGallery.viewModels

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.User
import kotlinx.android.synthetic.main.fragment_auth_forget_password.view.*

/**
 * ForgotPassword dataBinding class
 */
class ForgotPasswordBinderClass: BaseObservable() {
    private var user: User = User()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

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

    /**
     * click-handler for forgot password
     */
    fun onForgetPassword(view: View) {
        val email = getUserEmail()
        val context = view.context

        if (TextUtils.isEmpty(email)) {
            view.email_EditText_forgot.error = context.getString(R.string.empty_email)
        } else if (!LoginBinderClass.validEmail(email.toString())) {
            view.email_EditText_forgot.error = context.getString(R.string.invaild_email)
        } else {
            view.progressbar_forget_pass.visibility = View.VISIBLE
            mAuth.sendPasswordResetEmail(email.toString())
                .addOnCompleteListener { task ->
                    view.progressbar_forget_pass.visibility = View.GONE
                    if (task.isSuccessful) {
                        val message = "Email sent to ${email}."
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

    private fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    /**
     * function to updateUI
     */
    private fun updateUI(view : View) {
        val navController: NavController = Navigation.findNavController(view)
        navController.navigate(
            R.id.action_forgetPassword_to_loginScreen3
        )
    }
}