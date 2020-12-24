package com.example.demoapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class for users
 */
@Entity
data class Users(
    @PrimaryKey var id : String,
    val username: String?,
    val name: String?,
    val email: String?,
    val userImageUrl: String?
)

