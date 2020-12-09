package com.example.demoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for news
 */
@Parcelize
data class News(val articles: ArrayList<Articles>) : Parcelable