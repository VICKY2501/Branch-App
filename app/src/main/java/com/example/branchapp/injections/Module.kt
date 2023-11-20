package com.example.branchapp.injections

import android.content.Context
import androidx.room.Room
import com.example.branchapp.utils.DataStorage
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.rishi.branchinternational.model.database.BranchInternationalDao
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.rishi.branchinternational.model.database.BranchInternationalRoomDatabase
import com.rishi.branchinternational.model.network.AuthInterceptor
import com.rishi.branchinternational.model.network.MessageApiService
import com.example.branchapp.models.repository.AppRepository
import com.example.branchapp.viewmodels.AppViewModelFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {
    private const val BASE_URL = "https://android-messaging.branch.co/"

    // Provides AuthInterceptor with required dependencies
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context,
        dataStoreUtil: DataStorage
    ): AuthInterceptor {
        return AuthInterceptor(dataStoreUtil)
    }

    // Provides DataStore instance
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStorage {
        return DataStorage(context)
    }

    // Provides OkHttpClient instance with interceptors
    @Provides
    @Singleton
    fun provideOkhttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(OkHttpProfilerInterceptor())
            .build()
    }

    // Provides Retrofit instance with converters and OkHttpClient
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    // Provides MessageApiService using Retrofit
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): MessageApiService {
        return retrofit.create(MessageApiService::class.java)
    }

    // Provides AppRepository using MessageApiService and background dispatcher
    @Provides
    @Singleton
    fun provideAppRepository(messageApiService: MessageApiService): AppRepository {
        return AppRepository(messageApiService, Dispatchers.IO)
    }

    // Provides BranchInternationalDao using RoomDatabase
    @Provides
    @Singleton
    fun provideBranchInternationalDao(database: BranchInternationalRoomDatabase): BranchInternationalDao {
        return database.branchInternationalDao()
    }

    // Provides BranchInternationalRoomDatabase using Room Database Builder
    @Provides
    @Singleton
    fun provideBranchInternationalDatabase(@ApplicationContext applicationContext: Context): BranchInternationalRoomDatabase {
        return Room.databaseBuilder(
            applicationContext,
            BranchInternationalRoomDatabase::class.java,
            "BranchInternationalDB"
        )
            .build()
    }

    // Provides BranchInternationalRepository using BranchInternationalDao
    @Provides
    @Singleton
    fun provideBranchInternationalRepository(branchInternationalDao: BranchInternationalDao): BranchInternationalRepository {
        return BranchInternationalRepository(branchInternationalDao)
    }

    // Provides AppViewModelFactory using AppRepository, DataStore, and BranchInternationalRepository
    @Provides
    @Singleton
    fun provideAppViewModelFactory(
        authRepository: AppRepository,
        dataStore: DataStorage,
        branchInternationalRepository: BranchInternationalRepository
    ): AppViewModelFactory {
        return AppViewModelFactory(authRepository, dataStore, branchInternationalRepository)
    }
}