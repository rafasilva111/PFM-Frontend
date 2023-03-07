package com.example.projectfoodmanager.di


import android.content.SharedPreferences
import com.example.projectfoodmanager.data.old.AuthRepositoryImp_old
import com.example.projectfoodmanager.data.old.AuthRepository_old
import com.example.projectfoodmanager.data.old.RecipeRepositoryImp_old
import com.example.projectfoodmanager.data.old.RecipeRepository_old
import com.example.projectfoodmanager.data.repository.*
import com.example.projectfoodmanager.data.repository.datasourImp.RemoteDataSourceImpl
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun recipeRepositoryOld(
        database: FirebaseFirestore
    ): RecipeRepository_old {
        return RecipeRepositoryImp_old(database)
    }

    @Provides
    @Singleton
    fun recipeRepository(
        remoteDataSource: RemoteDataSourceImpl,
        sharedPreference: SharedPreference
    ): RecipeRepository {
        return RecipeRepositoryImp(remoteDataSource,sharedPreference)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson

    ): AuthRepository_old {
        return AuthRepositoryImp_old(auth,database,appPreferences, gson )
    }

    @Provides
    @Singleton
    fun providesRepository(
        remoteDataSource: RemoteDataSourceImpl,
        sharedPreference: SharedPreference
    ): AuthRepository {
        return AuthRepositoryImp(
            remoteDataSource = remoteDataSource,
            sharedPreference = sharedPreference
        )
    }
}