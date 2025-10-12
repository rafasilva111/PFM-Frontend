package com.example.projectfoodmanager.data.model.modelResponse.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RefreshToken(
    var refresh: String,
): Parcelable
