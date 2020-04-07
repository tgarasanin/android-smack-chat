package com.tgarasanin.smack

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.nav_header_main.*
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

    }

}
