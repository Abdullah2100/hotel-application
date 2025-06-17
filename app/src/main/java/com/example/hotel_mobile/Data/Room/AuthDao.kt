package com.example.hotel_mobile.Data.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface AuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuthData(authData :AuthModleEntity)

    @Query("SELECT * FROM AuthModleEntity")
    suspend  fun getAuthData():AuthModleEntity?

    @Query("DELETE FROM AuthModleEntity ")
   suspend fun nukeTable()


    @Query("SELECT * FROM AuthModleEntity WHERE id = 0")
     fun   readChunksLive(): Flow<AuthModleEntity?>;

}