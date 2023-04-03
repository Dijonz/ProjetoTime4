package com.dijonz.projeto_grupo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dijonz.projeto_grupo4.databinding.ActivityCadastroConcluidoBinding

class CadastroConcluido : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroConcluidoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroConcluidoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

}