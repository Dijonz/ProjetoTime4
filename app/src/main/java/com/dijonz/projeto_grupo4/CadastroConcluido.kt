package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CadastroConcluido : AppCompatActivity() {
    private var id: String = ""
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
    private lateinit var functions: FirebaseFunctions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            } else {
                val token = task.result
                Log.w(TAG, "FCM registration token successful")
            }

        }
    }

    override fun onStart() {
        super.onStart()

        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        definirNome(userEmail)

        db.collection("users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.id
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }.addOnFailureListener{
                Log.d(ContentValues.TAG, it.message.toString())
            }

        binding.swStatus.setOnClickListener {
            if (verificaStatus(userEmail)) {
                db.collection("users")
                    .document(id)
                    .update("status", false)

            } else {
                db.collection("users")
                    .document(id)
                    .update("status", true)

            }
        }
    }

    private fun verificaStatus(id: String): Boolean {
        var x = 1
        db.collection("users")
            .whereEqualTo("email", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    x = if (document.data["status"] == true) {
                        1
                    } else {
                        2
                    }
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
        return x == 1
    }

    private fun definirNome(email: String) {
        binding.tvBemVindo.text = ""
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.tvBemVindo.text = "Olá, " + document.data["nome"].toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
    }

    class ReceberNotificacoes : FirebaseMessagingService() {
        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)
            Log.d(TAG, "FCMFrom: ${remoteMessage.from}")
        }
    }
}
