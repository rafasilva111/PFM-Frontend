package com.codelytical.flybuy.presentation.di

import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.repository.datasourImp.UserRemoteDataSourceImpl
import com.example.projectfoodmanager.data.repository.datasource.UserRemoteDataSource
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
	fun provideFlyBuyRemoteDataSource(apiInterface: ApiInterface): UserRemoteDataSource {
		return UserRemoteDataSourceImpl(apiInterface = apiInterface)
	}
}