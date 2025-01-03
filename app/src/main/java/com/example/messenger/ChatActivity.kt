package com.example.messenger

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ChatActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var messageInput: EditText
    lateinit var db: FirebaseFirestore

    var messages = mutableListOf<Messages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth = FirebaseAuth.getInstance()

        messages = mutableListOf()
        db = Firebase.firestore
        rv = findViewById(R.id.chatMessages)
        messageInput = findViewById(R.id.messageInput)

        rv.layoutManager = LinearLayoutManager(this)
        val adapter = MessagesAdapter(this, messages)
        rv.adapter = adapter

        val button = findViewById<Button>(R.id.button)


        val contactEmail = intent.getStringExtra("contactEmail")
        val contactId = intent.getStringExtra("CONTACT_ID_KEY")

        val userId = auth.currentUser!!.uid

        val chatHistory = generateDocument(userId, contactId ?: "")

        val docRef = db.collection("Users").document(userId).collection(chatHistory)
        val contactDocRef = db.collection("Users").document(contactId ?: "").collection(chatHistory)

        docRef.get().addOnSuccessListener { documentSnapShot ->

            for (document in documentSnapShot.documents) {

                var userMessage = document.toObject<Messages>()

                if (userMessage != null) {
                    messages.add(userMessage)
                }
            }
            adapter.notifyDataSetChanged()
        }

        button.setOnClickListener() {

            val timeStamp = timeStamp()
            val inputMessage = messageInput.text.toString()

            val sendingMessage = Messages(userId, inputMessage, timeStamp)

            docRef.add(sendingMessage)
                .addOnSuccessListener {
                    messages.add(sendingMessage)
                    adapter.notifyDataSetChanged()
                    messageInput.text.clear()
                }

            contactDocRef.add(sendingMessage)

            rv.scrollToPosition(adapter.itemCount-1)

        }
    }

    fun generateDocument(currentUserId: String, contactId: String): String {

        val document = listOf(currentUserId, contactId).sorted()

        return "Chat_${document[0]}_${document[1]}"

    }

    fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }


    fun timeStamp(): String {
        val time = LocalDateTime.now()
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    }
}