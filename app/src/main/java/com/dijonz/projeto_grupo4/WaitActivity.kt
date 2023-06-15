package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dijonz.projeto_grupo4.CadastroConcluido
import com.dijonz.projeto_grupo4.EmergenciaAceita
import com.dijonz.projeto_grupo4.databinding.ActivityWaitBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import kotlin.concurrent.timer

class WaitActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private var auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityWaitBinding
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        val uidDentista = auth.currentUser?.uid.toString()

        val uidSocorrista = intent.getStringExtra("uid-socorrista")
        val telefoneSocorrista = intent.getStringExtra("telefone-socorrista")
        val idSocorrista = intent.getStringExtra("id-socorrista")

        waitingResult(
            uidSocorrista,
            uidDentista,
            telefoneSocorrista.toString(),
            idSocorrista.toString()
        )
    }

    private fun waitingResult(
        uidSocorrista: String?,
        uidDentista: String,
        telefone: String,
        docId: String
    ) {
        listenerRegistration = db.collection("emergencias").document(docId)
            .addSnapshotListener { value: DocumentSnapshot?, error ->
                if (value != null && value.exists()) {
                    val status = value.getBoolean("status")
                    val dentistas = value.get("dentistas")

                    if (status == true) {
                        if (dentistas?.toString() == uidDentista) {
                            binding.tvWaiting.text = "Você foi selecionado para a emergência!"
                            Thread.sleep(1_000)
                            val intent = Intent(this, EmergenciaAceita::class.java)
                            intent.putExtra("uid-socorrista", uidSocorrista)
                            intent.putExtra("telefone-socorrista", telefone)
                            startActivity(intent)
                        } else {
                            binding.tvWaiting.text = "Outro dentista foi selecionado para a emergência!"
                            Thread.sleep(1_000)
                            val intent = Intent(this, CadastroConcluido::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }
}