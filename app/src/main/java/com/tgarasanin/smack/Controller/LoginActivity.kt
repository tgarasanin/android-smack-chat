package com.tgarasanin.smack.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tgarasanin.smack.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun signupAction(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }

    fun loginAction(view: View) {

    }
}
