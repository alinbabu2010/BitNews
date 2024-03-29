package com.example.demoapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.ChatMessage
import com.example.demoapp.models.Users
import com.example.demoapp.ui.activities.dashboard.ChatActivity
import com.example.demoapp.utils.Utils.Companion.loadPhoto
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.mikhaellopez.circularimageview.CircularImageView


/**
 * Adapter class for RecyclerView of [ChatActivity]
 */
class ChatAdapter(
    options: FirebaseRecyclerOptions<ChatMessage>,
    private val progressBar: ProgressBar,
    private val activity: Activity?,
    val user: Users?,
    val currentUser: Users?
) : FirebaseRecyclerAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(options) {

    class ChatViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        var messageImageView: ImageView = itemView.findViewById(R.id.messageImageView)
        var messengerTextView: TextView = itemView.findViewById(R.id.messengerTextView)
        var messengerImageView: CircularImageView = itemView.findViewById(R.id.messengerImageView)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ChatViewHolder(
            inflater.inflate(
                R.layout.chat_item,
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(
        viewHolder: ChatViewHolder,
        position: Int,
        chatMessage: ChatMessage
    ) {
        progressBar.visibility = ProgressBar.INVISIBLE
        val imageUrl: String = chatMessage.imageUrl.toString()
        if (chatMessage.text.isNotEmpty() && chatMessage.imageUrl?.isNotEmpty() == true) {
            viewHolder.messageTextView.text = chatMessage.text
            viewHolder.messageTextView.visibility = TextView.VISIBLE
            activity?.let {
                Glide.with(it).load(imageUrl).override(500).into(viewHolder.messageImageView)
            }
            viewHolder.messageImageView.visibility = ImageView.VISIBLE
        } else if (chatMessage.text.isNotEmpty()) {
            viewHolder.messageTextView.text = chatMessage.text
            viewHolder.messageTextView.visibility = TextView.VISIBLE
            viewHolder.messageImageView.visibility = ImageView.GONE
        } else if (chatMessage.imageUrl?.isBlank() == false) {
            activity?.let {
                Glide.with(it).load(imageUrl).override(500).into(viewHolder.messageImageView)
            }
            viewHolder.messageImageView.visibility = ImageView.VISIBLE
            viewHolder.messageTextView.visibility = TextView.GONE
        }
        viewHolder.messageImageView.setOnClickListener {
            loadPhoto(imageUrl, viewHolder.messageImageView, activity)
        }
        var chatUser: Users? = null
        if (chatMessage.senderId == user?.id) {
            chatUser = user
        }
        if (chatMessage.senderId == currentUser?.id) {
            chatUser = currentUser
        }
        viewHolder.messengerTextView.text = chatUser?.name
        if (chatUser?.userImageUrl == "NONE") {
            viewHolder.messengerImageView.setImageDrawable(
                activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_avatar_anonymous) }
            )
        } else {
            activity?.let {
                Glide.with(it).load(chatUser?.userImageUrl).into(viewHolder.messengerImageView)
            }
        }
    }

}