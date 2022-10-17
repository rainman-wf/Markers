package ru.netology.markers.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

import ru.netology.markers.BuildConfig.MAPS_API_KEY

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPS_API_KEY)
    }
}