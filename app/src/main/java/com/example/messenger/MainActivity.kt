package com.example.messenger

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.triviaapp.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        var signInButton = findViewById<Button>(R.id.signIn)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        signInButton.setOnClickListener(){
            signIn()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
    }

    public fun signOut(){
        Firebase.auth.signOut()
    }

    public fun signIn(){
        var emailText = email.text.toString()
        var passwordText = password.text.toString()

        auth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener() { task ->
            if (task.isSuccessful) {

            } else {
                //TODO
            }
        }
    }
}