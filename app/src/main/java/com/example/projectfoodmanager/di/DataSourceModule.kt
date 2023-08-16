package com.example.projectfoodmanager.di

import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.api.ApiNotificationInterface
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSourceImpl
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

	@Provides
	@Singleton
	fun provideRemoteDataSource(apiInterface: ApiInterface, apiNotificationInterface: ApiNotificationInterface): RemoteDataSource {
		return RemoteDataSourceImpl(apiInterface = apiInterface, apiNotificationInterface = apiNotificationInterface)
	}
}