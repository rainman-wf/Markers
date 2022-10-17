package ru.netology.markers.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.markers.models.UserMarker

@Database(
    entities = [
        UserMarker::class],
    version = 1
)
abstract class AppDb : RoomDatabase() {

    abstract val markerDao: MarkerDao

}