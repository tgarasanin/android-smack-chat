package com.tgarasanin.smack.Controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        spinner.visibility = View.INVISIBLE
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            spinner.visibility = View.VISIBLE
        } else {
            spinner.visibility = View.INVISIBLE
        }
        createUserButton.isEnabled = !enable
        tapAvatarCreate.isEnabled = !enable
        generateAvatarColorButton.isEnabled = !enable
    }

    fun generateAvatarAction(view: View) {
        val color = Random.nextInt(2)
        val avatar = Random.nextInt(28)

        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        avatarImageView.setImageResource(resourceId)

    }

    fun generateBgdColorAction(view: View) {
        val r = Random.nextInt(255)
        val g = Random.nextInt(255)
        val b = Random.nextInt(255)

        avatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        val saveR = r.toDouble()/255
        val saveG = r.toDouble()/255
        val saveB = r.toDouble()/255

        avatarColor = "[$saveR,, $saveG, $saveB, 1]"



    }

    fun createUserAction(view: View) {
        enableSpinner(true)
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            //"j@j.com", 123456
            AuthService.registerUser(this, email, password) { registerSuccess ->
                Log.d("TAG", registerSuccess.toString())
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, username, email, userAvatar, avatarColor) { createSuccess ->
                                if (createSuccess) {

                                    val userDataChange = Intent("BROADCAST_USER_DATA_CHANGE")
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

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
        } else {
            Toast.makeText(this, "Make sure username, email and password are filled in", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

}
