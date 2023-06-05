package com.dijonz.projeto_grupo4

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dijonz.projeto_grupo4.databinding.ActivityFotoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.security.AllPermission
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.log

class FotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFotoBinding
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var id: String
    private val auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore

    private lateinit var outPutDirectory: File

    private var  imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = auth.currentUser?.uid

        db.collection("users")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    id = document.id
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, it.message.toString())
            }

        outPutDirectory = getOutPutDirectory()

        if (allPermissionGranted()) {
            startCamera()
            binding.buttonFoto.setOnClickListener {
                takePhoto()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

    }

    private fun getOutPutDirectory(): File{
        val mediaDirs = externalMediaDirs.firstOrNull()?.let {mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if(mediaDirs != null && mediaDirs.exists())
            mediaDirs else filesDir
    }

    private fun takePhoto() {
        val userAtual = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val imageCapture = imageCapture?:return
        val photoFile = File(
            outPutDirectory ,
            userAtual)

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Foto salva!"
                    storage.reference.child("dentistas/").child("${userAtual}.jpeg") .putFile(savedUri) .addOnCompleteListener{
                        val map = HashMap<String, Any>()
                        map["pic"] = savedUri.toString()
                        db.collection("users").document(id).update("foto",map).addOnCompleteListener {
                            Toast.makeText(this@FotoActivity, msg, Toast.LENGTH_SHORT)
                            val lntent = Intent(this@FotoActivity, CadastroConcluido::class.java)
                            startActivity(lntent)
                        }
                    }

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG,"deu erro", exception)
                }

            }
        )
    }


    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider =  cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {mPreview ->
                mPreview.setSurfaceProvider(
                    binding.camerPreview.surfaceProvider
                )
            }
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            }catch (e: java.lang.Exception){
                Log.d(Constants.TAG,"ERRO AO INICIAR A CAMERA",e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==Constants.REQUEST_CODE_PERMISSIONS){
            if (allPermissionGranted()){
                startCamera()

            } else{
                Toast.makeText(this, "Permissões não concedidas", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED

        }
}