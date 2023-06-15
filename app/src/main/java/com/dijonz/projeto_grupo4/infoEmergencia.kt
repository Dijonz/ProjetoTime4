package com.dijonz.projeto_grupo4

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.FieldClassification
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dijonz.projeto_grupo4.databinding.ActivityInfoEmergenciaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.lang.reflect.Field
import java.net.URL

class infoEmergencia : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var Photo: String
    private val storage = Firebase.storage
    private var idPaciente: String = ""
    private var uidPaciente: String = ""

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
                    uidPaciente = document.data["postID"].toString()

                    binding.tvtitulo.text = document.data["nome"].toString()
                    binding.tvCelularPaciente.text = document.data["telefone"].toString()
                    binding.tvNomePaciente.text = document.data["dados"].toString()
                }
            }
        print(uidPaciente)

        val idDentista = FirebaseAuth.getInstance().currentUser?.uid.toString()

        definirFoto(uidPaciente)


        binding.btAceitar.setOnClickListener {
            db.collection("emergencias").document(idPaciente).update("dentistas",
                FieldValue.arrayUnion(idDentista)).addOnSuccessListener {
                    val intent =Intent(this, WaitActivity::class.java)
                    intent.putExtra("uid-socorrista",uidPaciente)
                    intent.putExtra("id-socorrista",idPaciente)
                    intent.putExtra("telefone-socorrista",message)
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


    private fun definirFoto(uid: String) {


        FirebaseStorage.getInstance().reference.child("imagens")
            .child("${uid}.jpeg").downloadUrl.addOnSuccessListener { uri ->
                var foto = uri.toString()
                binding.ivPaciente.load(foto)
            }.set
    }



}