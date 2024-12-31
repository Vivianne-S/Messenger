package com.example.messenger

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class CreateUserFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var password : EditText
    lateinit var email : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
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

        registerButton.setOnClickListener() {
createUser()
        }

return view
    }

    fun createUser() {
       var emailText = email.text.toString()
        var passwordText = password.text.toString()

        auth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener() { task ->
            if (task.isSuccessful) {

                val intent = Intent(activity, MessengerOverviewActivity::class.java)
                startActivity(intent)


            } else {
                //TODO
            }
        }
    }
}