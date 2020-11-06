package com.example.demoapp.models

// Data class for users
data class Users(val username: String, val password: String, val name: String?, val email: String?)

// Array list of users
val users: ArrayList<Users> = arrayListOf(
    Users("alex", "alex123", "Alex John", "alexjohn485@gmail.com"),
    Users("bob", "bob321", "Bob Thomas", "bobt@outlook.com"),
    Users("alice1452", "alice1452", "Alice Sanda", "alics8752@gmail.com"),
    Users("Kevin", "k987321", "Kevin Dapper", "kevind@rediffmail.com")
)
