package com.example.demoapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class for users
 */
@Entity
data class Users(
    @PrimaryKey var id : String,
    var username: String?,
    var name: String?,
    val email: String?,
    var userImageUrl: String?
)

