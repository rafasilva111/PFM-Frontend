package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import okhttp3.ResponseBody
import javax.inject.Inject

class NotificationRepositoryImp@Inject constructor(private val remoteDataSource: RemoteDataSource):NotificationRepository {

    private val TAG:String = "RecipeRepositoryImp"

    private val _functionPostNotification = MutableLiveData<Event<NetworkResult<ResponseBody>>>()
    override val functionPostNotification: LiveData<Event<NetworkResult<ResponseBody>>>
        get() = _functionPostNotification


    override suspend fun postNotification(notificationModel: PushNotification) {
        _functionPostNotification.postValue(Event(NetworkResult.Loading()))
        Log.d(TAG, "NotificationRepositoryImp - postNotification: Sending notification.")
        val response = remoteDataSource.sendNotification(notificationModel)

        //handle response RecipeListResponse

        if (response.isSuccessful && response.body() != null) {
            Log.d(TAG, "NotificationRepositoryImp - postNotification: Request was sucessfull.")
            Log.d(TAG, "NotificationRepositoryImp - postNotification: Response body -> ${response.body()}.")
            _functionPostNotification.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            try {
                Log.d(TAG, "NotificationRepositoryImp - postNotification: Request was not sucessfull.")
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.d(TAG, "NotificationRepositoryImp - postNotification: $errorObj")
                _functionPostNotification.postValue(Event(NetworkResult.Error(errorObj)))
            } catch (_: Exception) {

            }
        }
        else{
            _functionPostNotification.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }
}