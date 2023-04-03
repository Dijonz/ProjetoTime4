package com.dijonz.projeto_grupo4

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dijonz.projeto_grupo4.databinding.TelaCadastroBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: TelaCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TelaCadastroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btCadastrar.setOnClickListener {

            val dentista = hashMapOf(
                    "nome" to binding.etNomeCompleto.text.toString(),
                    "telefone" to binding.etTelefone.text.toString().toInt(),
                    "endereco1" to binding.etEndereco.text.toString(),
                    "email" to binding.etEmail.text.toString(),
                    "senha" to binding.etSenha.text.toString(),
                    "curriculo" to binding.etMiniCurriculo.text.toString()
            )

            db.collection("dentistas")
                .add(dentista)
                .addOnSuccessListener {
                        Toast.makeText(this,"Cadastro Realizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"ERRO", Toast.LENGTH_SHORT).show()
                    }
        }
    }
}