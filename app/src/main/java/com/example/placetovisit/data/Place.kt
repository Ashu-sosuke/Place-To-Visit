package com.example.placetovisit.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Place-table")
data class Place(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "place-title")
    val title: String,
    @ColumnInfo(name = "description-title")
    val description: String,
    @ColumnInfo(name = "date-title")
    val date: String,
    @ColumnInfo(name = "location-title")
    val location: String,
    @ColumnInfo(name = "imageUri-title")
    val imageUri: String
)