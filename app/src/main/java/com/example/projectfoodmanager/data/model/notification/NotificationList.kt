package com.example.projectfoodmanager.data.model.notification

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationList(
    val _metadata: Metadata,
    @SerializedName("not_seen")
    val notSeen: Int,
    val result: MutableList<Notification>
): Parcelable