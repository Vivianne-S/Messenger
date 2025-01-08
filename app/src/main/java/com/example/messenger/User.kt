package com.example.messenger

data class User(val email : String?, val id : String?, val userName : String?) {

        constructor() : this(null, null, null)
    }

