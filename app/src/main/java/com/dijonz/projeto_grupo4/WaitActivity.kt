package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dijonz.projeto_grupo4.databinding.ActivityWaitBinding
import com.google.firebase.auth.FirebaseAuth
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uidDentista = auth.currentUser?.uid.toString()

        val uidSocorrista = intent.getStringExtra("uid-socorrista")

        waitingResponse(uidSocorrista,uidDentista)

    }
    fun waitingResponse(uidSocorrista: String?, uidDentista:String) {
        FirebaseFirestore.getInstance().collection("emergencias").whereEqualTo("postUID",uidSocorrista)
            .addSnapshotListener { value, error ->
                for (document in value!!.documents){
                    if (document.data?.get("status") == true){
                        if (document.data!!["dentistas"]==uidDentista){
                            binding.tvWaiting.text = "Você foi selecionado para a emergência!"
                            Thread.sleep(2_000)
                            val intent = Intent(this, EmergenciaAceita::class.java)
                            intent.putExtra("uid-socorrista",uidSocorrista)
                            startActivity(intent)

                        } else{
                            binding.tvWaiting.text = "Outro dentista foi selecionado para a emergência!"
                            Thread.sleep(2_000)
                            val intent = Intent(this,CadastroConcluido::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }


    }
}