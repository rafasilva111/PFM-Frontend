package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.dtos.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import retrofit2.Response
import javax.inject.Inject

class GoalRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference,
) : GoalRepository {

    private val TAG:String = "AuthRepositoryImp"

    /**
     * Generic function to handle API requests and responses.
     *
     * @param liveData The LiveData where the result will be posted.
     * @param saveSharedPreferences Flag to determine if the result should be saved in SharedPreferences.
     * @param apiCall The API call to be executed.
     */
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        saveSharedPreferences: Boolean = false,
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

                    if (saveSharedPreferences) {
                        when (responseBody) {
                            is Goal -> sharedPreference.saveFitnessGoal(responseBody)
                        }
                    }


                } else {
                    // Handle the case where the response body is null
                    liveData.postValue(Event(NetworkResult.Error("Response body is null")))
                }
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.e(TAG, "API request was not successful. Error: \n$errorObj")
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

    private val _functionGetIdealWeightLiveData = MutableLiveData<Event<NetworkResult<FitnessReport>>>()
    override val getFitnessModelLiveData: LiveData<Event<NetworkResult<FitnessReport>>>
        get() = _functionGetIdealWeightLiveData

    override suspend fun getFitnessModel() {
        handleApiResponse(
            _functionGetIdealWeightLiveData
        ) {
            remoteDataSource.getFitnessReport()
        }
    }


    private val _createFitnessGoalLiveData = MutableLiveData<Event<NetworkResult<Goal>>>()
    override val createFitnessGoalLiveData: LiveData<Event<NetworkResult<Goal>>>
        get() = _createFitnessGoalLiveData

    override suspend fun createFitnessGoal(goalDTO: GoalDTO) {
        handleApiResponse(
            _createFitnessGoalLiveData,
            saveSharedPreferences = true
        ) {
            remoteDataSource.createFitnessGoal(goalDTO)
        }
    }

}