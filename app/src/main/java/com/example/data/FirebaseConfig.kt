package com.example.data

/**
 * Converted from src/lib/firebase.ts
 */
data class FirebaseConfig(
    val apiKey: String,
    val authDomain: String,
    val projectId: String,
    val storageBucket: String,
    val messagingSenderId: String,
    val appId: String
)

val firebaseConfig = FirebaseConfig(
    apiKey = "YOUR_API_KEY",
    authDomain = "YOUR_PROJECT_ID.firebaseapp.com",
    projectId = "YOUR_PROJECT_ID",
    storageBucket = "YOUR_PROJECT_ID.appspot.com",
    messagingSenderId = "YOUR_MESSAGING_SENDER_ID",
    appId = "YOUR_APP_ID"
)
