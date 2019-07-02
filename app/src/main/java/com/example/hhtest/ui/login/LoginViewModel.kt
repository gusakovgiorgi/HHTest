package com.example.hhtest.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.StringRes
import com.example.hhtest.R
import com.example.hhtest.data.LoginRepository
import com.example.hhtest.data.Result
import com.example.hhtest.data.WeatherRepository
import com.example.hhtest.data.bean.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class LoginFormState(
    val usernameError: Int? = null,
    val hideKeyBoard: Boolean = false,
    val passwordError: Int? = null,
    val showLoading: Boolean = false
)

data class WeatherResult(
    val success: Weather? = null,
    @StringRes val error: Int? = null
)

class LoginViewModel(private val loginRepository: LoginRepository, private val weatherRepository: WeatherRepository) :
    ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    private val _weatherResult = MutableLiveData<WeatherResult>()
    val weatherResult: LiveData<WeatherResult> = _weatherResult

    private val loginValidator = LoginValidator()
    private val UI = CoroutineScope(Dispatchers.Main)

    fun login(username: String, password: String) {
        if (!validate(username, password)) return
        UI.launch {
            _loginForm.value = LoginFormState(showLoading = true, hideKeyBoard = true)
            val result = loginRepository.loginAsync(username, password).await()
            _loginForm.value = LoginFormState(showLoading = false)
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                loadWeather()
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    private fun loadWeather() {
        UI.launch {
            val result = weatherRepository.getWeatherAsync().await()
            if (result is Result.Success) {
                _weatherResult.value = WeatherResult(success = result.data)
            } else {
                _weatherResult.value = WeatherResult(error = R.string.weather_error)
            }

        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (loginValidator.emailIsNotValid(email)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
            return false
        } else if (loginValidator.passwordIsNotValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
            return false
        } else {
            return true
        }
    }

}
