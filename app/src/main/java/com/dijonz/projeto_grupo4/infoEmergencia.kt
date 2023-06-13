package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.FieldClassification
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.dijonz.projeto_grupo4.databinding.ActivityInfoEmergenciaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.lang.reflect.Field
import java.net.URL

class infoEmergencia : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var Photo: String
    private val storage = Firebase.storage
    private var idPaciente: String = ""

    private lateinit var binding: ActivityInfoEmergenciaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoEmergenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()

        val message = intent.getStringExtra("telefone-emergencia")

        db.collection("emergencias")
            .whereEqualTo("telefone",message)
            .get()
            .addOnSuccessListener {result ->
                for (document in result) {
                    idPaciente = document.id
                    binding.tvtitulo.text = document.data["nome"].toString()
                    binding.tvCelularPaciente.text = document.data["telefone"].toString()
                    binding.tvNomePaciente.text = document.data["dados"].toString()
                }
            }

        val idDentista = FirebaseAuth.getInstance().currentUser?.uid.toString()




        binding.btAceitar.setOnClickListener {
            db.collection("emergencias").document(idPaciente).update("dentistas",
                FieldValue.arrayUnion(idDentista)).addOnSuccessListener {
                    val intent =Intent(this, WaitActivity::class.java)
                    startActivity(intent)
            }
        }

        binding.toolbar.setOnClickListener {
            val intent = Intent(this, CadastroConcluido::class.java)
            startActivity(intent)
        }

        binding.btRecusar.setOnClickListener {
            val intent = Intent(this, TelaEmergencias::class.java)
            startActivity(intent)
        }

    }

    private fun setImage(){
        val urlPhoto= storage.getReferenceFromUrl(
            Photo)

        Glide.with(this)
            .load(urlPhoto)
            .into(binding.ivPaciente)
    }

    private fun retrieveImage(message: String?){
        db.collection("emergencias")
            .whereEqualTo("telefone",message)
            .get()
            .addOnSuccessListener {result ->
                for (document in result) {
                    Photo = document.data["postURL"].toString()
                }
            }

    }

}