package com.example.demoapp.fragments.dashboard

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.activities.MapsActivity
import com.example.demoapp.databinding.FragmentProfileBinding
import com.example.demoapp.firebase.ProfileOperationsFirebase.Companion.getDataFromFirebase
import com.example.demoapp.firebase.ProfileOperationsFirebase.Companion.removeUserImage
import com.example.demoapp.firebase.ProfileOperationsFirebase.Companion.uploadImageToFirebase
import com.example.demoapp.utils.Const.Companion.EMAIL_STRING
import com.example.demoapp.utils.Const.Companion.IMAGE_URL
import com.example.demoapp.utils.Const.Companion.NAME_STRING
import com.example.demoapp.utils.Const.Companion.PROFILE_IMAGE_DELETE
import com.example.demoapp.utils.Const.Companion.USERNAME_STRING
import com.example.demoapp.utils.Utils.Companion.checkNetworkConnection
import com.example.demoapp.utils.Utils.Companion.requestPermissionRationale
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var container: ViewGroup? = null
    private var userData: Map<String, String> = mapOf()
    private var photoUri: Uri? = null

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

        binding.userLocation.setOnClickListener {
            val permission = context?.let { it1 -> checkSelfPermission(it1,Manifest.permission.ACCESS_FINE_LOCATION) }
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            } else {
                startActivity(Intent(context, MapsActivity::class.java))
            }
        }
        return binding.root
    }

    /**
     * Method to set the profile data in fragment layout
     */
    private fun setProfileData(data: Map<String, String>) {
        binding.progressBarProfile.visibility = View.INVISIBLE
        binding.nameDisplay.setText(data[NAME_STRING])
        binding.usernameDisplay.setText(data[USERNAME_STRING])
        binding.emailDisplay.setText(data[EMAIL_STRING])
        if (!data[IMAGE_URL].equals("NONE"))
            context?.let { Glide.with(it).load(data[IMAGE_URL]).into(binding.userImage) }
        if (firebaseResponseMessage?.isNotEmpty() == true) {
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
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
            bottomSheet?.hide()
        }

        bottomSheetView.findViewById<ImageButton>(R.id.button_photo_remove).setOnClickListener {
            binding.userImage.setImageResource(R.drawable.avatar_anonymous_48dp)
            callRemoveUserImage()
            bottomSheet?.hide()
        }
    }

    /**
     * Method to remove user image  by calling [removeUserImage]
     */
    private fun callRemoveUserImage(){
        binding.progressProfileImage.visibility = View.VISIBLE
        removeUserImage()
        binding.progressProfileImage.visibility = View.INVISIBLE
        Toast.makeText(context, PROFILE_IMAGE_DELETE, Toast.LENGTH_SHORT).show()
    }

    /**
     * Method to check storage permissions
     */
    private fun checkAppPermissions() {

        val cameraPermission = context?.let {
            checkSelfPermission(it, Manifest.permission.CAMERA)
        }

        val storagePermission = context?.let {
            checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED) {
            takePictureIntent()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), STORAGE_REQUEST_CODE
            )
        }
    }

    /**
     * Method to create a image file in external storage for saving user profile image
     */
    private fun createImageFile(): Uri? {
        var imageUri: Uri? = null
        context?.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
            println(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
        return imageUri
    }

    /**
     * Method to open camera intent to take picture
     */
    private fun takePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoUri = createImageFile()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                takePictureIntent()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                    requestPermissionRationale(
                        context,
                        activity?.parent,
                        R.string.storage_camera_permission
                    )
                }
            }
        }
        else if (requestCode == LOCATION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startActivity(Intent(context, MapsActivity::class.java))
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                requestPermissionRationale(context, activity, R.string.location_permission)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == STORAGE_REQUEST_CODE) {
                saveUserImage(data?.data)
            }
            if (requestCode == IMAGE_CAPTURE_CODE) {
                saveUserImage(photoUri)
            }
        }
    }

    /**
     * Method to save user image by calling [uploadImageToFirebase]
     */
    private fun saveUserImage(data: Uri?) {
        context?.let { Glide.with(it).load(data).into(binding.userImage) }
        binding.progressProfileImage.visibility = View.VISIBLE
        uploadImageToFirebase(data, userData) {
            binding.progressProfileImage.visibility = View.INVISIBLE
            Toast.makeText(this.activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val STORAGE_REQUEST_CODE = 200
        var firebaseResponseMessage: String? = null
        private const val IMAGE_CAPTURE_CODE = 100
        private const val LOCATION_REQUEST_CODE = 300
    }


}