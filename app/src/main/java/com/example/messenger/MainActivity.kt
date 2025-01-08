package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.SyncRequest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

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

        //Checks if a user is already logged in.
        if (currentUser != null) {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }

        val signInButton = findViewById<Button>(R.id.signIn)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        signInButton.setOnClickListener() {
            signIn()
        }

        val signUpButton = findViewById<TextView>(R.id.signUpHereText)

        //TextButton that takes user to fragment to create new account.
        signUpButton.setOnClickListener() {

            signInButton.isEnabled = false

            val createUserFragment = CreateUserFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, createUserFragment)
            transaction.addToBackStack(null)
            transaction.commit()
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


    /**
     * function that log in user
     * if user is found and logged in properly, next activity will start
     */
    fun signIn() {
        val emailText = email.text.toString()
        val passwordText = password.text.toString()

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener() { task ->
            if (task.isSuccessful) {

                val intent = Intent(this, FriendsListActivity::class.java)
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

