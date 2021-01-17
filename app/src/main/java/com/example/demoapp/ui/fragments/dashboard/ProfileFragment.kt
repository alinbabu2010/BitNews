package com.example.demoapp.ui.fragments.dashboard

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.databinding.FragmentProfileBinding
import com.example.demoapp.firebase.ProfileFirebase.removeUserImage
import com.example.demoapp.firebase.ProfileFirebase.uploadImageToFirebase
import com.example.demoapp.models.Users
import com.example.demoapp.ui.activities.dashboard.MapsActivity
import com.example.demoapp.utils.Constants.Companion.GROUP_KEY
import com.example.demoapp.utils.Constants.Companion.NAME_STRING
import com.example.demoapp.utils.Constants.Companion.PROFILE_IMAGE_DELETE
import com.example.demoapp.utils.Constants.Companion.USERNAME_STRING
import com.example.demoapp.utils.Utils.Companion.isNetworkConnected
import com.example.demoapp.utils.Utils.Companion.requestPermissionRationale
import com.example.demoapp.viewmodels.AccountsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


/**
 * A simple [Fragment] subclass for showing logged in user profile
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var container: ViewGroup? = null
    private var photoUri: Uri? = null
    private var user: Users? = null
    private var accountsViewModel: AccountsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this.container = container
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        accountsViewModel?.getUserInfo(FirebaseAuth.getInstance().currentUser?.uid.toString())
        accountsViewModel?.userData?.observe(viewLifecycleOwner, {
            setProfileData(it)
            user = it
        })

        val connectivityManager =
            context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(networkManager)

        binding.changeUserImage.setOnClickListener {
            setBottomSheetDialog()
        }

        binding.userLocation.setOnClickListener {
            val permission = context?.let { it1 ->
                checkSelfPermission(
                    it1,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                startActivity(Intent(context, MapsActivity::class.java))
            }
        }

        binding.nameField.setEndIconOnClickListener {
            setEditBottomSheetDialog(NAME_STRING)
        }

        binding.usernameField.setEndIconOnClickListener {
            setEditBottomSheetDialog(USERNAME_STRING)
        }

        return binding.root
    }

    /**
     * Method to call [AccountsViewModel.updateUserInfoOnFirebase] to update user
     */
    private fun updateUserOnFirebase() {
        if (user?.let { accountsViewModel?.updateUserInfoOnFirebase(it) } == true)
            notifyUser()
    }

    /**
     * Method to set and show edit user info bottom sheet dialog
     * @param field A string value to denote whether the field is for editing name or username.
     */
    private fun setEditBottomSheetDialog(field: String) {
        val bottomSheet = context?.let { BottomSheetDialog(it) }
        val bottomSheetView: View = layoutInflater.inflate(R.layout.edit_profile, container, false)
        bottomSheet?.setContentView(bottomSheetView)
        bottomSheet?.show()

        val inputEditText = bottomSheetView.findViewById<TextInputEditText>(R.id.profile_editText)
        val textView = bottomSheetView.findViewById<TextView>(R.id.edit_textView)
        if (field == NAME_STRING) {
            textView.setText(R.string.enter_name)
            inputEditText.text = binding.nameDisplay.text
        }
        if (field == USERNAME_STRING) {
            textView.setText(R.string.enter_name)
            inputEditText.text = binding.usernameDisplay.text
        }


        bottomSheetView.findViewById<Button>(R.id.button_save).setOnClickListener {
            when (field) {
                NAME_STRING -> user?.name = inputEditText.text.toString()
                USERNAME_STRING -> user?.username = inputEditText.text.toString()
            }
            user?.let {
                accountsViewModel?.updateUserInfoOnDatabase(it) { isSuccess ->
                    if (isSuccess) {
                        bottomSheet?.hide()
                        accountsViewModel?.userData?.postValue(user)
                        if (isNetworkConnected(context)) updateUserOnFirebase()
                        Toast.makeText(this.activity, R.string.profile_update, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this.activity,
                            R.string.profile_unsuccessful,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        bottomSheetView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            bottomSheet?.hide()
        }
    }

    /**
     * Method to set the profile data in fragment layout
     * @param data Parameter containing user data which is object of [Users]
     */
    private fun setProfileData(data: Users?) {
        binding.progressBarProfile.visibility = View.INVISIBLE
        binding.nameDisplay.setText(data?.name)
        binding.usernameDisplay.setText(data?.username)
        binding.emailDisplay.setText(data?.email)
        if (!data?.userImageUrl.equals("NONE")) {
            context?.let { Glide.with(it).load(data?.userImageUrl).into(binding.userImage) }
        } else {
            binding.progressProfileImage.visibility = View.VISIBLE
            context?.let { binding.userImage.setImageResource(R.drawable.ic_avatar_anonymous) }
            binding.progressProfileImage.visibility = View.INVISIBLE
        }
        if (firebaseResponseMessage?.isNotEmpty() == true) {
            Toast.makeText(context, firebaseResponseMessage, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method to set different image selection type buttons bottom sheet dialog and process the button clicks
     */
    private fun setBottomSheetDialog() {
        val bottomSheet = context?.let { BottomSheetDialog(it) }
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
            binding.userImage.setImageResource(R.drawable.ic_avatar_anonymous)
            callRemoveUserImage()
            bottomSheet?.hide()
        }
    }

    /**
     * Method to remove user image  by calling [removeUserImage]
     */
    private fun callRemoveUserImage() {
        binding.progressProfileImage.visibility = View.VISIBLE
        removeUserImage()
        user?.userImageUrl = "NONE"
        user?.let {
            accountsViewModel?.updateUserInfoOnDatabase(it) {
                if (isNetworkConnected(context)) updateUserOnFirebase()
                binding.progressProfileImage.visibility = View.INVISIBLE
                Toast.makeText(context, PROFILE_IMAGE_DELETE, Toast.LENGTH_SHORT).show()
            }
        }
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
     * @return Returns the Uri of the image file
     */
    private fun createImageFile(): Uri? {
        var imageUri: Uri? = null
        context?.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
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
        } else if (requestCode == LOCATION_REQUEST_CODE) {
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

    private val networkManager = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            updateUserOnFirebase()
        }
    }


    /**
     * Method to save user image by calling [uploadImageToFirebase]
     * @param data An Uri of image to be saved
     */
    private fun saveUserImage(data: Uri?) {
        context?.let { Glide.with(it).load(data).into(binding.userImage) }
        binding.progressProfileImage.visibility = View.VISIBLE
        if (isNetworkConnected(context)) {
            uploadImageToFirebase(data) {
                binding.progressProfileImage.visibility = View.INVISIBLE
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.progressProfileImage.visibility = View.INVISIBLE
            Toast.makeText(context, R.string.fui_no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method to notify user about news update
     */
    private fun notifyUser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = context?.let { NotificationCompat.Builder(it, PROFILE_CHANNEL_ID) }
            ?.setSmallIcon(R.drawable.ic_stat_notification)
            ?.setContentTitle(getString(R.string.profile_channel_title))
            ?.setContentText(getString(R.string.profile_channel_text))
            ?.setColor(Color.YELLOW)
            ?.setAutoCancel(true)
            ?.setOnlyAlertOnce(true)
            ?.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            ?.setCategory(NotificationCompat.CATEGORY_MESSAGE)
            ?.setGroup(GROUP_KEY)
            ?.build()
        val notificationManagerCompat = activity?.let { NotificationManagerCompat.from(it) }
        notification?.let { notificationManagerCompat?.notify(2, it) }
    }

    /**
     * Method to create notification channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channelName = activity?.getString(R.string.profile_channel_name)
        val channel = NotificationChannel(
            PROFILE_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = activity?.getString(R.string.profile_channel_description)
        channel.shouldShowLights()
        val notificationManager = context?.let {
            ContextCompat.getSystemService(
                it,
                NotificationManager::class.java
            )
        }
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        private const val STORAGE_REQUEST_CODE = 200
        var firebaseResponseMessage: String? = null
        private const val IMAGE_CAPTURE_CODE = 100
        private const val LOCATION_REQUEST_CODE = 300
        private const val PROFILE_CHANNEL_ID = "PROFILE_CHANNEL_ID"
    }

}