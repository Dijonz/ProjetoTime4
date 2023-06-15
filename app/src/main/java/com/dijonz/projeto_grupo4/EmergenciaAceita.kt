package com.dijonz.projeto_grupo4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dijonz.projeto_grupo4.databinding.ActivityEmergenciaAceitaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maps.route.extensions.drawRouteOnMap

class EmergenciaAceita : AppCompatActivity() {
    private var auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private lateinit var binding: ActivityEmergenciaAceitaBinding
    private var userlat: LatLng? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergenciaAceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestLocationPermission()

        if (checkLocationPermission()) {
            fusedLocationProviderClient!!.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val currentlatitude = location!!.latitude
                    val currentlongitude = location.longitude
                    userlat = LatLng(currentlatitude, currentlongitude)
                }
        }

        val uidsocorro = intent.getStringExtra("uid-socorrista")

        val uidSocorrista = intent.getStringExtra("uid-socorrista")
        db.collection("emergencias").document(uidSocorrista.toString()).get()
            .addOnSuccessListener { result ->
                binding.tvNome.text = result.data?.get("nome")?.toString()
                binding.tvTelefone.text = result.data?.get("telefone")?.toString()
            }

        /*db.collection("emergencias").document(uidSocorrista.toString())
            .update("medicoLatLng", userlat)*/


        binding.cvEmergencias.setOnClickListener {
            db.collection("emergencias").document(uidSocorrista.toString())
                .update("status", "finalizado").addOnSuccessListener {
                    Toast.makeText(this, "EMERGÊNCIA ENCERRADA", Toast.LENGTH_LONG).show()
                    Thread.sleep(1_000)
                    val intent = Intent(this, CadastroConcluido::class.java)
                    startActivity(intent)
                }
        }

        /*if (checkLocationPermission()){
        db.collection("emergencias")
            .document(uidsocorro.toString())
            .get()
            .addOnSuccessListener { result ->
                val latuser= result.data?.get("latitude") as Double
                val longuser = result.data?.get("longitude") as Double
                userlat = LatLng(latuser,longuser)

            fusedLocationProviderClient!!.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        val currentlatitude = location!!.latitude
                        val currentlongitude = location!!.longitude
                        val currentLatLng = LatLng(currentlatitude, currentlongitude)*/

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.cvLocalizacao) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->

            googleMap?.run {
                drawRouteOnMap(
                    getString(R.string.google_api_key),
                    source = LatLng(-23.587157, -46.6825137),
                    destination = LatLng(-23.5870167, -46.6690508),
                    context = applicationContext
                )
            }
        }
    }
    /*else{
        requestLocationPermission()
    }*/

    fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }
}

