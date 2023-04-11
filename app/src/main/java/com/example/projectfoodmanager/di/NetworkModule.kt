package com.example.projectfoodmanager.di

import android.util.Log
import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.api.AuthInterceptor
import com.example.projectfoodmanager.util.Constants
import okhttp3.OkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
	@Provides
	@Singleton
	fun providesRetrofit(authInterceptor: AuthInterceptor): Retrofit {
		val interceptor = HttpLoggingInterceptor().apply {
			this.level = HttpLoggingInterceptor.Level.BODY
		}

		val client = OkHttpClient.Builder().apply {
			this.addInterceptor(authInterceptor)
				.addInterceptor(interceptor)
				.connectTimeout(30, TimeUnit.SECONDS)
				.readTimeout(20, TimeUnit.SECONDS)
				.writeTimeout(25, TimeUnit.SECONDS)
		}.build()

		return Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.baseUrl(Constants.BASE_URL)
			.build()
	}

	@Provides
	@Singleton
	fun providesAPI(retrofit: Retrofit): ApiInterface {
		return retrofit.create(ApiInterface::class.java)
	}

}