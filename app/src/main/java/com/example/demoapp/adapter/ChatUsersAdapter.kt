package com.example.demoapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.Users
import com.example.demoapp.ui.fragments.chat.ChatFragment
import com.example.demoapp.utils.Utils.Companion.loadPhoto
import com.example.demoapp.utils.Utils.Companion.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.mikhaellopez.circularimageview.CircularImageView

class ChatUsersAdapter(options: FirebaseRecyclerOptions<Users>, val activity: FragmentActivity?) :
    FirebaseRecyclerAdapter<Users, ChatUsersAdapter.ChatUserViewModel>(options) {

    class ChatUserViewModel(view: View) : RecyclerView.ViewHolder(view) {
        val userImageView: CircularImageView = itemView.findViewById(R.id.user_imageView)
        val userNameView: TextView = itemView.findViewById(R.id.user_nameView)
        val userLayout: LinearLayout = itemView.findViewById(R.id.user_layout)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatUserViewModel {
        val inflater = LayoutInflater.from(parent.context)
        return ChatUserViewModel(
            inflater.inflate(
                R.layout.list_users,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatUserViewModel, position: Int, user: Users) {
        if (user.id != FirebaseAuth.getInstance().currentUser?.uid) {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            if (user.userImageUrl == "NONE" || user.userImageUrl.isNullOrEmpty()) {
                holder.userImageView.setImageResource(R.drawable.ic_avatar_anonymous)
            } else {
                activity?.let { Glide.with(it).load(user.userImageUrl).into(holder.userImageView) }
            }
            holder.userImageView.setOnClickListener {
                user.userImageUrl?.let { url -> loadPhoto(url, holder.userImageView, activity) }
            }
            holder.userNameView.text = user.name
            holder.userLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("receiver", user)
                val fragment = ChatFragment()
                fragment.arguments = bundle
                activity?.supportFragmentManager?.let { manager ->
                    replaceFragment(fragment, R.id.chat_layout, manager)
                }
            }
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }
}