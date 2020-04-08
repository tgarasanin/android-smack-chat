package com.tgarasanin.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.AuthService
import com.tgarasanin.smack.Service.UserDataService
import com.tgarasanin.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
           if (AuthService.isLoggedIn) {
               usernameNavTextView.text = UserDataService.name
               emailNavTextView.text = UserDataService.email
               val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
               profileNavImageView.setImageResource(resourceId)
               profileNavImageView.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
               loginButton.text = "Logout"
           }
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginNavBarAction(view: View) {
        if (AuthService.isLoggedIn) {
           UserDataService.logout()
            loginButton.text = "Login"
            emailNavTextView.text = ""
            profileNavImageView.setImageResource(R.drawable.profiledefault)
            profileNavImageView.setBackgroundColor(Color.TRANSPARENT)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    fun addChannelAction(view: View) {

    }

    fun sendMessageAction(view: View) {
        Log.d("TAG", "TEODORA")
    }


}
