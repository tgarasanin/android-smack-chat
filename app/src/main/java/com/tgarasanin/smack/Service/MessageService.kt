package com.tgarasanin.smack.Service

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.tgarasanin.smack.Controller.App
import com.tgarasanin.smack.Model.Channel
import com.tgarasanin.smack.Model.Message
import com.tgarasanin.smack.Utilities.URL_GET_CHANNELS
import com.tgarasanin.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {
        clearChannels()
        val channelRequests = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val channelID = channel.getString("_id")
                    val newChannel = Channel(name, desc, channelID)
                    this.channels.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("ERROR", "Could not crete an user: $e")
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not crete an user: $it")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "applicaton/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $${App.prefs.authToken}")
                return headers
            }

        }
        App.prefs.requestQueue.add(channelRequests)

    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {
        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener {response ->
           clearMessages()
            try {
                for (x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timestamp = message.getString("timeStamp")

                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor,  id, timestamp)
                    this.messages.add(newMessage)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("ERROR", "Could not crete an user: $e")
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not crete an user: $it")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "applicaton/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer $${App.prefs.authToken}")
                return headers
            }

        }
        App.prefs.requestQueue.add(messagesRequest)

    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}