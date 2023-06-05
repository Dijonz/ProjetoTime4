package com.dijonz.projeto_grupo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.dijonz.projeto_grupo4.databinding.ActivityInfoEmergenciaBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.net.URL

class infoEmergencia : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var Photo: String
    private val storage = Firebase.storage

    private lateinit var binding: ActivityInfoEmergenciaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoEmergenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Photo = ""







    }

    override fun onStart() {
        super.onStart()

        val message = intent.getStringExtra("nome-emergencia")

        binding.tvH.text=message

        retrieveImage(message)
        setImage()
    }

    private fun setImage(){
        val urlPhoto= storage.getReferenceFromUrl(
            Photo)

        Glide.with(this)
            .load(urlPhoto)
            .into(binding.ivFotoPaciente)
    }

    private fun retrieveImage(message: String?){
        db.collection("emergencias")
            .whereEqualTo("dados",message)
            .get()
            .addOnSuccessListener {result ->
                for (document in result) {
                    Photo = document.data["postURL"].toString()
                }
            }

    }
}