package com.dijonz.projeto_grupo4

import android.app.PendingIntent
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class CadastroConcluido : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
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
        val idFirestore = returnId(userEmail)

        binding.bStatus.setOnClickListener {
            if (verificaStatus(userEmail)) {
                db.collection("users")
                    .document(idFirestore)
                    .update("status", false)

            } else {
                db.collection("users")
                    .document(idFirestore)
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
        binding.tvNome.text = ""
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.tvNome.text = document.data["nome"].toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
    }

    private fun returnId(email: String): String {
        var id = ""
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.reference.id
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
        return id
    }

    class ReceberNotificacoes : FirebaseMessagingService() {

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)
            Log.d(TAG, "FCMFrom: ${remoteMessage.from}")
        }
    }
}
