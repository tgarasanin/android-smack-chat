package com.tgarasanin.smack.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun signupAction(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun loginAction(view: View) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        AuthService.loginUser(this, email, password) {loginService ->
            if (loginService) {
                AuthService.findUserByEmail(this) {findSuccess ->
                    if (findSuccess) {
                        finish()
                    }
                }
            }
        }
    }
}
