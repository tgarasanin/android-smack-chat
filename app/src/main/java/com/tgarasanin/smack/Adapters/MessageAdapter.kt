package com.tgarasanin.smack.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tgarasanin.smack.Model.Message
import com.tgarasanin.smack.R
import com.tgarasanin.smack.Service.UserDataService
import kotlinx.android.synthetic.main.message_list_view.view.*

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindMessage(context: Context, message: Message) {
            val resourceID = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            itemView.messageProfileImage.setImageResource(resourceID)
            itemView.messageProfileImage.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            itemView.messageBodyTextView.text = message.message
            itemView.messageNameTextView.text = message.userName
            itemView.messageTimestampTextView.text = message.timestamp
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

}