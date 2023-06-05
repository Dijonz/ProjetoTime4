package com.dijonz.projeto_grupo4

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import com.dijonz.projeto_grupo4.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

        override fun onStart() {
            super.onStart()
            val user = auth.currentUser



            if (user != null) {
                val intentt = Intent(this, CadastroConcluido::class.java)
                startActivity(intentt)
            } else {

                binding = ActivityLoginBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.bConfirmLogin.setOnClickListener {
                    if (verificaVazio(binding.etEmail, binding.etSenha)) {
                        auth.signInWithEmailAndPassword(
                            binding.etEmail.text.toString(),
                            binding.etSenha.text.toString()
                        ).addOnCompleteListener { login ->

                            if (login.isSuccessful) {
                                val intent = Intent(this, CadastroConcluido::class.java)
                                startActivity(intent)
                            }
                        }.addOnFailureListener { fail ->
                            val MensagemErro = when (fail) {
                                is FirebaseAuthInvalidCredentialsException -> "Email ou senha errados!"
                                else -> "Erro no login"
                            }
                            val snack =
                                Snackbar.make((binding.root), MensagemErro, Snackbar.LENGTH_SHORT)
                            snack.setBackgroundTint(Color.RED)
                            snack.setTextColor(Color.WHITE)
                            snack.show()
                        }
                    }
                }

                binding.tvSemConta.setOnClickListener {
                    val intent = Intent(this, TelaCadastro::class.java)
                    startActivity(intent)
                }
            }
        }

    private fun verificaVazio(caixa: EditText, caixa2: EditText): Boolean{
        return !(TextUtils.isEmpty(caixa.text) && TextUtils.isEmpty(caixa2.text) )
    }
}