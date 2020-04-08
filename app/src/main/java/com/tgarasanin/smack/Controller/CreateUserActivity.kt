package com.tgarasanin.smack.Controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

    fun createUserAction(view: View){
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        //"j@j.com", 123456
        AuthService.registerUser(this, email, password) { registerSuccess ->
            Log.d("TAG", registerSuccess.toString())
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) {loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(this, username, email, userAvatar, avatarColor) {createSuccess ->
                            finish()
                        }
                    }
                }
            }
        }
    }

}
