package ru.netology.markers.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.markers.models.UserMarker

@Dao
interface MarkerDao {

    @Query("SELECT * FROM markers")
    fun getAll() : Flow<List<UserMarker>>

    @Insert (onConflict = REPLACE)
    suspend fun save(userMarker: UserMarker)

    @Query("DELETE FROM markers WHERE id = :id")
    suspend fun remove(id: Long)
}