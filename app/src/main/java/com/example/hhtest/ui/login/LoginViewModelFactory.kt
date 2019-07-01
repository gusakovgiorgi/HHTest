package com.example.hhtest.ui.login

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.hhtest.data.LoginDataSource
import com.example.hhtest.data.LoginRepository
import com.example.hhtest.data.WeatherDataSource
import com.example.hhtest.data.WeatherRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource()
                ), weatherRepository = WeatherRepository(
                    WeatherDataSource())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
