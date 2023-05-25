package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class CadastroConcluido : AppCompatActivity() {
    private var id: String = ""
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
    private lateinit var functions: FirebaseFunctions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btEmergencias.setOnClickListener {
            val intent = Intent(this, TelaAceites::class.java)
            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()

        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

        definirNome(userEmail)
        updateToken()


        db.collection("users")
            .whereEqualTo("email", userEmail)
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
                verificaStatus(userEmail)
                db.collection("users")
                    .document(id)
                    .update("status", true)
                val snack1 = Snackbar.make(
                    (binding.root), "Você será alertado em futuras emergências", Snackbar.LENGTH_SHORT)
                snack1.setBackgroundTint(Color.DKGRAY)
                snack1.setTextColor(Color.WHITE)
                snack1.show()

            } else {
                verificaStatus(userEmail)
                db.collection("users")
                    .document(id)
                    .update("status", false)
                val snack1 = Snackbar.make((binding.root),"Você NÃO receberá notificações!",Snackbar.LENGTH_SHORT)
                snack1.setBackgroundTint(Color.DKGRAY)
                snack1.setTextColor(Color.WHITE)
                snack1.show()
            }
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
}
