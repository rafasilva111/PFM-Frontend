package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
) : RecipeRepository {

    private val TAG:String = "RecipeRepositoryImp"

    private val _recipeResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeListResponse>>>()
    override val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
        get() = _recipeResponseLiveData


    override suspend fun getRecipesPaginated(page: Int) {
        _recipeResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.getRecipesPaginated(page)

        //handle response RecipeListResponse

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _recipeResponseLiveData.postValue(Event(NetworkResult.Success(
             response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _recipeResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _recipeResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _recipeSearchByTitleAndTagsResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeListResponse>>>()
    override val recipeSearchByTitleAndTagsResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
        get() = _recipeSearchByTitleAndTagsResponseLiveData


    override suspend fun getRecipesByTitleAndTags(string: String, searchPage: Int) {
        _recipeSearchByTitleAndTagsResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.getRecipesByTitleAndTags(string,searchPage)

        //handle response RecipeListResponse

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _recipeSearchByTitleAndTagsResponseLiveData.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _recipeSearchByTitleAndTagsResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _recipeSearchByTitleAndTagsResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

}