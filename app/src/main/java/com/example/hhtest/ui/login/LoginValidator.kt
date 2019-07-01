package com.example.hhtest.ui.login

import android.util.Patterns
import java.util.regex.Pattern

const val PASSWORD_MINIMUM_LENGTH = 6

class LoginValidator {
    /**
     * At least one lower case, one upper case and one digit. Minimum 6 characters
     */
    val passwordPattern: Pattern =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{$PASSWORD_MINIMUM_LENGTH,}\$")

    fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else false
    }

    // A placeholder password validation check
    fun isPasswordValid(password: String): Boolean {
        val trimmedPassword = password.trim()
        return if (trimmedPassword.length > PASSWORD_MINIMUM_LENGTH) {
            passwordPattern.matcher(trimmedPassword).matches()
        } else false
    }

    fun passwordIsNotValid(password: String) = !isPasswordValid(password)

    fun emailIsNotValid(email: String) = !isEmailValid(email)
}