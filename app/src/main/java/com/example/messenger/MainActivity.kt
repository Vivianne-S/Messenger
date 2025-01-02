package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }


        val signInButton = findViewById<Button>(R.id.signIn)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        signInButton.setOnClickListener(){
            signIn()
        }

        val signUpButton = findViewById<TextView>(R.id.signUpHereText)

        signUpButton.setOnClickListener(){

            signInButton.isEnabled = false

            val createUserFragment = CreateUserFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, createUserFragment)
            transaction.addToBackStack(null)
            transaction.commit()
//Hej
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


    public fun signIn() {
        var emailText = email.text.toString()
        var passwordText = password.text.toString()

        auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener() { task ->
            if (task.isSuccessful) {

                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)


            } else {
                val exception = task.exception
                when (exception) {
                    is FirebaseAuthInvalidUserException -> {
                        Toast.makeText(this, "No user found with this email", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

