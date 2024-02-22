package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import retrofit2.Response
import javax.inject.Inject

class MiscellaneousRepositoryImp@Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : MiscellaneousRepository {

    private val TAG:String = "MiscellaneousRepositoryImp"

    /**
     * Generic function to handle API requests and responses.
     *
     * @param liveData The LiveData where the result will be posted.
     * @param saveSharedPreferences Flag to determine if the result should be saved in SharedPreferences.
     * @param apiCall The API call to be executed.
     */
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        apiCall: suspend () -> Response<T>
    ) {
        try {
            // Post a loading state to indicate the request is in progress
            liveData.postValue(Event(NetworkResult.Loading()))
            Log.i(TAG, "Making API request.")

            // Invoke the API call
            val response = apiCall.invoke()

            if (response.isSuccessful) {
                // API request was successful
                val responseBody = response.body()
                if (responseBody != null) {
                    // Post a success result with the response body
                    liveData.postValue(Event(NetworkResult.Success(responseBody)))

                } else {
                    // Handle the case where the response body is null
                    liveData.postValue(Event(NetworkResult.Error("Response body is null")))
                }
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "API request was not successful. Error: \n$errorObj")
                liveData.postValue(Event(NetworkResult.Error(errorObj)))
            } else {
                // Handle the case where something went wrong without an error body
                liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e: Exception) {
            // Handle exceptions here if needed
            Log.e(TAG, "API request failed with exception: ${e.message}")
            // Post an error result for exceptions
            liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _miscellaneousLiveData = MutableLiveData<Event<NetworkResult<String>>>()
    override val postAppReportLiveData: LiveData<Event<NetworkResult<String>>>
        get() = _miscellaneousLiveData

    override suspend fun postAppReport(applicationReport: ApplicationReport) {

        
        _miscellaneousLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.postAppReport(applicationReport)
        if (response.isSuccessful && response.code() == 201) {
            Log.i(TAG, "loginUser: request made was sucessfull.")
            _miscellaneousLiveData.postValue(Event(NetworkResult.Success(response.code().toString())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "loginUser: request made was not sucessfull: $errorObj")
            _miscellaneousLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _miscellaneousLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }
}