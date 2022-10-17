package ru.netology.markers.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.markers.db.AppDb
import ru.netology.markers.db.MarkerDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Provides
    @Singleton
    fun provideMarkerDao(db: AppDb) : MarkerDao = db.markerDao

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context) : AppDb {
        return Room.databaseBuilder(context, AppDb::class.java, "app.db").build()
    }
}