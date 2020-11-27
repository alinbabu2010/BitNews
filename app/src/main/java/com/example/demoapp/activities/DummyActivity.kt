package com.example.demoapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoapp.R
import com.example.demoapp.models.APIError
import com.example.demoapp.models.UserPost
import com.example.demoapp.utils.ErrorUtils
import com.example.demoapp.utils.RetrofitService
import kotlinx.android.synthetic.main.activity_dummy.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        button_get.setOnClickListener {
            withConnectionCheck { loadPost() }
        }

        button_post.setOnClickListener {
            withConnectionCheck {
                CoroutineScope(Dispatchers.IO).launch { coroutinePostData() }
            }
        }

        button_upload.setOnClickListener {
            withConnectionCheck { pickImageFromGallery() }
        }

    }

    /**
     * Method that check network connection before calling the request function
     */
    private fun  withConnectionCheck(function: () -> Unit) {
        if (isNetworkConnected()) {
           function()
        } else {
            Toast.makeText(
                this,
                "No Internet Connection",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    /**
     * Method to check if network is connected or not
     */
    private fun isNetworkConnected(): Boolean {

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Method to make a GET request using retrofit
     */
    private fun loadPost() {

        val postsAPI = RetrofitService.getService("https://jsonplaceholder.typicode.com/")
        val call: Call<UserPost> = postsAPI.getPost()
        call.enqueue(object : Callback<UserPost> {
            override fun onResponse(call: Call<UserPost>, response: Response<UserPost>) {
                if (response.isSuccessful) {
                    val message =
                        "${response.body()?.id} \n ${response.body()?.userId} \n ${response.body()?.title} \n ${response.body()?.body} "
                    textView_out.text = message
                } else {
                    val error: APIError? = ErrorUtils().parseError(response)
                    textView_out.text = error?.message()
                }
            }

            override fun onFailure(call: Call<UserPost>, t: Throwable) {
                if (t is IOException) {
                    Toast.makeText(
                        baseContext,
                        "Network failure or Not Found",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(baseContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * Method to execute retrofit POST request using coroutine
     */
    private fun coroutinePostData() {

        val newsAPI = RetrofitService.getService("https://jsonplaceholder.typicode.com/")
        val newPost = UserPost(1, 2, "Sample Title", "Hello my dear friend!")
        val callPost: Call<UserPost> = newsAPI.setPost(newPost)

        try {
            val response = callPost.execute()
            if (response.isSuccessful) {
                val message = " ${response.code()} \n" + " ${response.message()}"
                runOnUiThread { textView_out.text = message }
            } else {
                val error: APIError? = ErrorUtils().parseError(response)
                runOnUiThread { textView_out.text = error?.message() }
            }
        }
        catch (e: IOException) {
            runOnUiThread { Toast.makeText(this, "Network failure", Toast.LENGTH_SHORT).show() }
        }


    }

    /**
     * Method to pick image from device gallery
     */
    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    /**
     * Handle requested permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Handle result of picked image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val selectedImage: Uri? = data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = selectedImage?.let {
                contentResolver.query(
                    it,
                    filePathColumn, null, null, null
                )
            }
            cursor?.moveToFirst()
            val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
            val picturePath: String? = columnIndex?.let { cursor.getString(it) }
            cursor?.close()
            uploadFile(picturePath)
        }
    }

    /**
     * Method to upload image as multipart-data
     */
    private fun uploadFile(data: String?) {
        val file = File(data.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val descriptionString = "hello, this is description speaking"
        val description = RequestBody.create(
            MultipartBody.FORM, descriptionString
        )

        val newsAPI = RetrofitService.getService("https://demoapp.free.beeceptor.com")
        val callPost = newsAPI.uploadPhoto(description, body)
        callPost.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val message = " ${response.code()} \n ${response.message()}"
                runOnUiThread { textView_out.text = message }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                textView_out.text = t.message.toString()
            }
        })
    }


}
