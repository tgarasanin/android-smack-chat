package com.tgarasanin.smack.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun signupAction(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun loginAction(view: View) {
        enableSpinner(true)
        hideKeyboard()
        val email = loginEmailEditView.text.toString()
        val password = loginPasswordView.text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(email, password) {loginService ->
                if (loginService) {
                    AuthService.findUserByEmail(this) {findSuccess ->
                        if (findSuccess) {
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            errorToast()
        }
    }

    fun errorToast() {
         Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginButton.isEnabled = !enable
        signupButton.isEnabled = !enable
    }

    fun hideKeyboard() {
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
