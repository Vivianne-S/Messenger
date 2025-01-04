package com.example.messenger

import android.content.Context
import android.content.Intent
import android.util.Log
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

       //Users email will be displayed in the textView
        holder.contactNameTV.text = contact.email

       //Will eventually hold last message. Now just a "Hej".
        holder.lastMessageTV.text = "Hej"

        //Email, id and userName is sent to ChatActivity.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("contactEmail", contact.email)
            intent.putExtra("CONTACT_ID_KEY", contact.id)
            intent.putExtra("USER_NAME", contact.userName)
            holder.itemView.context.startActivity(intent)
        }

        holder.addFriendButton.setOnClickListener {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                val db = Firebase.firestore
                val userRef = db.collection("Users").document(currentUserId)

                // Check if the document exists
                userRef.get().addOnSuccessListener { documentSnapshot ->
                    if (!documentSnapshot.exists()) {
                        // If document doesn't exist, create it with an empty friends array
                        userRef.set(mapOf("friends" to emptyList<String>()))
                            .addOnSuccessListener {
                                Log.d("Firestore", "User document created successfully.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Failed to create user document.", e)
                                Toast.makeText(context, "Failed to initialize user data.", Toast.LENGTH_SHORT).show()
                                return@addOnFailureListener
                            }
                    }

                    // Update the friends field after ensuring the document exists
                    userRef.update("friends", FieldValue.arrayUnion(contact.id))
                        .addOnSuccessListener {
                            Toast.makeText(context, "${contact.email} added as a friend!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            val errorMessage = exception.message ?: "Unknown error"
                            Toast.makeText(context, "Failed to add friend: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { exception ->
                    val errorMessage = exception.message ?: "Unknown error"
                    Log.e("Firestore", "Failed to fetch user document: $errorMessage", exception)
                    Toast.makeText(context, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }

    }



    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameTV: TextView = itemView.findViewById(R.id.contactNameTV)
        val lastMessageTV: TextView = itemView.findViewById(R.id.lastMessageTV)
        val addFriendButton: Button = itemView.findViewById(R.id.addFriendButton)
    }
}
