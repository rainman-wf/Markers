package ru.netology.markers.ui

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectDragListener
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.netology.markers.R
import ru.netology.markers.models.MarkerMetaData
import ru.netology.markers.models.UserMarker

class MapFunctions(
    private val mapView: MapView,
    private val context: Context
) {

    private val collection = mapView.map.mapObjects.addCollection()

    fun moveCamera(lat: Double, long: Double) {
        mapView.map.move(CameraPosition(Point(lat, long), 17f, 0f, 0f))
    }

    fun moveCamera(markers: List<UserMarker>) {
        val x0 = markers.maxOfOrNull { it.pointLat }
        val x1 = markers.minOfOrNull { it.pointLat }
        val y0 = markers.maxOfOrNull { it.pointLong }
        val y1 = markers.minOfOrNull { it.pointLong }

        val boundingBox = BoundingBox(Point(x0!!, y0!!), Point(x1!!, y1!!))

        val cameraPosition = mapView.map.cameraPosition(boundingBox)

        mapView.map.move(
            CameraPosition(
                cameraPosition.target,
                cameraPosition.zoom - 0.9f,
                0f,
                0F
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    fun addMapObject(
        marker: UserMarker,
        tapListener: MapObjectTapListener,
        dragListener: MapObjectDragListener? = null
    ) {
        val mapObject = collection.addPlacemark(
            Point(marker.pointLat, marker.pointLong),
            ImageProvider.fromBitmap(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_baseline_location_on_24
                )?.toBitmap() ?: error("missing image")
            )
        )
        mapObject.userData = MarkerMetaData(marker.description)


        mapObject.addTapListener(tapListener)
        dragListener?.let {
            mapObject.setDragListener(it)
            mapObject.isDraggable = true
        }
    }

    fun updateMapObject(
        marker: UserMarker,
        tapListener: MapObjectTapListener,
        dragListener: MapObjectDragListener? = null
    ) {
        collection.clear()
        addMapObject(marker, tapListener, dragListener)
    }


}