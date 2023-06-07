package com.dijonz.projeto_grupo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dijonz.projeto_grupo4.databinding.ActivityEmergenciaBinding

class EmergenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergenciaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}