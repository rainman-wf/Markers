package ru.netology.markers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import ru.netology.markers.db.MarkerDao
import ru.netology.markers.models.UserMarker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveDataContainer @Inject constructor(
    markerDao: MarkerDao
) {
    val data = markerDao.getAll().flowOn(Dispatchers.Default).asLiveData(Dispatchers.Default)
    val currentMapObject = MutableLiveData<UserMarker>()
    val currentPointSet = SingleLiveEvent<UserMarker>()
    val mapObjectCreated = SingleLiveEvent<Unit>()
}
