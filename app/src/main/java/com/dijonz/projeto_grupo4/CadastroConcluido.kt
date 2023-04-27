package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CadastroConcluido : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroConcluidoBinding
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val useriD = FirebaseAuth.getInstance().currentUser?.email.toString()
        definirNome(useriD)

        binding.bStatus.setOnClickListener {
            if(verificaStatus(useriD)) {
                if (useriD != null) {
                    db.collection("user")
                        .document(useriD)
                        .update("status", false)
                }
            } else{
                if (useriD != null) {
                    db.collection("user")
                        .document(useriD)
                        .update("status", true)
                }
            }
        }
    }

    private fun verificaStatus(id: String): Boolean{
        var x = 1
        db.collection("user")
            .whereEqualTo("email", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    x = if(document.data["status"]==true){
                        1
                    } else{
                        2
                    }
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
        return x==1
    }

    private fun definirNome(id: String) {
        binding.tvNome.text = ""
        db.collection("user")
            .whereEqualTo("email", id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    binding.tvNome.text = document.data["name"].toString()
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }
    }
}