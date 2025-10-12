package com.example.projectfoodmanager.di

import android.content.Context
import android.content.SharedPreferences
import com.example.projectfoodmanager.util.SharedPrefConstants
import com.google.gson.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF,Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, DateDeserializer())
            .create()
    }
    internal class DateDeserializer : JsonDeserializer<LocalDateTime> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
            return LocalDateTime.parse(json?.asString, DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
        }
    }

}