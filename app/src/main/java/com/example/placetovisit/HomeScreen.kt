package com.example.placetovisit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Places") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {

            }) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ){ innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding )) {

        }

    }
}