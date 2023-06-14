package com.dijonz.projeto_grupo4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dijonz.projeto_grupo4.databinding.ActivityEmergenciaAceitaBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maps.route.extensions.drawRouteOnMap
import kotlin.system.exitProcess

class EmergenciaAceita : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private lateinit var binding: ActivityEmergenciaAceitaBinding
    private var userlat = LatLng(12.3,12.3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergenciaAceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uidsocorro= intent.getStringExtra("uid-socorrista")

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return exitProcess(0)
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val currentlatitude = location!!.latitude
                val currentlongitude = location!!.longitude
                val currentLatLng = LatLng(currentlatitude,currentlongitude)

                db.collection("emergencias")
                    .document(uidsocorro.toString())
                    .get()
                    .addOnSuccessListener { result ->
                        val latuser: Double = result.data?.get("latitude") as Double
                        val longuser: Double = result.data?.get("longitude") as Double
                        userlat = LatLng(latuser,longuser)
                    }

                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.cvLocalizacao) as SupportMapFragment
                mapFragment.getMapAsync{ googleMap ->

                    googleMap?.run{
                        drawRouteOnMap(
                            getString(R.string.google_api_key),
                            source = currentLatLng,
                            destination = userlat,
                            context = applicationContext
                        )
                    }
                }
            }

        val uidSocorrista = intent.getStringExtra("uid-socorrista")


        db.collection("emergencias").document(uidSocorrista.toString()).get().addOnSuccessListener {result ->
            binding.tvNome.text = result.data?.get("nome")?.toString()
            binding.tvTelefone.text = result.data?.get("telefone")?.toString()
        }


        binding.cvEmergencias.setOnClickListener {
            db.collection("emergencias").document(uidSocorrista.toString()).update("status", "finalizado").addOnSuccessListener {
                Toast.makeText(this,"EMERGÊNCIA ENCERRADA",Toast.LENGTH_LONG)
                Thread.sleep(2_000)
                val intent = Intent(this,CadastroConcluido::class.java)
                startActivity(intent)
            }
        }

    }
}