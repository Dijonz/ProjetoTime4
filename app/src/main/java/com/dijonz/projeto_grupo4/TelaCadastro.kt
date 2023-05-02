package com.dijonz.projeto_grupo4

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dijonz.projeto_grupo4.databinding.TelaCadastroBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class TelaCadastro : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: TelaCadastroBinding
    private lateinit var functions: FirebaseFunctions
    private val auth = FirebaseAuth.getInstance()
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private var x:Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TelaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btCadastrar.setOnClickListener {
            if(emptyVerifier(binding.etNomeCompleto, binding.etTelefone, binding.etEmail, binding.etSenha, binding.etEndereco, binding.etMiniCurriculo)) {

                val us = user(
                    binding.etNomeCompleto.text.toString(), binding.etEmail.text.toString(),
                    binding.etTelefone.text.toString(), binding.etEndereco.text.toString(),
                    binding.etMiniCurriculo.text.toString()
                )

                auth.createUserWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etSenha.text.toString()
                ).addOnCompleteListener { usuarioA ->
                    if (usuarioA.isSuccessful) {
                        cadastrarUsuario(us,binding.etEmail.text.toString() )
                        x += 1
                    }
                }.addOnFailureListener { exception ->
                    val MensagemErro = when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Digite um email válido"
                        is FirebaseAuthUserCollisionException -> "Conta já Cadastrada!"
                        else -> "Erro no cadastro"
                    }
                    Toast.makeText(this, MensagemErro, Toast.LENGTH_SHORT).show()
                }

                if (x >= 1) {
                    Toast.makeText(this, "CADASTRO REALIZADO!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            else{
                val snack = Snackbar.make((binding.root),"Preencha todos os dados!",Snackbar.LENGTH_SHORT)
                snack.setBackgroundTint(Color.RED)
                snack.setTextColor(Color.WHITE)
                snack.show()
            }
    }
    }
    private fun emptyVerifier(nome: EditText, numero: EditText, email: EditText, senha: EditText, endereco: EditText, curriculo: EditText): Boolean{
        return !(TextUtils.isEmpty(nome.text) || TextUtils.isEmpty(numero.text) ||TextUtils.isEmpty(email.text) ||
                TextUtils.isEmpty(senha.text) || TextUtils.isEmpty(endereco.text) || TextUtils.isEmpty(curriculo.text))
    }
    //private fun cadastrarUsuario(p: user,id: String) {
    //    val data = hashMapOf(
    //        "name" to p.name,
    //        "number" to p.number,
    //        "email" to p.email,
    //        "endereco" to p.endereco,
    //        "curriculo" to p.curriculo,
    //        "status" to false
    //    )
    //    db.collection("users").document(id).set(data) ou db.collection("users").add(data)
    //}

    private fun cadastrarUsuario(p: user,id: String): Task<CustomResponse> {
        functions = Firebase.functions("southamerica-east1")
        val data = hashMapOf(
            "nome" to p.name,
            "email" to p.email,
            "numero" to p.number,
            "curriculo" to p.curriculo,
            "endereco" to p.endereco,
            "status" to false
       )
        return functions
            .getHttpsCallable("setUser")
            .call(data)
           .continueWith { task ->
               val result = gson.fromJson((task.result?.data as String), CustomResponse::class.java)
               result
            }
    }
}





