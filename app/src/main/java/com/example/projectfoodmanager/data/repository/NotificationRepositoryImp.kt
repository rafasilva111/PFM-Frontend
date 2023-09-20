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
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : NotificationRepository {

    private val TAG: String = "NotificationRepositoryImp"

    // Generic function to handle API requests and responses
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        apiCall: suspend () -> Response<T>
    ) {
        liveData.postValue(Event(NetworkResult.Loading()))
        Log.d(TAG, "Sending notification.")

        try {
            val response = apiCall.invoke()

            // Check if the API request was successful
            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Request was successful.")
                // Post a success result with the response body
                liveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.d(TAG, "Request was not successful. Error: $errorObj")
                // Post an error result with the error message
                liveData.postValue(Event(NetworkResult.Error(errorObj)))
            } else {
                // Handle the case where something went wrong without an error body
                liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e: Exception) {
            // Handle exceptions here if needed
            Log.e(TAG, "Request failed with exception: ${e.message}")
            // Post an error result for exceptions
            liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionPostNotification =
        MutableLiveData<Event<NetworkResult<ResponseBody>>>()
    override val functionPostNotification: LiveData<Event<NetworkResult<ResponseBody>>>
        get() = _functionPostNotification

    override suspend fun postNotification(notificationModel: PushNotification) {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_functionPostNotification) {
            remoteDataSource.sendNotification(notificationModel)
        }
    }
}