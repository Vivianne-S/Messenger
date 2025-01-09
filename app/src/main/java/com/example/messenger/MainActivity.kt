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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
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

    lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


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

        val signInButtonGoogle = findViewById<Button>(R.id.googleSignInButton)

        signInButtonGoogle.setOnClickListener() {
            signInWithGoogle()
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

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)

    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result

            if (account != null) {
                updateUI(account)
            }
        }
    }

    fun updateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = auth.currentUser!!.uid

                val email = auth.currentUser!!.email
                //The first part of email address
                // (everything before the "@") is saved in a new variable.
                val split = email!!.split("@")

                val contactName = split[0]

                val user = User(email, userId, contactName)

                val db = Firebase.firestore

                db.collection("Users").document(userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, ContactActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to create user: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}

