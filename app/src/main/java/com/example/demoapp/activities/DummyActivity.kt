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
import com.example.demoapp.api.NewsAPI
import com.example.demoapp.models.News
import com.example.demoapp.models.UserPost
import com.example.demoapp.utils.Services
import kotlinx.android.synthetic.main.activity_dummy.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        button_get.setOnClickListener {
            checkConnection { loadNews() }
        }

        button_post.setOnClickListener {
           checkConnection { saveData() }
            //thread.start()
        }

        button_upload.setOnClickListener {
            checkConnection { pickImageFromGallery() }
        }

    }

    /**
     * Method that check network connection before calling the request function
     */
    private fun  checkConnection(function: () -> Unit) {
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
    private fun loadNews() {

        // Retrofit builder
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Object to call methods
        val newsAPI: NewsAPI = retrofit.create(NewsAPI::class.java)
        val call: Call<News> = newsAPI.getNews()
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news: News? = response.body()
                val stringBuilder = StringBuilder()
                news?.articles?.forEach {
                    with(stringBuilder) {
                        append(it.title)
                        append("\n")
                        append(it.author)
                        append("\n")
                        append(it.content)
                        append("\n")
                        append(it.description)
                        append("\n")
                        append(it.publishedAt)
                        append("\n")
                        append(it.url)
                        append("\n\n")
                    }
                }

                textView_out.text = stringBuilder
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Toast.makeText(baseContext, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Method to make a POST request using retrofit
     */
    private fun saveData() {

        val apiServices = Services()
        val newsAPI = apiServices.getService("https://jsonplaceholder.typicode.com/")
        val newPost = UserPost(12, 25, "Sample Title", "Hello my dear friend!")
        val callPost: Call<UserPost> = newsAPI.setPost(newPost)

        // Asynchronous method
        callPost.enqueue(object : Callback<UserPost> {
            override fun onResponse(call: Call<UserPost>, response: Response<UserPost>) {
                val message = " ${response.code()} \n ${response.raw()} \n" + " ${response.body()}"
                textView_out.text = message
            }

            override fun onFailure(call: Call<UserPost>, t: Throwable) {
                textView_out.text = t.message.toString()
            }
        })
    }

    // Thread to execute Synchronous method of retrofit POST request
    private val thread = Thread() {

        val apiServices = Services()
        val newsAPI = apiServices.getService("https://jsonplaceholder.typicode.com/")
        val newPost = UserPost(1, 2, "Sample Title", "Hello my dear friend!")
        val callPost: Call<UserPost> = newsAPI.setPost(newPost)

        val post = callPost.execute()
        val message = " ${post.code()} \n ${post.message()}"
        runOnUiThread { textView_out.text = message }
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
     * Method to upload image using multipart-data
     */
    private fun uploadFile(data: String?) {
        val file = File(data.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val descriptionString = "hello, this is description speaking"
        val description = RequestBody.create(
            MultipartBody.FORM, descriptionString
        )

        val apiServices = Services()
        val newsAPI = apiServices.getService("https://demoapp.free.beeceptor.com")
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
