package com.example.demoapp.ui.fragments.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.adapter.ChatAdapter
import com.example.demoapp.databinding.FragmentChatBinding
import com.example.demoapp.firebase.ChatFirebase.sendMessage
import com.example.demoapp.firebase.ChatFirebase.storeImageMessage
import com.example.demoapp.models.Users
import com.example.demoapp.ui.activities.dashboard.ChatActivity.Companion.user
import com.example.demoapp.viewmodels.AccountsViewModel

/**
 * A simple [Fragment] subclass used as chat room
 */
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private var mMessageRecyclerView: RecyclerView? = null
    private var endUser: Users? = null
    private var mFirebaseAdapter: ChatAdapter? = null
    private var message: String = ""

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = it.data?.data
                storeImageMessage(uri, message, endUser?.id)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)
        endUser = arguments?.getParcelable("receiver")
        activity?.actionBar?.setDisplayShowTitleEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = endUser?.name
        (activity as AppCompatActivity).supportActionBar?.subtitle = endUser?.email
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        mMessageRecyclerView = binding.messageRecyclerView
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        binding.sendButton.setOnClickListener {
            message = binding.messageEditText.text.toString()
            if (message.isNotEmpty()) sendMessage(message, endUser?.id)
            binding.messageEditText.setText("")
        }
        binding.addMessageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResultLauncher.launch(intent)
        }
        setUpChatRoom(layoutManager)
    }

    /**
     * Method to set up chat between two users
     * @param layoutManager An instance of [LinearLayoutManager] for recyclerView setup
     */
    private fun setUpChatRoom(layoutManager: LinearLayoutManager) {
        val accountsViewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
        accountsViewModel.getUserChat(endUser?.id) {
            mFirebaseAdapter = ChatAdapter(it, binding.progressBar, activity, endUser, user)
        }
        val chatMessageCount = mFirebaseAdapter?.itemCount as Int
        mFirebaseAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 ||
                    positionStart >= chatMessageCount - 1 &&
                    lastVisiblePosition == positionStart - 1
                ) {
                    mMessageRecyclerView?.smoothScrollToPosition(positionStart)
                }
            }
        })
        mMessageRecyclerView?.layoutManager = layoutManager
        mMessageRecyclerView?.adapter = mFirebaseAdapter
    }

    override fun onPause() {
        mFirebaseAdapter?.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAdapter?.startListening()
    }

}