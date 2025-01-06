package com.example.messenger

class Messages() {
    var userId: String = ""
    var email: String = ""
    var message: String = ""
    var timeStamp: com.google.firebase.Timestamp? = null  // Changed to Firebase Timestamp

    constructor(userId: String, email: String, message: String, timeStamp: com.google.firebase.Timestamp?) : this() {
        this.userId = userId
        this.email = email
        this.message = message
        this.timeStamp = timeStamp
    }
}
