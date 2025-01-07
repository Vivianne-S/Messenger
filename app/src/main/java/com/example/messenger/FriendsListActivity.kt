package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class FriendsListActivity : AppCompatActivity() {
    private val friends = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        val recyclerView = findViewById<RecyclerView>(R.id.friendsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Use the new FriendsRecyclerAdapter instead of ContactRecycleAdapter
        val adapter = FriendsRecyclerAdapter(this, friends)
        recyclerView.adapter = adapter

        val viewContactsButton = findViewById<Button>(R.id.view_contacts_button)

        viewContactsButton.setOnClickListener(){
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }



        val currentUserId = Firebase.auth.currentUser?.uid
        if (currentUserId != null) {
            val db = Firebase.firestore
            db.collection("Users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    val friendIds = document.get("friends") as? List<String> ?: emptyList()

                    // Only proceed if there are friends to fetch
                    if (friendIds.isNotEmpty()) {
                        db.collection("Users").whereIn("id", friendIds).get()
                            .addOnSuccessListener { friendSnapshot ->
                                friends.clear()
                                for (document in friendSnapshot.documents) {
                                    val user = document.toObject<User>()
                                    if (user != null) {
                                        friends.add(user)
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
        }
    }


}