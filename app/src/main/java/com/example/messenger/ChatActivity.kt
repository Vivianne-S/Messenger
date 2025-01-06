package com.example.messenger
import android.R.attr.data
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections


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

        val backButton = findViewById<ImageView>(R.id.back_button)

        backButton.setOnClickListener(){
            finish()
        }


        val auth = FirebaseAuth.getInstance()

       // messages = mutableListOf()
        db = FirebaseFirestore.getInstance()
        rv = findViewById(R.id.chatMessages)
        messageInput = findViewById(R.id.messageInput)


        rv.layoutManager = LinearLayoutManager(this)
        val adapter = MessagesAdapter(this, messages)
        rv.adapter = adapter

        val button = findViewById<Button>(R.id.button)

        //not used
        // val contactEmail = intent.getStringExtra("contactEmail")

        //get contact id and userName from contacts.
        val contactId = intent.getStringExtra("CONTACT_ID_KEY")
        val userName = intent.getStringExtra("USER_NAME")

        val userId = auth.currentUser!!.uid

        //Document created by the ids of the users.
        val chatHistory = generateDocument(userId, contactId ?: "")

        val docRef = db.collection("Users").document(userId).collection(chatHistory)
        val contactDocRef = db.collection("Users").document(contactId ?: "").collection(chatHistory)


        docRef.get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot.documents) {
                val userMessage = document.toObject<Messages>()
                userMessage?.let {
                    messages.add(it)
                }
            }
            adapter.notifyDataSetChanged()
        }

        //Send button. Messages send and added to database.
        button.setOnClickListener {
            val inputMessage = messageInput.text.toString().trim()
            if (inputMessage.isNotEmpty()) {
                val timeStamp = timeStamp()
                val sendingMessage = Messages(userId, userName ?: "null", inputMessage, timeStamp)

                docRef.add(sendingMessage)
                    .addOnSuccessListener {
                        messages.add(sendingMessage)
                        adapter.notifyItemInserted(messages.size - 1)
                        messageInput.text.clear()
                        rv.scrollToPosition(adapter.itemCount - 1)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }

                contactDocRef.add(sendingMessage)
            }
        }
    }

    /**
     * generate a new document between the two users to add messages to.
     */
    fun generateDocument(currentUserId: String, contactId: String): String {
        val document = listOf(currentUserId, contactId).sorted()
        return "Chat_${document[0]}_${document[1]}"
    }

    /**
     * Timestamp to keep track on when messages are sent.
     */
    fun timeStamp(): String {
        val time = LocalDateTime.now()
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}
