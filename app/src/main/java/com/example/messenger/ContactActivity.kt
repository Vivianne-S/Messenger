package com.example.messenger

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

        signOutButton.setOnClickListener() {
            signOut()

        }

        var db = Firebase.firestore
        var recyclerView = findViewById<RecyclerView>(R.id.chatLists)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ContactRecycleAdapter(this, contacts)
        recyclerView.adapter = adapter

        val docRef = db.collection("Users")

        docRef.get().addOnSuccessListener { documentSnapShot ->

            for (document in documentSnapShot.documents) {

                val user = document.toObject<User>()

                if (user != null) {
                    contacts.add(user)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
    fun signOut(){
        Firebase.auth.signOut()
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
    }
}