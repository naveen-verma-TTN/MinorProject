package com.minorproject.cloudgallery.screens.profile

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.UserInfo
import com.minorproject.cloudgallery.viewmodels.UserProfileViewModel
import kotlinx.android.synthetic.main.collapse_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_user_details.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


class UserDetails : Fragment() {
    private var userDetails: UserInfo? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: UserProfileViewModel

    companion object {
        private const val TAG: String = "UserDetails"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(UserProfileViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getUserInfoData().observe(
            requireActivity(),
            androidx.lifecycle.Observer { user ->
                userDetails = user
                resetValues()
            })

        user_detail_page_edit.setOnClickListener {
            saveValues()
            toggleButton()
        }
        user_detail_page_correct.setOnClickListener {
            if (!TextUtils.isEmpty(user_detail_page_email.text) and !validEmail(
                    user_detail_page_email.text.toString()
                )
            ) {
                user_detail_page_email.error = getString(R.string.invaild_email)
            } else if (!TextUtils.isEmpty(user_detail_page_phone.text) && user_detail_page_phone.length() != 10) {
                user_detail_page_phone.error =
                    "Invalid Phone number. Phone number should be 10-digit number."
            } else {
                saveValues()
                writeDataOnFireStore(userDetails)
                toggleButton()
            }
        }
        user_detail_page_cancel.setOnClickListener {
            resetValues()
            toggleButton()
        }
        user_detail_page_dob.setOnClickListener {
            clickDataPicker()
        }
    }


    private fun writeDataOnFireStore(userInfo: UserInfo?) {

        val user = HashMap<String, Any>()

        val currentUser = mAuth.currentUser

        user["UserAdditionalEmail"] = userInfo!!.UserAdditionalEmail
        user["UserPhoneNumber"] = userInfo.UserPhoneNumber
        user["UserDOB"] = userInfo.UserDOB
        user["UserAddress"] = userInfo.UserAddress

        val rootRef = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference =
            rootRef.collection("UserDetails").document(currentUser?.uid!!)
        docIdRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    docIdRef.update(user)
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                } else {
                    docIdRef.set(user)
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
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

    private fun saveValues() {
        userDetails = UserInfo(
            user_detail_page_email.text.toString().trim(),
            user_detail_page_phone.text.toString().trim(),
            user_detail_page_dob.text.toString().trim(),
            user_detail_page_address.text.toString().trim()
        )
    }

    private fun resetValues() {
        user_detail_page_email.setText(userDetails?.UserAdditionalEmail)
        user_detail_page_phone.setText(userDetails?.UserPhoneNumber)
        user_detail_page_dob.text = userDetails?.UserDOB
        user_detail_page_address.setText(userDetails?.UserAddress)
    }

    private fun toggleButton() {
        if (user_detail_page_edit.visibility == View.VISIBLE) {
            toggleEnable(true)
            user_detail_page_edit.visibility = View.GONE
            user_detail_page_cancel.visibility = View.VISIBLE
            user_detail_page_correct.visibility = View.VISIBLE
        } else {
            toggleEnable(false)
            user_detail_page_edit.visibility = View.VISIBLE
            user_detail_page_cancel.visibility = View.GONE
            user_detail_page_correct.visibility = View.GONE
        }
    }

    private fun toggleEnable(isEnable: Boolean) {
        user_detail_page_email.isEnabled = isEnable
        user_detail_page_phone.isEnabled = isEnable
        user_detail_page_dob.isEnabled = isEnable
        user_detail_page_address.isEnabled = isEnable
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun clickDataPicker() {
        val year = 1990
        val month = 9
        val day = 2

        val dpd = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy")
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                val formatted = date.format(formatter)
                user_detail_page_dob.text = formatted
            },
            year,
            month,
            day
        )
        dpd.show()
    }


}
