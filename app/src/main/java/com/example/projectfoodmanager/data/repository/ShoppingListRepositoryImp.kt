package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingIngredientListRequest
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientListList

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import retrofit2.Response
import javax.inject.Inject

class ShoppingListRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
) : ShoppingListRepository {

    private val TAG: String = "ShoppingListImp"

    // Generic function to handle API requests and responses
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        apiCall: suspend () -> Response<T>
    ) {
        // Post a loading state to indicate the request is in progress
        liveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "Making API request.")

        try {
            // Invoke the API call
            val response = apiCall.invoke()

            // Check if the API request was successful
            if (response.isSuccessful) {
                // Post a success result with the response body
                liveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "API request was successful. Error: \n$errorObj")
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

    private val _getShoppingListsLiveData = MutableLiveData<Event<NetworkResult<ShoppingIngredientListList>>>()
    override val getShoppingListsLiveData: LiveData<Event<NetworkResult<ShoppingIngredientListList>>>
        get() = _getShoppingListsLiveData

    override suspend fun getShoppingLists() {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_getShoppingListsLiveData) {
            remoteDataSource.getShoppingList()
        }
    }

    private val _getShoppingListLiveData =
        MutableLiveData<Event<NetworkResult<ShoppingIngredientList>>>()
    override val getShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingIngredientList>>>
        get() = _getShoppingListLiveData

    override suspend fun getShoppingList(shoppingListId: Int) {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_getShoppingListLiveData) {
            remoteDataSource.getShoppingList(shoppingListId)
        }
    }

    // post

    private val _postShoppingListLiveData = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val postShoppingListLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = _postShoppingListLiveData

    override suspend fun postShoppingList(shoppingIngredientListRequest: ShoppingIngredientListRequest) {
        // Use the handleApiResponse function to handle the API call and update LiveData
        handleApiResponse(_postShoppingListLiveData) {
            remoteDataSource.postShoppingList(shoppingIngredientListRequest)
        }
    }
}