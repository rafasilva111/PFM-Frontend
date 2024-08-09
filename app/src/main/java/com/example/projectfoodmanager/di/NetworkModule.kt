package com.example.projectfoodmanager.di
import android.content.Context
import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.api.AuthApiInterface
import com.example.projectfoodmanager.data.api.AuthAuthenticator
import com.example.projectfoodmanager.data.api.AuthInterceptor
import com.example.projectfoodmanager.util.Constants
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import okhttp3.OkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirstRetrofit



@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

	@Singleton
	@Provides
	fun provideTokenManager(@ApplicationContext context: Context): TokenManager = TokenManager(context)


	@Singleton
	@Provides
	fun provideOkHttpClient(
		authInterceptor: AuthInterceptor,
		authAuthenticator: AuthAuthenticator,
	): OkHttpClient {
		val loggingInterceptor = HttpLoggingInterceptor()
		loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

		return OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.addInterceptor(authInterceptor)
			.authenticator(authAuthenticator)
			.build()
	}

	@Singleton
	@Provides
	fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
		AuthInterceptor(tokenManager)

	@Singleton
	@Provides
	fun provideAuthAuthenticator(tokenManager: TokenManager): AuthAuthenticator =
		AuthAuthenticator(tokenManager)

	@Singleton
	@Provides
	fun provideRetrofitBuilder(): Retrofit.Builder =
		Retrofit.Builder()
			.baseUrl(Constants.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())

	@Singleton
	@Provides
	fun provideAuthAPIService(retrofit: Retrofit.Builder): AuthApiInterface =
		retrofit
			.build()
			.create(AuthApiInterface::class.java)

	@Singleton
	@Provides
	fun provideMainAPIService(okHttpClient: OkHttpClient, retrofit: Retrofit.Builder): ApiInterface =
		retrofit
			.client(okHttpClient)
			.build()
			.create(ApiInterface::class.java)

	// First set of code
	/*@FirstRetrofit
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
	}*/

}