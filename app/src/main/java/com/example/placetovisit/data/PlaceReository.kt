package com.example.placetovisit.data

import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val placeDAO: PlaceDAO) {

    suspend fun addPlace(place: Place){
        placeDAO.addPlace(place)
    }

    fun getPlaces(): Flow<List<Place>> = placeDAO.getALlPlaces()

    fun getPlaceById(id: Long) : Flow<Place> {
        return placeDAO.getPlaceById(id)
    }

    suspend fun updateAPlace(place: Place){
        placeDAO.updatePlace(place)
    }
    suspend fun deleteAPlace(place: Place){
        placeDAO.deletePlace(place)
    }
}