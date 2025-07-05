package com.example.placetovisit

import android.content.Context
import androidx.room.Room
import com.example.placetovisit.data.PlaceDatabase
import com.example.placetovisit.data.PlaceRepository

object Graph {
    lateinit var database: PlaceDatabase

    val placeRepository by lazy {
        PlaceRepository(placeDAO = database.placeDAO())
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(context, PlaceDatabase::class.java, "placelist.db").build()
    }
}