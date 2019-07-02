package com.example.hhtest.ui.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.hhtest.R
import com.example.hhtest.util.hideKeyBoard
import com.tooltip.Tooltip
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var passwordToolTip: Tooltip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        passwordToolTip = Tooltip.Builder(password)
            .setText(R.string.invalid_password)
            .setGravity(Gravity.TOP)
            .build()

        initToolbar()

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val uiState = it ?: return@Observer

            if (uiState.usernameError != null) {
                Toast.makeText(this, uiState.usernameError, Toast.LENGTH_SHORT).show()
            }
            if (uiState.passwordError != null) {
                Toast.makeText(this, uiState.passwordError, Toast.LENGTH_SHORT).show()
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

        password.apply {
            setOnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0;
                val DRAWABLE_TOP = 1;
                val DRAWABLE_RIGHT = 2;
                val DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        passwordToolTip.apply {
                            if (isShowing) {
                                dismiss()
                            } else show()
                        }
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
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
        username.isEnabled = true
        password.isEnabled = true
        login.isEnabled = true
    }

    private fun enableLoading() {
        loading.visibility = View.VISIBLE
        username.isEnabled = false
        password.isEnabled = false
        login.isEnabled = false
        passwordToolTip.dismiss()
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
