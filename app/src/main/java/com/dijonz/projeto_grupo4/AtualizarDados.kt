package com.dijonz.projeto_grupo4

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import coil.load
import com.dijonz.projeto_grupo4.databinding.ActivityAtualizarDadosBinding
import com.dijonz.projeto_grupo4.databinding.ActivityInfoEmergenciaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AtualizarDados : AppCompatActivity() {

    private lateinit var binding: ActivityAtualizarDadosBinding
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarDadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("uid",uid).get().addOnSuccessListener {result ->
            for (document in result){id = document.id}
        }
        definirFoto(uid)


        binding.tvAtualizarFoto.setOnClickListener {
            val intent = Intent(this, FotoActivity::class.java)
            startActivity(intent)
        }

        binding.ivHome.setOnClickListener {
            val intent = Intent(this, CadastroConcluido::class.java)
            startActivity(intent)
        }


        binding.btCadastrar.setOnClickListener {
            atualizarDados(id)
        }

}

    private fun atualizarDados(id: String){
        val dbRef = FirebaseFirestore.getInstance().collection("users").document(id)

        if (binding.curriculo.text?.isNotEmpty() == true) {
            dbRef.update("curriculo", binding.curriculo.text.toString()).addOnSuccessListener {
                binding.curriculo.setText("")
                binding.curriculo.hint = "ATUALIZADO!"
            }
        }
        if (binding.email.text?.isNotEmpty()==true){
            dbRef.update("email",binding.email.text.toString()).addOnSuccessListener {
                FirebaseAuth.getInstance().currentUser?.updateEmail(binding.email.text.toString())?.addOnSuccessListener {
                    binding.email.setText("")
                    binding.email.hint = "ATUALIZADO!"
                }

            }
        }
        if (binding.telefone.text?.isNotEmpty()==true){
            dbRef.update("telefone",binding.telefone.text.toString()).addOnSuccessListener {
                binding.telefone.setText("")
                binding.telefone.hint = "ATUALIZADO!"
            }
        }
        if (binding.endereco.text?.isNotEmpty()==true){
            dbRef.update("endereco", binding.endereco.text.toString()).addOnSuccessListener {
                binding.endereco.setText("")
                binding.endereco.hint = "ATUALIZADO!"
            }
        }
        if (binding.nome.text?.isNotEmpty()==true){
            dbRef.update("nome", binding.nome.text.toString()).addOnSuccessListener {
                binding.nome.setText("")
                binding.nome.hint = "ATUALIZADO!"
            }
        }

    }

    private fun definirFoto(uid: String) {


        FirebaseStorage.getInstance().reference.child("dentistas")
            .child("${uid}.jpeg").downloadUrl.addOnSuccessListener { uri ->
                var foto = uri.toString()
                binding.ivProfile.load(foto)
            }
    }

    fun criarToast(texto: String) {
        val toast = Toast.makeText(applicationContext, texto, Toast.LENGTH_SHORT)
        toast.show()
    }
}