package com.example.placetovisit.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
abstract class PlaceDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addPlace(placeEntity: Place)

    //Load all places from table
    @Query("SELECT * from `place-table`")
    abstract fun getALlPlaces(): Flow<List<Place>>

    @Update
    abstract suspend fun updatePlace(placeEntity: Place)

    @Delete
    abstract suspend fun deletePlace(placeEntity: Place)

    @Query("SELECT * from `place-table` where id=:id")
    abstract fun getPlaceById(id: Long): Flow<Place>


}
