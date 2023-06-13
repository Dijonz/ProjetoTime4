package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dijonz.projeto_grupo4.databinding.ActivityEmergenciaAceitaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EmergenciaAceita : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private lateinit var binding: ActivityEmergenciaAceitaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergenciaAceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uidDentista = auth.currentUser?.uid.toString()

        val uidSocorrista = intent.getStringExtra("uid-socorrista")


        db.collection("emergencias").document(uidSocorrista.toString()).get().addOnSuccessListener {result ->
            binding.tvNome.text = result.data?.get("nome")?.toString()
            binding.tvTelefone.text = result.data?.get("telefone")?.toString()
        }


        binding.cvEmergencias.setOnClickListener {
            db.collection("emergencias").document(uidSocorrista.toString()).update("status", "finalizado").addOnSuccessListener {
                Toast.makeText(this,"EMERGÊNCIA ENCERRADA",Toast.LENGTH_LONG)
                Thread.sleep(2_000)
                val intent = Intent(this,CadastroConcluido::class.java)
                startActivity(intent)
            }
        }

    }
}