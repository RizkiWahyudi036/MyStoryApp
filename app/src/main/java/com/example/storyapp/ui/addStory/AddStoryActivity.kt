package com.example.storyapp.ui.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyapp.Api.AddNewStoryResponse
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.preferences.SharedPreferences
import com.example.storyapp.utils.MyResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private var getFile: File? = null
    private lateinit var sph: SharedPreferences
    private var currentLatitude: Float? = null
    private var currentLongitude: Float? = null
    private val addViewModel by viewModels<AddViewModel> { AddViewModel.Factory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        sph = SharedPreferences(this)
        requestPermissionsIfNecessary()
        setupUI()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun requestPermissionsIfNecessary() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun setupUI() {
        binding.progressBar.visibility = View.GONE
        binding.btnOpenCamera.setOnClickListener { startCamera() }
        binding.btnOpenGalery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) getMyLocation()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.also {
            createCustomTempFile(application).also { file ->
                val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.storyapp", file)
                currentPhotoPath = file.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val bitmap = BitmapFactory.decodeFile(myFile.path)
            binding.imgView.setImageBitmap(bitmap)
            getMyLocation()
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
            return
        } else {
            val location = fusedLocationProvider.lastLocation
            location.addOnSuccessListener {
                if(it!=null){
                    currentLatitude = it.latitude.toFloat()
                    currentLongitude = it.longitude.toFloat()
                }
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.imgView.setImageURI(selectedImg)
            getMyLocation()
        }
    }

    private fun uploadImage() {
        getFile?.let { file ->
            val reducedFile = reduceFileImage(file)
            val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", reducedFile.name, requestImageFile)
            uploadImageToServer(imageMultipart)
        } ?: run {
            Toast.makeText(this, "Please insert the image file first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToServer(img: MultipartBody.Part) {
        val token = "Bearer ${sph.getUserToken()}"
        val description = binding.edAddDescription.text.trim().toString().toRequestBody("text/plain".toMediaType())
        val latitude = if (binding.checkBox.isChecked) currentLatitude ?: 0f else 0f
        val longitude = if (binding.checkBox.isChecked) currentLongitude ?: 0f else 0f
        addViewModel.addStories(token, description, img, latitude, longitude).observe(this) { result ->
            handleUploadResult(result)
        }
    }

    private fun handleUploadResult(result: MyResult<AddNewStoryResponse>?) {
        result?.let {
            when (it) {
                is MyResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                is MyResult.Success -> handleSuccess()
                is MyResult.Error -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleSuccess() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
        Intent(this, MainActivity::class.java).also { intent ->
            intent.putExtra(MainActivity.SUCCESS_UPLOAD_STORY, true)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}