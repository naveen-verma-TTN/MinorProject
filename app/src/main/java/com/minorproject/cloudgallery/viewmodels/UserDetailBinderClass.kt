package com.minorproject.cloudgallery.viewmodels

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.minorproject.cloudgallery.BR
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.viewmodels.LoginBinderClass.Companion.validEmail
import com.minorproject.cloudgallery.views.HomePageActivity
import com.minorproject.cloudgallery.views.SplashScreenActivity
import kotlinx.android.synthetic.main.fragment_user_details.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class UserDetailBinderClass(
    private val activity: FragmentActivity
) : BaseObservable() {
    private var userDefault: User = User()

    companion object {
        private const val TAG: String = "UserDetailViewModel"
    }

    private fun saveValues(view: View) {
        userDefault = User(
            UserAdditionalEmail =
            view.user_detail_page_email.text.toString().trim(),
            UserPhoneNumber =
            view.user_detail_page_phone.text.toString().trim(),
            UserDOB =
            view.user_detail_page_dob.text.toString().trim(),
            UserAddress =
            view.user_detail_page_address.text.toString().trim()
        )
    }

    var isEnable: Boolean = false
    var isVisibility: Int = VISIBLE

    fun toggleButton(view: View) {
        saveValues(view)
        if (!isEnable) {
            isEnable = true
            isVisibility = GONE
        } else {
            isEnable = false
            isVisibility = VISIBLE
        }
        notifyChange()
    }

    fun resetValues(view: View) {
        view.user_detail_page_email.setText(userDefault.UserAdditionalEmail)
        view.user_detail_page_phone.setText(userDefault.UserPhoneNumber)
        view.user_detail_page_dob.text = userDefault.UserDOB
        view.user_detail_page_address.setText(userDefault.UserAddress)
        notifyChange()
        toggleButton(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clickDataPicker(view: View) {
        val calendar = Calendar.getInstance()
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH)
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
                val date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                val formatted = date.format(formatter)
                view.user_detail_page_dob.text = formatted
            },
            y,
            m,
            d
        )
        dpd.show()
    }

    fun writeDataOnFireStore(view: View, viewModel: UserViewModel) {
        val context = view.context
        if (!TextUtils.isEmpty(view.user_detail_page_email.text) and !validEmail(
                view.user_detail_page_email.text.toString()
            )
        ) {
            view.user_detail_page_email.error = context.getString(R.string.invaild_email)
        } else if (!TextUtils.isEmpty(view.user_detail_page_phone.text) && view.user_detail_page_phone.length() != 10) {
            view.user_detail_page_phone.error =
                "Invalid Phone number. Phone number should be 10-digit number."
        } else {
            saveValues(view)
            viewModel.updateUserDetails(userDefault).observe(
                activity,
                androidx.lifecycle.Observer { success ->
                    if (success) {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        toggleButton(view)
                    } else {
                        Log.d(TAG, "Error writing document")
                        toggleButton(view)
                    }
                }
            )
        }
    }

    fun logout(view: View) {
        val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
        if (mAuth?.currentUser != null) {
            val intent = Intent(view.context, SplashScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            val bundle = Bundle()
            bundle.putString("Key", "LOGOUT")
            intent.putExtras(bundle)
            mAuth.signOut()
            view.context.startActivity(intent)
            (view.context as HomePageActivity).finish()
        }
    }
}