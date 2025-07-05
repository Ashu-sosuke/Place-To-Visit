package com.example.placetovisit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placetovisit.data.Place
import com.example.placetovisit.data.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaceViewModel(
    private val placeRepository: PlaceRepository = Graph.placeRepository
): ViewModel() {

       lateinit var getAllPlaces: Flow<List<Place>>

       init {
           viewModelScope.launch {
               getAllPlaces = placeRepository.getPlaces()
           }
       }

    fun addPlace(place: Place){
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.addPlace(place = place)
        }
    }
    fun updatePlace(place: Place){
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.updateAPlace(place = place)
        }
    }

    fun getAPlaceById(id: Long): Flow<Place>{
        return placeRepository.getPlaceById(id)
    }
    fun deletePlace(place: Place){
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.deleteAPlace(place = place)
        }
    }


}