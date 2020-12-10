package com.example.demoapp.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp.R
import com.example.demoapp.databinding.FragmentProfileBinding
import com.example.demoapp.firebase.ProfileOperationsFirebase.Companion.getDataFromFirebase
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Utils.Companion.checkNetworkConnection
import com.example.demoapp.utils.Utils.Companion.firebaseError
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var container: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.container = container
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkNetworkConnection(context) {
            getDataFromFirebase {
                setProfileData(it)
            }
        }

        binding.changeUserImage.setOnClickListener {
            setBottomSheetDialog()
        }
        return binding.root
    }

    private fun setProfileData(data: Map<String, String>) {
        binding.progressBarProfile.visibility = View.INVISIBLE
        binding.nameDisplay.setText(data[NAME_STRING])
        binding.usernameDisplay.setText(data[USERNAME_STRING])
        binding.emailDisplay.setText(data[EMAIL_STRING])
        if(firebaseError?.isNotEmpty() == true) {
            Toast.makeText(activity, firebaseError, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method to set radio buttons and datepicker for filter bottom sheet dialog and process the filter clicks
     */
    private fun setBottomSheetDialog() {
        val bottomSheet = context?.let { it1 -> BottomSheetDialog(it1) }
        val bottomSheetView: View = layoutInflater.inflate(R.layout.image_options, container, false)
        bottomSheet?.setContentView(bottomSheetView)
        bottomSheet?.show()
    }


    }