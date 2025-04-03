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

    @Query("Select * From Orte WHERE current = 1 ")
    fun getCurrent():kotlinx.coroutines.flow.Flow<OrteEntity>

    @Query("UPDATE Orte SET current = CASE WHEN name=:selectedName THEN 1 ELSE 0 END")
    suspend fun setCurrent(selectedName:String)
}