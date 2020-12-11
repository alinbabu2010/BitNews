package com.example.demoapp.fragments.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.activities.ImageDetailActivity
import com.example.demoapp.databinding.FragmentProfileBinding
import com.example.demoapp.firebase.ProfileOperationsFirebase
import com.example.demoapp.firebase.ProfileOperationsFirebase.Companion.getDataFromFirebase
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.IMAGE_URL
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Utils.Companion.checkNetworkConnection
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.ByteArrayOutputStream


/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var container: ViewGroup? = null
    private var userData : Map<String, String> = mapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.container = container
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        checkNetworkConnection(context) {
            getDataFromFirebase {
                userData = it
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
        context?.let { Glide.with(it).load(data[IMAGE_URL]).into(binding.userImage) }
        Log.i(IMAGE_URL, data[IMAGE_URL].toString())
        if(firebaseResponseMessage?.isNotEmpty() == true) {
            Toast.makeText(context, firebaseResponseMessage, Toast.LENGTH_SHORT).show()
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

        bottomSheetView.findViewById<ImageButton>(R.id.button_camera).setOnClickListener {
            checkAppPermissions()
            bottomSheet?.hide()
        }

        bottomSheetView.findViewById<ImageButton>(R.id.button_gallery).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
            bottomSheet?.hide()
        }

        bottomSheetView.findViewById<ImageButton>(R.id.button_photo_remove).setOnClickListener {
            binding.userImage.setImageResource(R.drawable.avatar_anonymous_48dp)
        }
    }

    /**
     * Method to check storage permissions
     */
    private fun checkAppPermissions() {
        val permission = context?.let { ContextCompat.checkSelfPermission(
            it,
            Manifest.permission.CAMERA
        )
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA), REQUEST_CODE
            )
        }
        else {
            openCamera()
        }
    }

    private fun openCamera(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == ImageDetailActivity.PERMISSION_REQUEST_CODE) {
            println(requestCode)
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null){
            if (requestCode == REQUEST_CODE) {
                context?.let { Glide.with(it).load(data.data).into(binding.userImage) }
                saveUserImage(data.data)
            }
            if (requestCode == IMAGE_CAPTURE_CODE) {
                val bitmap = data.extras?.get("data") as Bitmap
                binding.userImage.setImageBitmap(bitmap)
                getImageUri(context,bitmap)

            }
        }
    }

    /**
     * Get URI from image bitmap capture by camera.
     */
    private fun getImageUri(context: Context?, inImage: Bitmap) {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        @Suppress("DEPRECATION") val path = Images.Media.insertImage(context?.contentResolver, inImage, "Title", null)
        saveUserImage(Uri.parse(path))
    }

    private fun saveUserImage(data: Uri?) {
        binding.progressProfileImage.visibility = View.VISIBLE
        ProfileOperationsFirebase.uploadImageToFirebase(data, userData) {
            binding.progressProfileImage.visibility = View.INVISIBLE
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE = 200
        var firebaseResponseMessage : String? = null
        private const val IMAGE_CAPTURE_CODE = 100
    }


}