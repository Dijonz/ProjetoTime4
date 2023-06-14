package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.ContextParams
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.URI

class CadastroConcluido : AppCompatActivity() {
    private var id: String = ""
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = FirebaseAuth.getInstance()
    private lateinit var functions: FirebaseFunctions
    private var foto: URI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }


    override fun onStart() {
        super.onStart()

        val userId = auth.currentUser?.uid.toString()

        waitingResponse(userId)
        definirNome(userId)
        definirFoto(userId)


        db.collection("users")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.id
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, it.message.toString())
            }


        binding.swStatus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                verificaStatus(userId)
                db.collection("users")
                    .document(id)
                    .update("status", true)
                updateToken()
                criarToast("Status Ativado!")

            } else {
                verificaStatus(userId)
                db.collection("users")
                    .document(id)
                    .update("status", false)
                criarToast("Status Desativado!")
            }
        }

        binding.tvLogout.setOnClickListener {
            auth.signOut()
            val intentt =  Intent(this, LoginActivity::class.java)
            startActivity(intentt)
        }

        binding.llEmergencias.setOnClickListener {
            val lntent = Intent(this,TelaEmergencias::class.java)
            startActivity(lntent)
        }

        binding.ivPerfil.setOnClickListener {
            val lntent = Intent(this,AtualizarDados::class.java)
            startActivity(lntent)
        }
    }

    fun criarToast(texto: String) {
        val toast = Toast.makeText(applicationContext, texto, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun updateToken() {

        val fcmToken = Firebase.messaging.token.result.toString()
        val uidUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if (uidUser != null) {
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("uid", uidUser)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = document.id
                        FirebaseFirestore.getInstance().collection("users").document(user)
                            .update("token", fcmToken)
                    }
                }
        }

    }


    private fun verificaStatus(uid: String): Boolean {
        var x = 1
        db.collection("users")
            .whereEqualTo("uid", uid)
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

    private fun definirNome(uid: String) {
        binding.tvBemVindo.text = ""
        db.collection("users")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.tvBemVindo.text = "Olá, " + document.data["nome"].toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
    }
    private fun definirFoto(uid: String){
        var storageRef = FirebaseStorage.getInstance().reference.child("dentistas").child("${uid}.jpeg")

        val local = File.createTempFile("tempImage","jpeg")
        storageRef.getFile(local).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(local.absolutePath)
            binding.ivProfile.setImageBitmap(bitmap)
        }.addOnFailureListener{
            criarToast("ERRO AO CARREGAR A FOTO DE PERFIL")
        }


    }
    private fun waitingResponse(uidDentista:String) {
            db.collection("emergencias")
                .whereEqualTo("status", true)
                .get().addOnSuccessListener {result ->
                    for(document in result){
                            if(document.data!!["dentistas"]==uidDentista){
                                val intent = Intent(this,EmergenciaAceita::class.java).putExtra("uid-socorrista", document.id.toString())
                                startActivity(intent)
                            }

                            }
                        }
                }
        }




