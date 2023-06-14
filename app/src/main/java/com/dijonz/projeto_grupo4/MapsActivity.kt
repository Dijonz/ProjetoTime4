package com.dijonz.projeto_grupo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dijonz.projeto_grupo4.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapsBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}