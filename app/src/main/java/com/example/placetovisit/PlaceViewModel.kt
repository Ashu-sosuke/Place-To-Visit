package com.example.placetovisit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.placetovisit.data.Place
import com.example.placetovisit.data.PlaceRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.compose.runtime.State


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

    fun getPlaceById(placeId: Long): Flow<Place?> {
        return placeRepository.getPlaceById(placeId)
    }

    fun deletePlaceByIds(ids: List<Long>){
        viewModelScope.launch(Dispatchers.IO) {
            ids.forEach { id->
                placeRepository.deletePlaceById(id)
            }
        }
    }

    fun updatePlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.updatePlace(place)
        }
    }


    private val _selectedLatLng = mutableStateOf<LatLng?>(null)
    val selectedLatLng: State<LatLng?> = _selectedLatLng

    fun setSelectedLatLng(latLng: LatLng) {
        _selectedLatLng.value = latLng
    }


}