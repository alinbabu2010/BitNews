package com.example.demoapp.ui.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp.adapter.ChatUsersAdapter
import com.example.demoapp.databinding.FragmentChatUsersBinding
import com.example.demoapp.viewmodels.AccountsViewModel

/**
 * A simple [Fragment] subclass used to display to list of users or chatting
 */
class ChatUsersFragment : Fragment() {

    private lateinit var binding : FragmentChatUsersBinding
    private var adapter : ChatUsersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatUsersBinding.inflate(layoutInflater)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.chat)
        (activity as AppCompatActivity).supportActionBar?.subtitle = null
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        val accountsViewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
        accountsViewModel.getUserList {
            adapter = ChatUsersAdapter(it,activity)
        }
        val chatMessageCount = adapter?.itemCount as Int
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val lastVisiblePosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                    positionStart >= chatMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    binding.recyclerviewChatUsers.smoothScrollToPosition(positionStart)
                }
            }
        })
        binding.recyclerviewChatUsers.layoutManager = layoutManager
        binding.recyclerviewChatUsers.adapter = adapter

    }

    override fun onPause() {
        adapter?.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

}