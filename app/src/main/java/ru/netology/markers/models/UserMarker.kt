package ru.netology.markers.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markers")
data class UserMarker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "latitude") val pointLat: Double,
    @ColumnInfo(name = "longitude") val pointLong: Double,
    val description: String
)
