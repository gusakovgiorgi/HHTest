package com.example.hhtest.data

import com.example.hhtest.data.model.LoggedInUser
import java.io.IOException
import java.lang.IllegalArgumentException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            if (username=="123@gmail.com") {
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                Result.Success(fakeUser)
            }else{
                Result.Fail("Invalid credentials")
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

