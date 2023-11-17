package com.example.projectfoodmanager.di
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.api.ApiNotificationInterface
import com.example.projectfoodmanager.data.api.AuthInterceptor
import com.example.projectfoodmanager.util.Constants
import com.example.projectfoodmanager.util.FIREBASE_NOTIFICATIONS
import okhttp3.OkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirstRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecondRetrofit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

	// First set of code
	@FirstRetrofit
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

		val gson = GsonBuilder()
			.registerTypeAdapter(LocalDateTime::class.java, object : TypeAdapter<LocalDateTime>() {
				@Throws(IOException::class)
				override fun write(out: JsonWriter, value: LocalDateTime) {
					out.value(value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))
				}

				@Throws(IOException::class)
				override fun read(input: JsonReader): LocalDateTime {
					return LocalDateTime.parse(input.nextString(), DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
				}
			})
			.create()

		return Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.baseUrl(Constants.BASE_URL)
			.build()
	}

	@Provides
	@Singleton
	fun providesAPI(@FirstRetrofit retrofit: Retrofit): ApiInterface {
		return retrofit.create(ApiInterface::class.java)
	}


}