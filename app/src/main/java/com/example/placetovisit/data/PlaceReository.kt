package com.example.placetovisit.data

import kotlinx.coroutines.flow.Flow

class PlaceRepository(private val placeDAO: PlaceDAO) {

    suspend fun addPlace(place: Place){
        placeDAO.addPlace(place)
    }

    fun getPlaces(): Flow<List<Place>> = placeDAO.getALlPlaces()

    fun getPlaceById(placeId: Long): Flow<Place?> {
        return placeDAO.getPlaceById(placeId)
    }
    suspend fun deletePlaceById(id: Long) {
        placeDAO.deleteById(id)
    }
    suspend fun updatePlace(place: Place) {
        placeDAO.updatePlace(place)
    }


}