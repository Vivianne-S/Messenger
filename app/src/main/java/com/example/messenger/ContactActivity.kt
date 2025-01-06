package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class ContactActivity() : AppCompatActivity() {
   //List with all users.
    var contacts = mutableListOf<User>()

    fun removeContact(contact: User) {
        val position = contacts.indexOfFirst { it.id == contact.id }
        if (position != -1) {
            contacts.removeAt(position)
            findViewById<RecyclerView>(R.id.chatLists).adapter?.notifyItemRemoved(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signOutButton = findViewById<Button>(R.id.signOutButton)

        //Button that sign out user.
        signOutButton.setOnClickListener() {
            signOut()

        }

        val backButton = findViewById<ImageView>(R.id.back_button_c)
        backButton.setOnClickListener() {
            finish()
        }



        val viewFriendsButton = findViewById<Button>(R.id.viewFriendsButton)
        viewFriendsButton.setOnClickListener {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }





        val db = Firebase.firestore
        val recyclerView = findViewById<RecyclerView>(R.id.chatLists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ContactRecycleAdapter(this, contacts)
        recyclerView.adapter = adapter

        val currentUserId = Firebase.auth.currentUser?.uid
        if (currentUserId != null) {
            db.collection("Users").document(currentUserId).get()
                .addOnSuccessListener { currentUserDoc ->
                    val friendIds = currentUserDoc.get("friends") as? List<String> ?: emptyList()

                    db.collection("Users").get().addOnSuccessListener { documentSnapShot ->
                        contacts.clear()
                        for (document in documentSnapShot.documents) {
                            val user = document.toObject<User>()
                            if (user != null && user.id != currentUserId && !friendIds.contains(user.id)) {
                                contacts.add(user)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
        }
    }


    /**
     * function sign out user and send user to the log in page (MainActivity).
     * after log out, doesn´t allow user to use "back swipe".
     */
    fun signOut(){
        Firebase.auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()

    }
}