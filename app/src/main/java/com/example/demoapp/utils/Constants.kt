package com.example.demoapp.utils

/**
 * Class for holding constant values
 */
class Constants {

    companion object {
        const val ARTICLE = "article"
        const val SHARE_TYPE = "text/plain"
        const val NAME_STRING = "name"
        const val USERNAME_STRING = "username"
        const val EMAIL_STRING = "email"
        const val IMAGE_URL = "userImageUrl"
        const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        const val USERS = "Users"
        const val DATE_FORMAT_ENCODE = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_FORMAT_DECODE = "MMM dd, y hh:mm a"
        const val NOT_FOUND = "Item not found"
        const val SERVER_ERROR = "Sorry, Server error occurred"
        const val NETWORK_FAILURE = "Network failure"
        const val FAVOURITES = "Favourites"
        const val NO_INTERNET = "No Internet Connection"
        const val PROFILE_IMAGE_UPDATE = "Profile picture updated"
        const val PROFILE_IMAGE_DELETE = "Profile picture removed"
        const val GROUP_KEY = "com.example.demoapp.dev"
        const val NOTIFICATION_ID = 100
        const val FAIL_MSG = "Fetching FCM registration token failed"
        const val MAX_RESULTS = 100
        const val MESSAGES = "messages"
    }
}