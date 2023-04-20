package com.dijonz.projeto_grupo4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import com.dijonz.projeto_grupo4.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bConfirmLogin.setOnClickListener {   if(verificaVazio(binding.etEmail, binding.etSenha)){

            auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etSenha.text.toString()).addOnCompleteListener {login ->

                if (login.isSuccessful){
                    val intent =  Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                }

            }

        }}


    }
    private fun verificaVazio(caixa: EditText, caixa2: EditText): Boolean{
        return !(TextUtils.isEmpty(caixa.text) && TextUtils.isEmpty(caixa2.text) )
    }
}