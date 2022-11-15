package com.example.projectfoodmanager.di

import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.data.repository.RecipeRepositoryImp
import com.google.firebase.firestore.FirebaseFirestore
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
        database: FirebaseFirestore
    ): RecipeRepository{
        return RecipeRepositoryImp(database)
    }
}