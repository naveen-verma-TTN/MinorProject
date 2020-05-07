package com.minorProject.cloudGallery.util

import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern

object HelperClass {
    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

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

    fun Context.ShowToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}