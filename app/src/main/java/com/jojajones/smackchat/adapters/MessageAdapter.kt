package com.jojajones.smackchat.adapters

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jojajones.smackchat.Model.Message
import com.jojajones.smackchat.R
import com.jojajones.smackchat.Services.User

class MessageAdapter(private val context: Context, private val messages: List<Message>, private val messageClick: (Message) -> Unit): RecyclerView.Adapter<MessageAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MessageAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_template, parent, false)
        return Holder(view, messageClick)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindMessage(messages[position], context)
    }

    inner class Holder(itemView: View, val messageClick: (Message) -> Unit): RecyclerView.ViewHolder(itemView){

        val messageMessage = itemView.findViewById<TextView>(R.id.messageMessage)
        val messageUser = itemView.findViewById<TextView>(R.id.messageUserName)
        val messageTime = itemView.findViewById<TextView>(R.id.messageTimeStamp)
        val messageImage = itemView.findViewById<ImageView>(R.id.messageUserImage)

        fun bindMessage(message: Message, context: Context){
            messageMessage.text = message.message
            messageUser.text = message.userName
            messageTime.text = message.timestamp
            messageImage.setImageResource(context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName))
            messageImage.setBackgroundColor(User.getBgColor(message.userAvatarBg))
            itemView.setOnClickListener{messageClick(message)}
        }
    }
}