package com.example.demoapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Data class for users
 */
@Entity @Parcelize
data class Users(
    @PrimaryKey var id : String = "",
    var username: String? = null,
    var name: String? = null,
    val email: String? = null,
    var userImageUrl: String? = null
) : Parcelable
