package com_2is.egypt.wipegadmin.domain.di_module

import android.content.Context
import android.os.Looper
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com_2is.egypt.wipegadmin.domain.gateways.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DIModule {
    @Singleton
    @Provides
    fun providesGsonBuilder(): Gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create()


    @Singleton
    @Provides
    fun providesForegroundServiceGateway(@ApplicationContext context: Context)
            : ForegroundServiceGateway = ForegroundServiceGateway(context)

    @Singleton
    @Provides
    fun providesServiceGateway(preferenceGateway: PreferenceGateway, gson: Gson): ServiceGateway =
        ServiceGateway(preferences = preferenceGateway, gson = gson)

    @Singleton
    @Provides
    fun providesDatabaseGatewayAsync(@ApplicationContext context: Context): ItemsDatabase =
        Room.databaseBuilder(context, ItemsDatabase::class.java, "items")
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun providesItemsDao(database: ItemsDatabase) = database.itemsDao

    @Singleton
    @Provides
    fun providesServerMaterialsDao(database: ItemsDatabase) = database.serverMaterialDao

    @Singleton
    @Provides
    fun providesUploadMaterialDao(database: ItemsDatabase) = database.materialDao

    @Singleton
    @Provides
    fun providesRecordsDao(database: ItemsDatabase): RecordsDao = database.recordsDao


}
