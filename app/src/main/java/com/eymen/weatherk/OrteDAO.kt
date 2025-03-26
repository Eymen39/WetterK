package com.eymen.weatherk

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orte :OrteEntity)

    @Delete
    suspend fun delete(orte:OrteEntity)

    @Query("Select * FROM Orte ORDER BY name ASC")
    fun getAllOrte():kotlinx.coroutines.flow.Flow<List<OrteEntity>>
}