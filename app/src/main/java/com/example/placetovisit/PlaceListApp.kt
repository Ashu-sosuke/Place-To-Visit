package com.example.placetovisit

import android.app.Application

class PlaceListApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}