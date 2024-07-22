package com.example.projectfoodmanager.di


import com.example.projectfoodmanager.data.repository.*
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSourceImpl
import com.example.projectfoodmanager.data.repository.UserRepository
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
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
    fun recipeRepository(
        remoteDataSource: RemoteDataSourceImpl,
        gson: Gson
    ): RecipeRepository {
        return RecipeRepositoryImp(remoteDataSource,gson)
    }

    @Provides
    @Singleton
    fun providesAuthRepository(
        remoteDataSource: RemoteDataSourceImpl,
        firebaseMessaging: FirebaseMessaging,
        sharedPreference: SharedPreference,
        tokenManager: TokenManager,
        gson: Gson
    ): UserRepository {
        return UserRepositoryImp(
            remoteDataSource = remoteDataSource,
            firebaseMessaging= firebaseMessaging,
            sharedPreference = sharedPreference,
            tokenManager= tokenManager,
            gson = gson
        )
    }

    @Provides
    @Singleton
    fun providesCalenderRepository(
        remoteDataSource: RemoteDataSourceImpl,
        sharedPreference: SharedPreference
    ): CalenderRepository {
        return CalenderRepositoryImp(
            remoteDataSource = remoteDataSource,
            sharedPreference = sharedPreference
        )
    }

    @Provides
    @Singleton
    fun providesMiscellaneousRepository(
        remoteDataSource: RemoteDataSourceImpl
    ): MiscellaneousRepository {
        return MiscellaneousRepositoryImp(
            remoteDataSource = remoteDataSource
        )
    }



    @Provides
    @Singleton
    fun providesShoppingListRepository(
        remoteDataSource: RemoteDataSourceImpl,
        sharedPreference: SharedPreference
    ): ShoppingListRepository {
        return ShoppingListRepositoryImp(
            remoteDataSource = remoteDataSource,
            sharedPreference = sharedPreference
        )
    }


    @Provides
    @Singleton
    fun providesGoalRepository(
        remoteDataSource: RemoteDataSourceImpl,
        sharedPreference: SharedPreference
    ): GoalRepository {
        return GoalRepositoryImp(
            remoteDataSource = remoteDataSource,
            sharedPreference = sharedPreference
        )
    }
}