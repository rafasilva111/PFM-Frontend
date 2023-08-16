package com.example.projectfoodmanager.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.api.ApiNotificationInterface
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.data.repository.NotificationRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val apiInterface: ApiNotificationInterface, val repository: NotificationRepository) : ViewModel() {



    private val _connectionError = MutableStateFlow("")
    private val _response = MutableStateFlow("")


    val connectionError: LiveData<String>
        get() = _connectionError.asLiveData()

    val response: LiveData<String>
        get() = _response.asLiveData()


    fun sendNotification(notificationModel: PushNotification) {


        viewModelScope.launch(Dispatchers.IO) {
            _connectionError.emit("sending")
            try {
                val response = apiInterface.sendNotification(notificationModel)
                if (response.isSuccessful) {
                    //_response.emit(response.toString())
                    Log.d("TAG", "Notification in Kotlin: ${response.body()} ")

                    _connectionError.emit("sent")
                } else {
                    _connectionError.emit("error while sending")
                    Log.d("TAG", "Notification in Kotlin1: ${response.errorBody()} ")

                }
            }catch (e:Exception){
                _connectionError.emit("error while sending")
                Log.d("TAG", "Notification in Kotlin2: ${e.message} ")

            }
        }

    }

    val functionPostNotification: LiveData<Event<NetworkResult<ResponseBody>>>
        get() = repository.functionPostNotification

    fun postNotification(notificationModel: PushNotification) {
        viewModelScope.launch {
            repository.postNotification(notificationModel)
        }
    }



}