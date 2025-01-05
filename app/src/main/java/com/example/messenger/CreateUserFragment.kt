package com.example.messenger

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CreateUserFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var password: EditText
    lateinit var email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_user, container, false)


        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)

        val registerButton = view.findViewById<Button>(R.id.register)
        val signInTextView = view.findViewById<TextView>(R.id.signInText)


        //TextButton that takes user back to mainActivity to log in.
        signInTextView.setOnClickListener() {
            //TODO
        }


        registerButton.setOnClickListener() {
            createUser()
        }

        return view
    }

    /**
     * function that takes user input, creates a new account and add user to database.
     */
    fun createUser() {
        var emailText = email.text.toString()
        var passwordText = password.text.toString()

        if (emailText.contains("@") && passwordText.length >= 6) {
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser!!.uid

                        //The first part of email address
                        // (everything before the "@") is saved in a new variable.
                        val split = emailText.split("@")

                        val contactName = split[0]

                        val user = User(emailText, userId, contactName)

                        db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, ContactActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(activity, "Failed to create user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        val exception = task.exception
                        Toast.makeText(
                            activity,
                            "Registration failed: ${exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }
        } else if (!emailText.contains("@")) {
            Toast.makeText(activity, "Not a valid Email", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                activity,
                "Password must contain minimum 6 characters",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}