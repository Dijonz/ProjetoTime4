package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dijonz.projeto_grupo4.databinding.ActivityTelaEmergenciasBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TelaEmergencias : AppCompatActivity() {

    private lateinit var binding: ActivityTelaEmergenciasBinding
    private val db = Firebase.firestore
    private lateinit var listaNomesEmergencias: MutableList<String>
    private lateinit var listaTelefones: MutableList<String>

    abstract class ItemDetailsLookup<K>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaEmergenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaNomesEmergencias = mutableListOf<String>()
        listaTelefones = mutableListOf<String>()

        binding.rvEmergencias.apply {
            layoutManager = LinearLayoutManager(this@TelaEmergencias)
            binding.rvEmergencias.setHasFixedSize(true)

        }
        buscarDados()

        binding.toolbar.setOnClickListener {
            val intent = Intent(this, CadastroConcluido::class.java)
            startActivity(intent)
        }





    }

    private fun buscarDados() {
        FirebaseFirestore.getInstance().collection("emergencias").whereEqualTo("status", false)
            .get()
            .addOnSuccessListener {result ->

                    for(document in result){
                        listaNomesEmergencias.add(document.data["nome"].toString())
                        listaTelefones.add(document.data["telefone"].toString())

                }
                var adapter = AdapterEmergencia(listaNomesEmergencias, listaTelefones)
                binding.rvEmergencias.adapter = adapter
                adapter.setOnItemClickListener(object: AdapterEmergencia.onItemClickListener{
                    override fun onItemClick(position: Int) {
                        var telefone = listaTelefones[position]

                        val intent = Intent(this@TelaEmergencias,infoEmergencia::class.java)
                        intent.putExtra("telefone-emergencia", telefone)
                        startActivity(intent)
                    }

                } )


            }.addOnFailureListener {
                Toast.makeText(this, "DEU ERRO NA BUSCA KKKKKKKKKKKKKKK", Toast.LENGTH_SHORT).show()
            }
    }
}