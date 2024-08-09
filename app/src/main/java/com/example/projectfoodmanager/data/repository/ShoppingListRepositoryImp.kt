package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelResponse.IdResponse
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import retrofit2.Response
import javax.inject.Inject

class ShoppingListRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
) : ShoppingListRepository {

    private val TAG: String = "ShoppingListImp"

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
        deleteSharedPreferences: Boolean = false,
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

                    // Optionally save data to SharedPreferences

                    if (saveSharedPreferences ) {
                        when (responseBody) {
                            is ShoppingList -> sharedPreference.saveShoppingList(responseBody)
                            is ListOfShoppingLists -> sharedPreference.saveMultipleShoppingList(responseBody.result)
                            else -> Log.e(TAG, "Unable to save this type into shared preferences...")
                        }
                    }

                    if (deleteSharedPreferences) {
                        when (responseBody) {
                            is  IdResponse -> sharedPreference.deleteShoppingList(responseBody.id)
                            is ShoppingList -> sharedPreference.deleteShoppingList(responseBody.id)
                            else -> Log.e(TAG, "Unable to save this type into shared preferences...")
                        }
                    }

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

    // get

    private val _getShoppingListsLiveData = MutableLiveData<Event<NetworkResult<ListOfShoppingLists>>>()
    override val getShoppingListsLiveData: LiveData<Event<NetworkResult<ListOfShoppingLists>>>
        get() = _getShoppingListsLiveData

    override suspend fun getUserShoppingLists() {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_getShoppingListsLiveData, saveSharedPreferences = true) {
            remoteDataSource.getShoppingList()
        }
    }

    private val _getShoppingListLiveData = MutableLiveData<Event<NetworkResult<ShoppingList>>>()
    override val getShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = _getShoppingListLiveData

    override suspend fun getShoppingList(shoppingListId: Int) {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_getShoppingListLiveData) {
            remoteDataSource.getShoppingList(shoppingListId)
        }
    }

    // post

    private val _postShoppingListLiveData = MutableLiveData<Event<NetworkResult<ShoppingList>>>()
    override val postShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = _postShoppingListLiveData

    override suspend fun postShoppingList(shoppingListRequest: ShoppingListRequest) {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_postShoppingListLiveData, saveSharedPreferences = true) {
            remoteDataSource.postShoppingList(shoppingListRequest)
        }
    }

    // put

    private val _putShoppingListLiveData = MutableLiveData<Event<NetworkResult<ShoppingList>>>()
    override val putShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = _putShoppingListLiveData

    override suspend fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest) {
        handleApiResponse(_putShoppingListLiveData, saveSharedPreferences = true) {
            remoteDataSource.putShoppingList(shoppingListId,shoppingListRequest)
        }
    }

    override suspend fun archiveShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest) {
        handleApiResponse(_putShoppingListLiveData, deleteSharedPreferences = true) {
            remoteDataSource.putShoppingList(shoppingListId,shoppingListRequest)
        }
    }

    // delete

    private val _deleteShoppingListLiveData = MutableLiveData<Event<NetworkResult<IdResponse>>>()
    override val deleteShoppingListLiveData: LiveData<Event<NetworkResult<IdResponse>>>
        get() = _deleteShoppingListLiveData

    override suspend fun deleteShoppingList(shoppingListId: Int) {
        handleApiResponse(_deleteShoppingListLiveData, deleteSharedPreferences = true) {
            remoteDataSource.deleteShoppingList(shoppingListId)
        }
    }
}