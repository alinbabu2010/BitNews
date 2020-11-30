package com.example.demoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inflatedView = inflater.inflate(R.layout.fragment_profile, container, false)
        val name:TextView = inflatedView.findViewById(R.id.name_display)
        val username:TextView = inflatedView.findViewById(R.id.username_display)
        val email:TextView = inflatedView.findViewById(R.id.email_display)
        getDataFromFirebase(name,username,email)
        return inflatedView
    }

    /**
     * Method to get user details from firebase.
     */
   private fun getDataFromFirebase(name: TextView, username: TextView, email: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val user = FirebaseDatabase.getInstance().getReference("Users")

        val getUser = user.orderByKey().equalTo(currentUser)
        getUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    name.text = currentUser?.let { dataSnapshot.child(it).child("name").value } as CharSequence?
                    username.text = currentUser?.let { dataSnapshot.child(it).child("username").value } as CharSequence?
                    email.text = currentUser?.let { dataSnapshot.child(it).child("email").value } as CharSequence?
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}