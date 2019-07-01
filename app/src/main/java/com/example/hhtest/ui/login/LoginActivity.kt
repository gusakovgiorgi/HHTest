package com.example.hhtest.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.hhtest.R
import com.example.hhtest.util.afterTextChanged
import com.example.hhtest.util.hideKeyBoard
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        initToolbar()

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val uiState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = uiState.isDataValid

            if (uiState.usernameError != null) {
                username.error = getString(uiState.usernameError)
            }
            if (uiState.passwordError != null) {
                password.error = getString(uiState.passwordError)
            }

            if (uiState.hideKeyBoard) {
                hideKeyBoard()
            }

            if (uiState.showLoading) {
                enableLoading()
            } else {
                disableLoading()
            }
        })

        loginViewModel.weatherResult.observe(this@LoginActivity, Observer {
            val weatherResult = it ?: return@Observer
            if (weatherResult.success != null) {
                Snackbar
                    .make(
                        container,
                        "temperature is ${weatherResult.success.list[0].main.temp} C",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Exit") {
                        finish()
                    }.show()
            }
            if (weatherResult.error != null) {
                Snackbar
                    .make(
                        container,
                        weatherResult.error,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Exit") {
                        finish()
                    }.show()
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }

        })

        username.afterTextChanged {
            loginViewModel.enteredLoginData(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.enteredLoginData(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun disableLoading() {
        loading.visibility = View.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun enableLoading() {
        loading.visibility = View.VISIBLE
        loading.requestFocus()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_create -> Toast.makeText(this, getString(R.string.create_pressed), Toast.LENGTH_SHORT).show()
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        login.isEnabled = false
        username.isEnabled = false
        password.isEnabled = false
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
