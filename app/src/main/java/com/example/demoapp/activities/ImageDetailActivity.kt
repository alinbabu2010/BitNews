package com.example.demoapp.activities

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.Articles
import com.example.demoapp.utils.Const.Companion.ARTICLE
import com.example.demoapp.utils.Utils.Companion.showAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * Activity class for storage and permissions check demo
 */
class ImageDetailActivity : AppCompatActivity() {

    /**
     * This method creates the options menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    private var lastMsg: String? = null
    private var progressBar: ProgressBar? = null
    private var url: String? = null
    private var storageType: String? = null
    private var directory : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val newsImage: ImageView = findViewById(R.id.article_image)
        val article = intent.getParcelableExtra<Articles>(ARTICLE)
        progressBar = findViewById(R.id.progressBar_download)
        progressBar?.visibility = View.INVISIBLE
        url = article?.urlToImage.toString()
        Glide.with(applicationContext).load(url).override(800).into(newsImage)
        findViewById<Button>(R.id.button_download).setOnClickListener {
            showStorageOptions()
        }

    }

    /**
     * Method to show alert dialog to select storage options
     */
    private fun showStorageOptions() {
        val builder = AlertDialog.Builder(this, R.style.DialogBoxTheme)
        builder.setTitle("Please select one of the storage option and click OK")
        builder.setIcon(android.R.drawable.ic_menu_save)
        val storageOptions  = arrayOf("Internal", "External", "Private")
        builder.setSingleChoiceItems(storageOptions,-1) { _: DialogInterface, index: Int ->
            storageType = storageOptions[index]
        }
        builder.setPositiveButton("OK"){ _: DialogInterface, _: Int ->
            progressBar?.visibility = View.VISIBLE
            getStorageType()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun getStorageType(){

        when (storageType) {
            "Internal" -> {
                directory  = filesDir
                downloadImage(url)
            }
            "External" -> {
                directory = File(Environment.DIRECTORY_PICTURES)
                if (directory?.exists()==false) directory?.mkdirs()
                checkAppPermissions()
            }
            "Private" -> {
                directory  = getDir("Downloads",Context.MODE_PRIVATE)
                if (directory?.exists()==false) directory?.mkdirs()
                downloadImage(url)
            }
        }
    }

    /**
     * Method to check storage permissions
     */
    private fun checkAppPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
        else downloadImage(url)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            println(requestCode)
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                downloadImage(url)
            } else {
                Toast.makeText(baseContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Method to download image from url using download manager
     */
    private fun downloadImage(url: String?) {

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
            setTitle(url?.substring(url.lastIndexOf("/") + 1))
            setDescription("Downloading article image")
            setMimeType("*/*")
            setDestinationInExternalPublicDir(
                directory.toString(),
                url?.substring(url.lastIndexOf("/") + 1)
            )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        CoroutineScope(Dispatchers.Default).launch {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.i("Location",msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg
                }
                if (status == DownloadManager.STATUS_SUCCESSFUL){
                    progressBar?.visibility = View.INVISIBLE
                }
                cursor.close()
            }
        }
    }

    /**
     * Method to show download status messages
     */
    private fun statusMessage(url: String?, directory: File?, status: Int): String {
        return when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url?.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.logout_option) {
            showAlert(this, this)
            true
        } else {
            false
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 101
    }
}