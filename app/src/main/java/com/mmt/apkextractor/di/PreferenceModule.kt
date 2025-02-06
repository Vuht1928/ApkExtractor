package com.mmt.apkextractor.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mmt.apkextractor.BuildConfig
import com.mmt.apkextractor.data.preferences.PreferenceRepository
import com.mmt.apkextractor.data.preferences.PreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PreferenceModule {
    companion object {
        private const val PREFERENCES = "settings"
        private const val SHARED_PREFERENCES = "${BuildConfig.APPLICATION_ID}_preferences"

        @Singleton
        @Provides
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }),
                migrations = listOf(SharedPreferencesMigration(context, SHARED_PREFERENCES)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { context.preferencesDataStoreFile(PREFERENCES) })
        }
    }

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferenceRepositoryImpl: PreferenceRepositoryImpl
    ): PreferenceRepository
}