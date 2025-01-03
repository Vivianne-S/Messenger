package com.example.messenger

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {

    lateinit var rv : RecyclerView
    lateinit var messageInput : EditText
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Firebase.firestore
        rv = findViewById(R.id.chatMessages)
        messageInput = findViewById(R.id.messageInput)

        rv.layoutManager = LinearLayoutManager(this)
        val adapter = ContactRecycleAdapter(this, messeges)
        rv.adapter = adapter


        val contactEmail = intent.getStringExtra("contactEmail")
        val contactId = intent.getStringExtra("CONTACT_ID_KEY")









    }






    fun timeStamp() {
        val time = LocalDateTime.now()
        val timeStamp = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}