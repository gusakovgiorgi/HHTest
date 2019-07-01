package com.example.hhtest.data

import com.example.hhtest.data.model.LoggedInUser
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    @Volatile var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }


    suspend fun loginAsync(username: String, password: String): Deferred<Result<LoggedInUser>> = GlobalScope.async {
        delay(3000)
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return@async result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}
