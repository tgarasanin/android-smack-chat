package com.tgarasanin.smack.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.tgarasanin.smack.Model.Message
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.UserDataService
import kotlinx.android.synthetic.main.message_list_view.view.*
import java.net.SocketImpl
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindMessage(context: Context, message: Message) {
            val resourceID = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            itemView.messageProfileImage.setImageResource(resourceID)
            itemView.messageProfileImage.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            itemView.messageBodyTextView.text = message.message
            itemView.messageNameTextView.text = message.userName
            itemView.messageTimestampTextView.text = returnDataString(message.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMessage(context, messages[position])
    }

    fun returnDataString(isoString: String) : String {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertedDate = Date()
        try {
            convertedDate = isoFormatter.parse(isoString)
        } catch (e: ParseException) {
            Log.d("PARSE", "Cannot parse date")
        }

        val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
        return outDateString.format(convertedDate)
    }

}