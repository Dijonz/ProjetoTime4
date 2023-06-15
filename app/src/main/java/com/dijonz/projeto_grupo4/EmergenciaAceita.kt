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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maps.route.extensions.drawRouteOnMap

class EmergenciaAceita : AppCompatActivity(), OnMapReadyCallback{
    private var auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private lateinit var binding: ActivityEmergenciaAceitaBinding
    private var userlat: LatLng = LatLng(00.0, 00.0)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var docId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergenciaAceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.cvLocalizacao) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val uidSocorrista = intent.getStringExtra("uid-socorrista")
        db.collection("emergencias").whereEqualTo("postID",uidSocorrista.toString()).get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    binding.tvNome.text = doc.data["nome"].toString()
                    binding.tvTelefone.text = doc.data["telefone"].toString()
                    docId = doc.id
                }
            }

        binding.cvEmergencias.setOnClickListener {
            db.collection("emergencias").document(docId).update("status","finalizado").addOnSuccessListener {
                Toast.makeText(this, "EMERGÊNCIA ENCERRADA", Toast.LENGTH_LONG).show()
                Thread.sleep(1_000)
                val intent = Intent(this, CadastroConcluido::class.java)
                startActivity(intent)
            }
        }

        if (checkPermissions()) {
            dentistaLocation()
            mapFragment.getMapAsync {googleMap ->
                googleMap.run{
                    drawRouteOnMap(
                        getString(R.string.google_api_key),
                        source = userlat,
                        destination = LatLng(-23.5870167, -46.6690508),
                        context = applicationContext
                    )
                }
            }
        } else {
            requestPermissions()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.run{
            drawRouteOnMap(
                getString(R.string.google_api_key),
                source = LatLng(-23.587157,-46.6825137),
                destination = LatLng(-23.5870167, -46.6690508),
                context = applicationContext
            )
        }
    }

    private fun dentistaLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location!=null) {
                val lng = location.longitude
                val lat = location.latitude
                userlat = LatLng(lng, lat)
            }
        }
    }


    //PERMISSIONS
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dentistaLocation()
            } else {
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    private fun checkPermissions(): Boolean {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
    }
}

