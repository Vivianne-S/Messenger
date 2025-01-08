package com.example.messenger

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class ContactRecycleAdapter(
    private val context: Context,
    private val contacts: List<User>
) : RecyclerView.Adapter<ContactRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.itemlistlayout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]

        // Reset views to their default state
        holder.contactNameTV.visibility = View.VISIBLE
       // holder.lastMessageTV.visibility = View.VISIBLE
        holder.addFriendButton.visibility = View.VISIBLE

        // Set the email in the contact name TextView
        holder.contactNameTV.text = contact.email ?: "No email"
       // holder.lastMessageTV.text = "Hej"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("contactEmail", contact.email)
            intent.putExtra("CONTACT_ID_KEY", contact.id)
            intent.putExtra("USER_NAME", contact.userName)
            holder.itemView.context.startActivity(intent)
        }


        holder.addFriendButton.setOnClickListener {
            holder.konfettiView.visibility = View.VISIBLE

            val emitter = Emitter(duration = 10, TimeUnit.SECONDS).perSecond(50)

            val konfettiParty = Party(
                speed = 10f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(Color.YELLOW, Color.GREEN, Color.MAGENTA),
                emitter = emitter,
                position = Position.Relative(0.5, 0.3),
                size = listOf(Size.SMALL, Size.LARGE),
                timeToLive = 10000L
            )
            holder.konfettiView.start(konfettiParty)

            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val db = Firebase.firestore
                val userRef = db.collection("Users").document(currentUserId)

                userRef.get().addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        userRef.set(mapOf("friends" to emptyList<String>()))
                            .addOnSuccessListener {
                                updateFriendsList(currentUserId, contact, holder)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to initialize user data.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        updateFriendsList(currentUserId, contact, holder)
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFriendsList(currentUserId: String, contact: User, holder: ViewHolder) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(currentUserId)

        userRef.update("friends", FieldValue.arrayUnion(contact.id))
            .addOnSuccessListener {
                Toast.makeText(context, "${contact.email} added as a friend!", Toast.LENGTH_SHORT).show()
                // Remove this contact from the RecyclerView
                if (contact.id != null && context is ContactActivity) {
                    context.removeContact(contact)
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Unknown error"
                Toast.makeText(context, "Failed to add friend: $errorMessage", Toast.LENGTH_LONG).show()
            }
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameTV: TextView = itemView.findViewById(R.id.contactNameTV)
        val addFriendButton: Button = itemView.findViewById(R.id.addFriendButton)
        val konfettiView: KonfettiView = itemView.findViewById(R.id.konfettiView)
    }
}