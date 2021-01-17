package com.example.demoapp.models

data class ChatMessage(
    var id: String? = "",
    var text: String = "",
    var name: String = "",
    var photoUrl: String = "",
    var imageUrl: String? = null,
)