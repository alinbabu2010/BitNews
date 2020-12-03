package com.example.demoapp.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.databinding.FragmentProfileBinding
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Const.Companion.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        getDataFromFirebase()
        return binding.root
    }

    /**
     * Method to get user details from firebase.
     */
   private fun getDataFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val user = FirebaseDatabase.getInstance().getReference(USERS)
        val getUser = user.orderByKey().equalTo(currentUser)
        getUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    binding.progressBarProfile.visibility = View.INVISIBLE
                    binding.nameDisplay.text = currentUser?.let { dataSnapshot.child(it).child(NAME_STRING).value } as CharSequence?
                    binding.usernameDisplay.text = currentUser?.let { dataSnapshot.child(it).child(USERNAME_STRING).value } as CharSequence?
                    binding.emailDisplay.text = currentUser?.let { dataSnapshot.child(it).child(EMAIL_STRING).value } as CharSequence?
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,error.message,Toast.LENGTH_SHORT).show()
            }
        })

   }

}