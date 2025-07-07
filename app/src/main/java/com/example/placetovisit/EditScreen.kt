package com.example.placetovisit

import android.app.DatePickerDialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.placetovisit.data.Place
import java.io.File
import java.util.*
import android.provider.Settings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    place: Place,
    navController: NavController,
    viewModel: PlaceViewModel,

) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(place.title) }
    var description by remember { mutableStateOf(place.description) }
    var date by remember { mutableStateOf(place.date) }
    var location by remember { mutableStateOf(place.location) }

    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(place.imageUri)) }
    var showImagePicker by remember { mutableStateOf(false) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = saveImageToDevice(context, it)
            imageUri = savedUri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile!!
            )
            val savedUri = saveImageToDevice(context, uri)
            imageUri = savedUri
        }
    }

    fun launchCamera(context: Context) {
        val imageFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        photoFile = imageFile
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
        cameraLauncher.launch(uri)
    }

    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = { Text("Select Image") },
            text = {
                Column {
                    Text("Choose image from:")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        showImagePicker = false
                        launchCamera(context)
                    }) {
                        Text("Camera")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        showImagePicker = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Gallery")
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = convertMillisToDate(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Camera and storage permissions are required. Please enable them in settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Place") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.HomeScreen.route)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            val selectedLatLng by viewModel.selectedLatLng

            LaunchedEffect(selectedLatLng) {
                selectedLatLng?.let {
                    location = "${it.latitude}, ${it.longitude}"
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Button(onClick = {
                        navController.navigate(Screen.MapScreen.route)
                    }) {
                        Text("Pick")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_image_24),
                        contentDescription = "Image Placeholder",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Gray
                    )
                }

                Button(onClick = {
                    requestPermissions(
                        context = context,
                        onPermissionGranted = {
                            showImagePicker = true
                        },
                        onPermissionPermanentlyDenied = {
                            showSettingsDialog = true
                        }
                    )
                }) {
                    Text("Change Image")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && date.isNotBlank()
                        && location.isNotBlank() && imageUri != null
                    ) {
                        val updatedPlace = place.copy(
                            title = title,
                            description = description,
                            date = date,
                            location = location,
                            imageUri = imageUri.toString()
                        )
                        viewModel.updatePlace(updatedPlace)
                        navController.navigate(Screen.HomeScreen.route)
                    } else {
                        Toast.makeText(context, "Please fill all fields and add an image", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update")
            }
        }
    }
}
