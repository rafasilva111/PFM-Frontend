package com.codelytical.flybuy.presentation.di

import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.api.AuthInterceptor
import com.example.projectfoodmanager.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

	@Singleton
	@Provides
	fun providesRetrofit(): Retrofit.Builder {
		return Retrofit.Builder().baseUrl(Constants.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
	}

	@Singleton
	@Provides
	fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
		return OkHttpClient.Builder().addInterceptor(interceptor).build()
	}

	@Singleton
	@Provides
	fun providesAPI(retrofitBuilder: Retrofit.Builder): ApiInterface {
		return retrofitBuilder.build().create(ApiInterface::class.java)
	}

}