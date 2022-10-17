package ru.netology.markers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.markers.db.MarkerDao
import ru.netology.markers.models.UserMarker
import javax.inject.Inject

@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val markerDao: MarkerDao,
    val liveDataContainer: LiveDataContainer
) : ViewModel() {


    fun onSaveClicked() {
        viewModelScope.launch {
            liveDataContainer.currentMapObject.value?.let {
                markerDao.save(it)
                liveDataContainer.mapObjectCreated.postValue(Unit)
                clearCurrentMapObject()
            }
        }
    }

    fun setCurrentMapObject(id: Long) {
        liveDataContainer.data.value?.find { it.id == id }?.let {
            liveDataContainer.currentMapObject.postValue(it)
            liveDataContainer.currentPointSet.postValue(it)
        }
    }

    fun updateCurrentMapObject(lat: Double, long: Double, description: String) {
        liveDataContainer.currentMapObject.postValue(
            liveDataContainer.currentMapObject.value?.copy(
                pointLat = lat,
                pointLong = long,
                description = description
            ) ?: UserMarker(0, lat, long, description)
        )
    }

    fun onRemoveClicked(id: Long) {
        viewModelScope.launch {
            markerDao.remove(id)
        }
    }

    fun clearCurrentMapObject() {
        liveDataContainer.currentMapObject.postValue(null)
    }
}