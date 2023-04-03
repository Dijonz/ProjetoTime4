package com.dijonz.projeto_grupo4

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dijonz.projeto_grupo4.databinding.TelaCadastroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: TelaCadastroBinding

    private fun verificaVazio(nome: EditText, numero: EditText, email: EditText, senha: EditText, endereco: EditText, curriculo: EditText): Boolean{
        if(TextUtils.isEmpty(nome.text) || TextUtils.isEmpty(numero.text) || TextUtils.isEmpty(email.text) || TextUtils.isEmpty(senha.text) || TextUtils.isEmpty(endereco.text) || TextUtils.isEmpty(curriculo.text)) {
            return false }
        else{
            return true
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TelaCadastroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btCadastrar.setOnClickListener {


            if(verificaVazio(binding.etNomeCompleto, binding.etTelefone, binding.etEmail, binding.etSenha, binding.etEndereco, binding.etMiniCurriculo)) {


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
                        Toast.makeText(this, "Cadastro Realizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "ERRO", Toast.LENGTH_SHORT).show()
                    }
            } else {

                val snack = Snackbar.make(findViewById(android.R.id.content), "Olá, Snackbar!", Snackbar.LENGTH_SHORT)
                snack.setBackgroundTint(Color.RED)
                snack.setTextColor(Color.WHITE)
                snack.show()




            }
        }
    }
}