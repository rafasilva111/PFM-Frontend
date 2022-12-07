package com.example.projectfoodmanager.di


import android.content.SharedPreferences
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.data.repository.AuthRepositoryImp
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.data.repository.RecipeRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideRecipeRepository(
        database: FirebaseFirestore,
        storage: FirebaseStorage
    ): RecipeRepository{
        return RecipeRepositoryImp(database,storage)
    }



    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth,database,appPreferences,gson)
    }
}