package com.markdownpilot.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences as Prefs
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.markdownpilot.app.data.local.AppDatabase
import com.markdownpilot.app.util.C
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(C.PREFS)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext c: Context): DataStore<Preferences> = c.dataStore

    @Provides @Singleton
    fun provideDb(@ApplicationContext c: Context): AppDatabase =
        Room.databaseBuilder(c, AppDatabase::class.java, C.DB_NAME)
            .fallbackToDestructiveMigration().build()
}
