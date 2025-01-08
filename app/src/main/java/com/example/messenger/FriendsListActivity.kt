package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class FriendsListActivity : AppCompatActivity() {
    private val friends = mutableListOf<User>()
    private val filteredFriends = mutableListOf<User>()
    private lateinit var adapter: FriendsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        val recyclerView = findViewById<RecyclerView>(R.id.friendsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize SearchView
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFriends(newText)
                return true
            }
        })

        // Use the new FriendsRecyclerAdapter instead of ContactRecycleAdapter
        adapter = FriendsRecyclerAdapter(this, filteredFriends)
        recyclerView.adapter = adapter

        val viewContactsButton = findViewById<Button>(R.id.view_contacts_button)
        val backButton = findViewById<ImageView>(R.id.back_button_f)

        backButton.setOnClickListener {
            finish()
        }

        viewContactsButton.setOnClickListener(){
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        loadFriends()
    }

    /**
     * Filter friends
     */
    private fun filterFriends(query: String?) {
        filteredFriends.clear()
        if (query.isNullOrEmpty()) {
            filteredFriends.addAll(friends)
        } else {
            val lowercaseQuery = query.lowercase()
            friends.filterTo(filteredFriends) { friend ->
                friend.email?.lowercase()?.contains(lowercaseQuery) == true ||
                        friend.userName?.lowercase()?.contains(lowercaseQuery) == true
            }
        }
        adapter.notifyDataSetChanged()
    }

    /**
     * Load friends from database
     */
    private fun loadFriends() {
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
                                filteredFriends.clear()
                                for (document in friendSnapshot.documents) {
                                    val user = document.toObject<User>()
                                    if (user != null) {
                                        friends.add(user)
                                        filteredFriends.add(user)
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                    }
                }
        }
    }
}