package com.example.messenger

class Messages() {
    var userId: String = ""
    var email : String = ""
    var message: String = ""
    var timeStamp: String = ""

    constructor(userId: String, message: String, timeStamp: String) : this() {
        this.userId = userId
        this.email = email
        this.message = message
        this.timeStamp = timeStamp
    }
}
