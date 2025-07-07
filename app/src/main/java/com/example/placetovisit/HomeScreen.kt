package com.example.placetovisit

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.placetovisit.data.Place
import com.example.placetovisit.ui.theme.AdventureGradient
import com.example.placetovisit.ui.theme.DarkEleganceGradient



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PlaceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
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
                title = { Text("My Places", fontSize = 24.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.ExtraBold) },
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
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
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
            }
        }
    }
}
