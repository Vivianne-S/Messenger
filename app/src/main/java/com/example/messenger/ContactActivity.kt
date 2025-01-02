package com.example.messenger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView



class ContactActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var contacts : List<User>
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var recyclerView = findViewById<RecyclerView>(R.id.chatLists)
        val adapter = MessagesAdapter(this, contacts)


        setContentView(R.layout.activity_messenger_overview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView.adapter = adapter
    }
}