package com.example.placetovisit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    navController: NavController,
    viewModel: PlaceViewModel
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    val markerState = rememberMarkerState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyBf8LbKqdKwq41forRrQmkFuh-s49EqXZ0")
        }
        Places.createClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLastKnownLocation(context) { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    markerPosition = latLng
                    markerState.position = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val status = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (status == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation(context) { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    markerPosition = latLng
                    markerState.position = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick a Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            // 🔍 Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.text.length >= 3) {
                        fetchPlacesSuggestions(placesClient, it.text) { result ->
                            suggestions = result
                        }
                    } else {
                        suggestions = emptyList()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                label = { Text("Search location") }
            )

            // 📍 Suggestions List
            suggestions.forEach { prediction ->
                TextButton(
                    onClick = {
                        val placeId = prediction.placeId
                        val fields = listOf(
                            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
                        )
                        val request = FetchPlaceRequest.builder(placeId, fields).build()
                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response ->
                                val latLng = response.place.latLng
                                if (latLng != null) {
                                    markerPosition = latLng
                                    markerState.position = latLng
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(latLng, 15f)
                                    searchQuery = TextFieldValue(prediction.getPrimaryText(null).toString())
                                    suggestions = emptyList()
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(prediction.getFullText(null).toString())
                }
            }

            // 🗺️ Map
            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        markerPosition = it
                        markerState.position = it
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)

                    }
                ) {
                    markerPosition?.let {
                        Marker(
                            state = markerState,
                            title = "Selected Location",
                            draggable = true
                        )
                    }
                }

                Button(
                    onClick = {
                        markerPosition?.let {
                            viewModel.setSelectedLatLng(it)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Save Location")
                }
            }
        }
    }

    LaunchedEffect(markerState.position) {
        markerPosition = markerState.position
    }
}

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
private fun getLastKnownLocation(context: Context, callback: (Location?) -> Unit) {
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener { callback(it) }
        .addOnFailureListener { callback(null) }
}

private fun fetchPlacesSuggestions(
    placesClient: PlacesClient,
    query: String,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    val bounds = RectangularBounds.newInstance(
        LatLng(-85.0, -180.0),
        LatLng(85.0, 180.0)
    )
    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .setLocationBias(bounds)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { e ->
            Log.e("Places", "Error: ${e.message}")
            onResult(emptyList())
        }
}
