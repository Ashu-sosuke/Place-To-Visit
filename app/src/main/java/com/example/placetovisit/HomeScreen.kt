package com.example.placetovisit

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.placetovisit.data.Place
import com.example.placetovisit.ui.theme.AdventureGradient
import com.example.placetovisit.ui.theme.DarkEleganceGradient
import androidx.core.net.toUri
import java.io.File

// No package or import changes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PlaceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val placeList by viewModel.getAllPlaces.collectAsState(initial = emptyList())

    var selectedPlaces by remember { mutableStateOf(setOf<Long>()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    fun toggleSelection(place: Place) {
        selectedPlaces = if (selectedPlaces.contains(place.id)) {
            selectedPlaces - place.id
        } else {
            selectedPlaces + place.id
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Selected Places") },
            text = { Text("Are you sure you want to delete ${selectedPlaces.size} selected place(s)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePlaceByIds(selectedPlaces.toList())
                        selectedPlaces = emptySet()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "My Places",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            "Explore. Remember. Share 🌍",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_location_city_24),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 12.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        val youtubeUrl = "https://youtu.be/xvFZjo5PgG0?si=RIARkzm2G5697Txr"
                        val intent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB5C6E0),
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            if (selectedPlaces.isEmpty()) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddPage.route) },
                    containerColor = Color.White,
                    contentColor = Color(0xFF00C9FF)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            } else {
                ExtendedFloatingActionButton(
                    text = { Text("Delete (${selectedPlaces.size})") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = null
                        )
                    },
                    onClick = { showDeleteDialog = true },
                    containerColor = Color(0xFFFF6F61),
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkEleganceGradient)
                .padding(innerPadding)
        ) {
            if (placeList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No places added yet!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    items(placeList, key = { it.id }) { place ->
                        PlaceCard(
                            place = place,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            onClick = {
                                if (selectedPlaces.isEmpty()) {
                                    navController.navigate(Screen.EditScreen.createRoute(place.id))
                                } else {
                                    toggleSelection(place)
                                }
                            },
                            onLongClick = {
                                toggleSelection(place)
                            },
                            isSelected = selectedPlaces.contains(place.id)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaceCard(
    place: Place,
    modifier: Modifier = Modifier,
    onClick: (Place) -> Unit,
    onLongClick: (Place) -> Unit,
    isSelected: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = modifier
                .padding(8.dp)
                .defaultMinSize(minHeight = 180.dp)
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = { onClick(place) },
                    onLongClick = { onLongClick(place) }
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFF44475A) else Color(0xFF2E3440),
                contentColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (place.imageUri.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = place.imageUri,
                            placeholder = painterResource(R.drawable.baseline_image_24),
                            error = painterResource(R.drawable.baseline_image_24)
                        ),
                        contentDescription = "Place Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = place.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📍 ${place.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "📅 ${place.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        val imageUri = place.imageUri.toUri()

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                """
                📍 ${place.title}
                
                🗺 Location: ${place.location}
                📅 Date: ${place.date}
                📝 Description: ${place.description}
                
                Shared via PlaceToVisit App
            """.trimIndent()
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        try {
                            context.startActivity(Intent.createChooser(shareIntent, "Share this place"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to open share dialog", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share this place",
                            tint = Color.White
                        )
                    }

                }
            }
        }
    }
}
