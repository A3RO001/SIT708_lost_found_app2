package com.example.lostfoundapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lostfoundapp.R
import com.example.lostfoundapp.database.DatabaseHelper
import com.example.lostfoundapp.model.Item
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var placesClient: PlacesClient
    
    private var selectedLat: Double = 0.0
    private var selectedLng: Double = 0.0
    
    private val AUTOCOMPLETE_REQUEST_CODE = 2
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var rgType: RadioGroup
    private lateinit var tvHeader: TextView
    private lateinit var imagePreview: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)
        dbHelper = DatabaseHelper(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etLocation = findViewById(R.id.etLocation)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        rgType = findViewById(R.id.rgType)
        tvHeader = findViewById(R.id.tvHeader)
        imagePreview = findViewById(R.id.imagePreview)
        progressBar = findViewById(R.id.progressBar)
        btnSave = findViewById(R.id.btnSave)

        val categories = arrayOf("Electronics", "Pets", "Wallets")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnSearchLocation).setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        findViewById<Button>(R.id.btnCurrentLocation).setOnClickListener { getCurrentLocation() }

        findViewById<Button>(R.id.btnImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        findViewById<Button>(R.id.btnBackHome).setOnClickListener { finish() }

        rgType.setOnCheckedChangeListener { _, checkedId ->
            tvHeader.text = getString(if (checkedId == R.id.rbLost) R.string.report_lost_item else R.string.report_found_item)
        }

        etLocation.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedLat = 0.0
                selectedLng = 0.0
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        btnSave.setOnClickListener { validateAndSave() }
    }

    private fun validateAndSave() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val locationName = etLocation.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val type = if (rgType.checkedRadioButtonId == R.id.rbLost) "Lost" else "Found"

        if (title.isEmpty() || description.isEmpty() || locationName.isEmpty() || imageUri == null) {
            val msg = when {
                title.isEmpty() -> "Please enter a title"
                description.isEmpty() -> "Please enter a description"
                locationName.isEmpty() -> "Please select a location"
                else -> "Please upload an image"
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedLat == 0.0 && selectedLng == 0.0) {
            geocodeAndSave(title, description, category, type, locationName)
        } else {
            saveItem(title, description, category, type, locationName, selectedLat, selectedLng)
        }
    }

    private fun geocodeAndSave(title: String, desc: String, cat: String, type: String, loc: String) {
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        val request = FindAutocompletePredictionsRequest.builder().setQuery(loc).build()
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val prediction = response.autocompletePredictions.firstOrNull()
            if (prediction != null) {
                val fetchRequest = FetchPlaceRequest.newInstance(prediction.placeId, listOf(Place.Field.LAT_LNG))
                placesClient.fetchPlace(fetchRequest).addOnSuccessListener { fetchResponse ->
                    val place = fetchResponse.place
                    saveItem(title, desc, cat, type, loc, place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
                }.addOnFailureListener { handleFailure(it) }
            } else {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { handleFailure(it) }
    }

    private fun handleFailure(e: Exception) {
        progressBar.visibility = View.GONE
        btnSave.isEnabled = true
        Toast.makeText(this, "Geocoding failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }

    private fun saveItem(title: String, desc: String, cat: String, type: String, loc: String, lat: Double, lng: Double) {
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val item = Item(
            title = title,
            description = desc,
            category = cat,
            imageUri = imageUri.toString(),
            dateTime = dateTime,
            type = type,
            location = loc,
            latitude = lat,
            longitude = lng
        )
        dbHelper.insertItem(item)
        Toast.makeText(this, "Item Saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                selectedLat = location.latitude
                selectedLng = location.longitude
                etLocation.setText(getString(R.string.current_location_display, selectedLat, selectedLng))
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    imageUri = data?.data
                    imagePreview.setImageURI(imageUri)
                    imagePreview.visibility = View.VISIBLE
                }
                AUTOCOMPLETE_REQUEST_CODE -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    etLocation.setText(place.name)
                    selectedLat = place.latLng?.latitude ?: 0.0
                    selectedLng = place.latLng?.longitude ?: 0.0
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
