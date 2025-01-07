package com.example.messenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class FriendsRecyclerAdapter(
    private val context: Context,
    private val friends: MutableList<User>
) : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.friend_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]

        holder.friendNameTV.text = friend.userName

        holder.deleteButton.setOnClickListener {
            val currentUserId = Firebase.auth.currentUser?.uid
            if (currentUserId != null) {
                removeFriend(currentUserId, friend, position)
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("contactEmail", friend.email)
            intent.putExtra("CONTACT_ID_KEY", friend.id)
            intent.putExtra("USER_NAME", friend.userName)
            holder.itemView.context.startActivity(intent)
        }
    }
    private fun removeFriend(currentUserId: String, friend: User, position: Int) {
        val db = Firebase.firestore
        val userRef = db.collection("Users").document(currentUserId)

        db.runTransaction { transaction ->
            transaction.update(userRef, "friends", FieldValue.arrayRemove(friend.id))

            null
        }.addOnSuccessListener {
            friends.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, friends.size)

            Toast.makeText(context, "${friend.userName} removed from friends", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener { e ->
            Toast.makeText(context, "Failed to remove friend: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int = friends.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendNameTV: TextView = itemView.findViewById(R.id.friendNameTV)
        val deleteButton : ImageView = itemView.findViewById(R.id.deleteButton)
    }
}