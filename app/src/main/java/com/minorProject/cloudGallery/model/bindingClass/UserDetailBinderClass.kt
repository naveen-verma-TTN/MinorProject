@file:Suppress("DEPRECATION")

package com.minorProject.cloudGallery.model.bindingClass

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.validEmail
import com.minorProject.cloudGallery.view.activities.HomePageActivity
import com.minorProject.cloudGallery.view.activities.SplashScreenActivity
import com.minorProject.cloudGallery.viewModels.UserViewModel
import kotlinx.android.synthetic.main.f_user_details.view.*
import java.util.*

/**
 * UserDetail dataBinding class
 */
class UserDetailBinderClass(
    private val activity: FragmentActivity
) : BaseObservable() {
    private var userDefault: User =
        User()
    var isEnable: Boolean = false
    var isVisibility: Int = VISIBLE

    companion object {
        private val TAG: String = UserDetailBinderClass::class.java.name
    }

    /**
     * fun to save values to the userDefault object
     */
    private fun saveValues(user: User, view: View) {
        userDefault = User(
            UserId = user.UserId,
            UserName = user.UserName,
            UserEmail = user.UserEmail,
            UserProfile = user.UserProfile,
            AccountCreatedOn = user.AccountCreatedOn,
            UserAdditionalEmail = view.user_detail_page_email.text.toString().trim(),
            UserPhoneNumber = view.user_detail_page_phone.text.toString().trim(),
            UserDOB = view.user_detail_page_dob.text.toString().trim(),
            UserAddress = view.user_detail_page_address.text.toString().trim()
        )
    }

    /**
     * fun to enable/disable EditText and other action button (edit, submit, cancel)
     */
    fun toggleButton(user: User, view: View) {
        saveValues(user, view)
        if (!isEnable) {
            isEnable = true
            isVisibility = GONE
        } else {
            isEnable = false
            isVisibility = VISIBLE
        }
        notifyChange()
    }

    /**
     * fun to reset the values using userDefault
     */
    fun resetValues(user: User, view: View) {
        view.user_detail_page_email.setText(userDefault.UserAdditionalEmail)
        view.user_detail_page_phone.setText(userDefault.UserPhoneNumber)
        view.user_detail_page_dob.text = userDefault.UserDOB
        view.user_detail_page_address.setText(userDefault.UserAddress)
        notifyChange()
        toggleButton(user, view)
    }

    /**
     * DatePicker to select the date and update the DOB TextView
     */
    fun clickDataPicker(view: View) {
        val calendar = Calendar.getInstance()
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH)
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val date = Date(year-1900, monthOfYear, dayOfMonth)
                val formatted = DateFormat.format("dd MMM, yyyy", date)
                view.user_detail_page_dob.text = formatted
            }, y, m, d
        )
        dpd.show()
    }

    /**
     * fun to update the user details
     */
    fun writeDataOnFireStore(user: User, view: View, userViewModel: UserViewModel) {
        val context = view.context
        when {
            !TextUtils.isEmpty(view.user_detail_page_email.text) and !validEmail(
                view.user_detail_page_email.text.toString()
            ) -> {
                view.user_detail_page_email.error = context.getString(R.string.invaild_email)
            }
            !TextUtils.isEmpty(view.user_detail_page_phone.text) && view.user_detail_page_phone.length() != 10 -> {
                view.user_detail_page_phone.error =
                    "Invalid Phone number. Phone number should be 10-digit number."
            }
            else -> {
                saveValues(user, view)
                userViewModel.updateUserDetailsToFireStore(userDefault).observe(
                    activity,
                    Observer { response ->
                        when (response) {
                            is Success -> {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                toggleButton(response.value as User, view)
                            }
                            is Failure -> {
                                Log.d(TAG, "Error writing document")
                                toggleButton(user, view)
                            }
                        }

                    }
                )
            }
        }
    }

    /**
     * fun to logOut from the firebase account and open the SplashScreenActivity
     */
    fun logout(userViewModel: UserViewModel, view: View) {
        userViewModel.logout().observe(activity,
            Observer { response ->
                when (response) {
                    is Success -> {
                        val intent = Intent(view.context, SplashScreenActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        val bundle = Bundle()
                        bundle.putString("Key", "LOGOUT")
                        intent.putExtras(bundle)
                        view.context.startActivity(intent)
                        (view.context as HomePageActivity).finish()
                    }
                    is Failure -> {
                        Log.e(TAG, response.e.toString())
                    }
                }
            })
    }
}