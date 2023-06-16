package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.google.gson.Gson
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val gson: Gson
) : RecipeRepository {

    private val TAG:String = "RecipeRepositoryImp"

    private val _recipeResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _recipeResponseLiveData


    override suspend fun getRecipesPaginated(page: Int) {
        _recipeResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.d(TAG, "RecipeRepositoryImp - getRecipesPaginated: Making request to page $page.")
        val response =remoteDataSource.getRecipesPaginated(page)

        //handle response RecipeListResponse

        if (response.isSuccessful && response.body() != null) {
            Log.d(TAG, "RecipeRepositoryImp - getRecipesPaginated: Request was sucessfull.")
            Log.d(TAG, "RecipeRepositoryImp - getRecipesPaginated: Response body -> ${response.body()}.")
            _recipeResponseLiveData.postValue(Event(NetworkResult.Success(
             response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            try {
                Log.d(TAG, "RecipeRepositoryImp - getRecipesPaginated: Request was not sucessfull.")
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.d(TAG, "RecipeRepositoryImp - getRecipesPaginated: $errorObj")
                _recipeResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            } catch (_: Exception) {

            }
        }
        else{
            _recipeResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _recipeSearchByTitleAndTagsResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val recipeSearchByTitleAndTagsResponseLiveData: LiveData<Event<NetworkResult<RecipeList>>>
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

    /// like function

    private val _userLikedRecipesResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val userLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _userLikedRecipesResponseLiveData



    override suspend fun getUserLikedRecipes() {
        _userLikedRecipesResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: Making request.")
        val response =remoteDataSource.getUserLikedRecipes()

        //handle response RecipeListResponse

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: Request was sucessfull.")
            Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: Response body -> ${response.body()}.")
            _userLikedRecipesResponseLiveData.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            try {
                Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: Request was not sucessfull.")
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: $errorObj")
                _userLikedRecipesResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            } catch (e: Exception) {
                Log.i(TAG, "e")
            }

        }
        else{
            _userLikedRecipesResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _functionLikeOnRecipe = MutableLiveData<Event<NetworkResult<Int>>>()
    override val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = _functionLikeOnRecipe

    private val _functionRemoveLikeOnRecipe = MutableLiveData<Event<NetworkResult<Int>>>()
    override val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = _functionRemoveLikeOnRecipe


    override suspend fun addLikeOnRecipe(recipeId: Int) {
        _functionLikeOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.addLike(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionLikeOnRecipe.postValue(Event(NetworkResult.Success(
                recipeId
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionLikeOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionLikeOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    override suspend fun removeLikeOnRecipe(recipeId: Int) {
        _functionRemoveLikeOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making removeLikeOnRecipe request.")
        // fazer alterações na shared preferences aqui

        val response =remoteDataSource.removeLike(recipeId)
        if (response.isSuccessful && response.code() == 204) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionRemoveLikeOnRecipe.postValue(Event(NetworkResult.Success(
                recipeId
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionRemoveLikeOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionRemoveLikeOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }

    }


    private val _functionAddSaveOnRecipe = MutableLiveData<Event<NetworkResult<Int>>>()
    override val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = _functionAddSaveOnRecipe

    private val _functionRemoveSaveOnRecipe = MutableLiveData<Event<NetworkResult<Int>>>()
    override val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = _functionRemoveSaveOnRecipe


    override suspend fun addSaveOnRecipe(recipeId: Int) {
        _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.addSave(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Success(
                recipeId
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    override suspend fun removeSaveOnRecipe(recipeId: Int) {
        _functionRemoveSaveOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making removeLikeOnRecipe request.")
        // fazer alterações na shared preferences aqui

        val response =remoteDataSource.removeSave(recipeId)
        if (response.isSuccessful && response.code() == 204) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionRemoveSaveOnRecipe.postValue(Event(NetworkResult.Success(
                recipeId
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionRemoveSaveOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionRemoveSaveOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionGetCommentsOnRecipe = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetCommentsOnRecipe: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetCommentsOnRecipe

    override suspend fun getCommentsOnRecipe(recipeId: Int) {
        _functionGetCommentsOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getCommentsByRecipe(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionGetCommentsOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionGetCommentsOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetCommentsOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

}