package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

            val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
            definirNome(userEmail)

            val idDocumento = returnId(userEmail)

            binding.bStatus.setOnClickListener {
                if (verificaStatus(userEmail)) {
                    db.collection("users")
                        .document(idDocumento)
                        .update("status", false).addOnSuccessListener {
                            val snack2 = Snackbar.make((binding.root),"STATUS: INDISPONÍVEL",Snackbar.LENGTH_SHORT)
                            snack2.setBackgroundTint(Color.GREEN)
                            snack2.setTextColor(Color.WHITE)
                            snack2.show()
                        }
                } else {
                    db.collection("users")
                        .document(idDocumento)
                        .update("status", true).addOnSuccessListener {
                            val snack1 = Snackbar.make((binding.root),"STATUS: DISPONÍVEL", Snackbar.LENGTH_SHORT)
                            snack1.setBackgroundTint(Color.GREEN)
                            snack1.setTextColor(Color.WHITE)
                            snack1.show()
                        }

                }
            }
        }



    private fun verificaStatus(id: String): Boolean {
        var x = 0
        db.collection("users")
            .whereEqualTo("email", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    x = if (document.data["status"] == true) {
                        1
                    } else {
                        7
                    }
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
        return x==1
    }

    private fun definirNome(id: String) {
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

    private fun returnId(email: String): String {
        var id = ""
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.reference.id
                }
            }
        return id
    }

}

