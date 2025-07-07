package com.example.placetovisit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun addPlace(placeEntity: Place)

    //Load all places from table
    @Query("SELECT * from `place-table`")
     fun getALlPlaces(): Flow<List<Place>>

    @Update
     suspend fun updatePlace(place: Place)

    @Delete
     suspend fun deletePlace(placeEntity: Place)

    @Query("SELECT * from `place-table` where id=:placeId LIMIT 1")
     fun getPlaceById(placeId: Long): Flow<Place>

    @Query("DELETE FROM `Place-table` where id = :id")
    suspend fun deleteById(id: Long)

}
