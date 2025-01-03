package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messenger_overview)
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

        val db = Firebase.firestore
        val recyclerView = findViewById<RecyclerView>(R.id.chatLists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ContactRecycleAdapter(this, contacts)
        recyclerView.adapter = adapter

        val docRef = db.collection("Users")

        docRef.get().addOnSuccessListener { documentSnapShot ->

            // Adds user to database and contact list
            for (document in documentSnapShot.documents) {
                val user = document.toObject<User>()

                if (user != null) {
                    contacts.add(user)
                }
            }
            adapter.notifyDataSetChanged()
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