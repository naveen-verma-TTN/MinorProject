package com.minorProject.cloudGallery.model.bindingClass

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.minorProject.cloudGallery.BR
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.snack
import com.minorProject.cloudGallery.util.HelperClass.validEmail
import com.minorProject.cloudGallery.view.fragments.auth.ForgotPasswordFragment
import com.minorProject.cloudGallery.viewModels.AuthViewModel
import kotlinx.android.synthetic.main.f_auth_forget_password.view.*

/**
 * ForgotPassword dataBinding class
 */
class ForgotPasswordBinderClass(private val forgotPasswordFragment: ForgotPasswordFragment) :
    BaseObservable() {
    private var user: User = User()

    companion object {
        private val TAG: String = ForgotPasswordBinderClass::class.java.name
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
    fun onForgetPassword(authViewModel: AuthViewModel, view: View) {
        val email = getUserEmail()
        val context = view.context
        when {
            TextUtils.isEmpty(view.email_EditText_forgot.text) -> {
                view.email_EditText_forgot.error = context.getString(R.string.empty_email)
                view.email_EditText_forgot.requestFocus()
            }
            !validEmail(email.toString()) -> {
                view.email_EditText_forgot.error = context.getString(R.string.invaild_email)
                view.email_EditText_forgot.requestFocus()
            }
            else -> {
                view.progressbar_forget_pass.visibility = View.VISIBLE
                authViewModel.onForgetPassword(email).observe(forgotPasswordFragment,
                    Observer { response ->
                        when (response) {
                            is Success -> {
                                val message = "Email sent to ${email}."
                                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
                                view.progressbar_forget_pass.visibility = View.GONE
                                updateUI(view)
                            }
                            is Failure -> {
                                Log.w(
                                    TAG,
                                    context.getString(R.string.user_email_failure),
                                    response.e
                                )
                                view.progressbar_forget_pass.visibility = View.GONE
                                view.snack(response.e.cause?.message.toString())
                            }
                        }
                    })
            }
        }
    }

    /**
     * function to updateUI
     */
    private fun updateUI(view: View) {
        val navController: NavController = Navigation.findNavController(view)
        navController.navigate(
            R.id.action_forgetPassword_to_loginScreen3
        )
    }
}
