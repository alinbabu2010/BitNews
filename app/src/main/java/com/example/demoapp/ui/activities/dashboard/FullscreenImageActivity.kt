package com.example.demoapp.ui.activities.dashboard

import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.demoapp.R

/**
 * A full-screen activity that shows the full size image
 */
class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            sharedElementExitTransition = Fade()
        }
        setContentView(R.layout.activity_fullscreen_image)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val imageUrl = intent.getStringExtra("image")
        val messageImageView: ImageView = findViewById(R.id.full_imageView)
        Glide.with(this).load(imageUrl).into(messageImageView)
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (android.R.id.home == item.itemId) {
            supportFinishAfterTransition()
            true
        } else super.onOptionsItemSelected(item)
    }

}