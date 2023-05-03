package com.dijonz.projeto_grupo4

import android.app.PendingIntent
import android.content.ContentValues
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CadastroConcluido : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

        override fun onStart() {
            super.onStart()

            val useriD = FirebaseAuth.getInstance().currentUser?.email.toString()
            definirNome(useriD)

            binding.bStatus.setOnClickListener {
                if (verificaStatus(useriD)) {
                    if (useriD != null) {
                        db.collection("users")
                            .document(returnId(useriD))
                            .update("status", false)
                    }
                } else {
                    if (useriD != null) {
                        db.collection("users")
                            .document(returnId(useriD))
                            .update("status", true)
                    }
                }
            }
        }

    class MyFirebaseMessagingService : FirebaseMessagingService() {

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            // Verifique se a mensagem contém dados
            if (remoteMessage.data.isNotEmpty()) {
                val data = remoteMessage.data

                val title = data["title"]
                val body = data["body"]
                val documentId = data["documentId"]

                val intent = Intent(this, CadastroConcluido::class.java)
                intent.putExtra("documentId", documentId)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val notificationBuilder = NotificationCompat.Builder(this, "channelId")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(0, notificationBuilder.build())
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

    private fun definirNome(id: String) {
        binding.tvNome.text = ""
        db.collection("users")
            .whereEqualTo("email", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.tvNome.text = document.data["nome"].toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
    }

    private fun returnId(aid: String): String {
        var id = ""
        db.collection("users")
            .whereEqualTo("email", aid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.id.toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
        return id
    }
}

