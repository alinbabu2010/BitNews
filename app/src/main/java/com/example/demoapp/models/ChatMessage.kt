package com.example.demoapp.models

data class ChatMessage(
    var id: String? = "",
    var senderId: String = "",
    var text: String = "",
    var imageUrl: String? = null,
)