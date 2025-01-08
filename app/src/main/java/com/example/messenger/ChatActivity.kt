package com.example.messenger
import android.R.attr.data
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Collections
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


class ChatActivity : AppCompatActivity() {

    lateinit var rv: RecyclerView
    lateinit var messageInput: EditText
    lateinit var db: FirebaseFirestore
    lateinit var contactName: TextView

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

        backButton.setOnClickListener() {
            finish()
        }

        contactName = findViewById(R.id.contactNameTV)

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
        val contactNameString = intent.getStringExtra("USER_NAME")

        contactName.text = contactNameString

        val userId = auth.currentUser!!.uid


        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                val currentUserName = document.getString("userName") ?: "Unknown"

                val chatId = generateDocument(userId, contactId ?: "")
                val chatRef = db.collection("chats").document(chatId).collection("messages")

                // Real-time message listener
                chatRef.orderBy("timeStamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT)
                                .show()
                            return@addSnapshotListener
                        }

                        snapshot?.documentChanges?.forEach { change ->
                            when (change.type) {
                                DocumentChange.Type.ADDED -> {
                                    val message = change.document.toObject<Messages>()
                                    messages.add(message)
                                    adapter.notifyItemInserted(messages.size - 1)
                                    rv.scrollToPosition(messages.size - 1)

                                    if (containsCelebrationWord(message.message)) {
                                        showConfetti()
                                    }
                                }


                                else -> {}
                            }
                        }
                    }

                // Send button handler
                button.setOnClickListener {
                    val inputMessage = messageInput.text.toString().trim()
                    if (inputMessage.isNotEmpty()) {
                        val timeStamp = timeStamp()


                        val sendingMessage =
                            Messages(userId, currentUserName, inputMessage, timeStamp)

                        chatRef.add(sendingMessage)
                            .addOnSuccessListener {
                                messageInput.text.clear()
                                if (containsCelebrationWord(inputMessage)) {
                                    showConfetti()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get user details", Toast.LENGTH_SHORT).show()
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
    /* fun timeStamp(): String {
        val time = LocalDateTime.now()
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }*/

    fun formatEpochToDate(epochSeconds: Long): String {
        val instant = Instant.ofEpochSecond(epochSeconds)

        val zonedDateTime = instant.atZone(ZoneId.of("Europe/Stockholm"))

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return formatter.format(zonedDateTime)
    }
//hej
    fun timeStampInSec(): Long {
        return Instant.now().epochSecond
    }

    fun timeStamp() : String{
        val epochTime = timeStampInSec()
        return formatEpochToDate(epochTime)
    }

    private fun containsCelebrationWord(message: String): Boolean {
        val celebrationWords = listOf(
            "grattis", "gratulerar", "hurra",
            "congrats", "congratulation", "congratulations", "gz"
        )
        return celebrationWords.any { message.contains(it, ignoreCase = true) }
    }
    private fun showConfetti() {
        val konfettiView = findViewById<nl.dionsegijn.konfetti.xml.KonfettiView>(R.id.konfettiView)
        konfettiView.visibility = ImageView.VISIBLE
        konfettiView.start(
            Party(
                speed = 5f,
                maxSpeed = 10f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.BLUE),
                position = Position.Relative(0.5, 0.1),
                emitter = Emitter(duration = 1, TimeUnit.SECONDS).max(400)
            )
        )
    }

}
