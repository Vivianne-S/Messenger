package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore

class GoogleAccount {
    /*.   !!!MOVED CODE FROM MAIN ACTIVITY. DOES NOT WORK IN THIS CONTEXT!!!


        var auth = FirebaseAuth.getInstance()
        var oneTapClient = Identity.getSignInClient(this)
        var  signInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = googleCredential.googleIdToken
                when {
                    idToken != null -> {

                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Log.d("!!!", "signInWithCredential:success")
                                    val user = auth.currentUser

                                    googleUserCreated(user)

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("!!!", "signInWithCredential:failure", task.exception)
                                    googleUserCreated(null)
                                }
                            }
                    }

                    else -> {
                        // Shouldn't happen.
                        Log.d("!!!", "No ID token!")
                    }
                }

            }
        }

        var signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
        BeginSignInRequest.GoogleIdTokenRequestOptions
        .builder()
        .setSupported(true)
        .setServerClientId(getString(R.string.default_web_client_id))
        .setFilterByAuthorizedAccounts(true).build()
        ).build()

        fun signInWithGoogle() {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)

                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("!!!", "One tap didn't work.    ${e.localizedMessage}")
                    }
                }.addOnFailureListener(this) { e ->
                    Log.e("!!!", "One tap failed.   ${e.localizedMessage} ")
                    Log.e("!!!", "One tap failed.   ${e.localizedMessage} ")

                }
        }

        fun googleUserCreated(user: FirebaseUser?) {

            var db = Firebase.firestore

            if (user != null) {
                val userId = auth.currentUser!!.uid
                val email = auth.currentUser!!.email
                val split = email!!.split("@")
                val userName = split[0]
                val newUser = User(email, userId, userName)

                db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                    if (!document.exists()) {
                        db.collection("Users").document(userId).set(newUser)
                            .addOnSuccessListener {

                                val intent = Intent(this, FriendsListActivity::class.java)
                                context.startActivity(intent)
                                finish()
                            }
                    } else {
                        val intent = Intent(this, FriendsListActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    */
}