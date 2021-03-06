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
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tgarasanin.smack.Adapters.MessageAdapter
import com.tgarasanin.smack.Model.Message
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.AuthService
import com.tgarasanin.smack.Service.MessageService
import com.tgarasanin.smack.Service.UserDataService
import com.tgarasanin.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.tgarasanin.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<com.tgarasanin.smack.Model.Channel>
    var selectedChannel : com.tgarasanin.smack.Model.Channel? = null
    lateinit var messageAdapter: MessageAdapter

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this, MessageService.messages)
        chatRecyclerView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this){}
        }


    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
           if (App.prefs.isLoggedIn) {
               usernameNavTextView.text = UserDataService.name
               emailNavTextView.text = UserDataService.email
               val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
               profileNavImageView.setImageResource(resourceId)
               profileNavImageView.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
               loginButton.text = "Logout"

               MessageService.getChannels() { complete ->
                   if (complete) {
                       if (MessageService.channels.count() > 0) {
                           selectedChannel = MessageService.channels[0]
                           channelAdapter.notifyDataSetChanged()
                           updateWithChannel()
                       }

                   }
               }

           }
        }
    }

    fun updateWithChannel() {
        channelNameTextView.text = "#${selectedChannel?.name}"

        if (selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) {complete ->
                if (complete) {
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0) {
                        chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
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
        if (App.prefs.isLoggedIn) {
           UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
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
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { _ , _ ->
                    val name = dialogView.channelNameEditText.text.toString()
                    val description = dialogView.channelDescriptionEditText.text.toString()
                    // create a channel

                    socket.emit("newChannel", name, description)

                }
                .setNegativeButton("Cancel") { _, _ ->

                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val name = args[0] as String
                val desc = args[1] as String
                val channelID = args[2] as String

                val newChannel = com.tgarasanin.smack.Model.Channel(name, desc, channelID)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                 if (channelId == selectedChannel?.id) {
                     val msgBody = args[0] as String
                     val userName = args[3] as String
                     val userAvatar = args[4] as String
                     val avatarColor = args[5] as String
                     val userID: String = args[6] as String
                     val timestamp: String = args[7] as String

                     val newMessage = Message(msgBody, channelId, userName, userAvatar, avatarColor, userID, timestamp)
                     MessageService.messages.add(newMessage)
                     messageAdapter.notifyDataSetChanged()
                     chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                 }
            }
        }
    }


    fun sendMessageAction(view: View) {
        if (App.prefs.isLoggedIn && messageEditText.text.isNotEmpty() && selectedChannel != null) {
            val userID = UserDataService.id
            val channelID = selectedChannel!!.id
            socket.emit("newMessage", messageEditText.text.toString(), userID, channelID, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageEditText.text.clear()
            hideKeyboard()
        }
        Log.d("TAG", "TEODORA")
    }

    fun hideKeyboard() {
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


}
