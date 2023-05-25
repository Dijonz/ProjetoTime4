package com.dijonz.projeto_grupo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dijonz.projeto_grupo4.databinding.ActivityTelaAceitesBinding

class TelaAceites : AppCompatActivity() {
    private lateinit var binding: ActivityTelaAceitesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaAceitesBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}