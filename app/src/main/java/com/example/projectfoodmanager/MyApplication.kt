package com.example.projectfoodmanager

import android.app.Application
import com.example.projectfoodmanager.data.model.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    var session: User? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun getSessionUser(): User? {
        return session
    }

    fun setSessionUser(): User? {
        return session
    }
}