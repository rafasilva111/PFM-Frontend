package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.google.gson.Gson
import retrofit2.Response
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val gson: Gson
) : RecipeRepository {

    private val TAG:String = "RecipeRepositoryImp"

    // Helper function to make API requests and handle responses
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        request: suspend () -> Response<T>
    ) {
        // Notify observers that a loading operation is in progress
        liveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "Making API request.")

        try {
            // Invoke the API request using the provided lambda function
            val response = request.invoke()

            if (response.isSuccessful && response.body() != null) {
                // Handle a successful API response
                Log.d(TAG, "Request was successful.")
                liveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            } else if (response.errorBody() != null) {
                // Handle an error response with an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.d(TAG, "Request was not successful. \n$errorObj")
                liveData.postValue(Event(NetworkResult.Error(errorObj)))
            } else {
                // Handle an error response without an error body
                liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e: Exception) {
            // Handle exceptions that may occur during the API request
            Log.e(TAG, "Error making API request: ${e.message}")
            liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    /**
     * Recipes
     */

    // LiveData for sorted recipes
    private val _recipesResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val recipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _recipesResponseLiveData


    // Function to get paginated and sorted recipes
    override suspend fun getRecipes(page: Int,pageSize: Int,userId: Int,searchString:String,searchTag: String, by:String) {


        handleApiResponse(
            _recipesResponseLiveData
        ) {
            remoteDataSource.getRecipes(userId,by,searchString,searchTag, page,pageSize)
        }
    }


    // LiveData for searched recipes

    private val _recipesCommentedByUserSearchPaginated = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val recipesCommentedByUser: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _recipesCommentedByUserSearchPaginated


    // Function to search recipes by clients
    override suspend fun getRecipesCommentedByUser(page: Int, clientId: Int, searchString:String?) {
        // Use the helper function to make the API request
        searchString?.let {
            handleApiResponse(
                _recipesCommentedByUserSearchPaginated
            ) {
                remoteDataSource.getRecipesByClientSearchPaginated(clientId, searchString, page)
            }
        }?: run {
            // Code to execute when `nullableValue` is null
            handleApiResponse(
                _recipesCommentedByUserSearchPaginated
            ) {
                remoteDataSource.getRecipesByClientPaginated(clientId,page)
            }
        }

    }

    /**
     * Like Function
     */

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
        // todo fazer alterações na shared preferences aqui

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
        // todo fazer alterações na shared preferences aqui

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

    /// COMMENTS SECTION

    private val _functionGetCommentsOnRecipePaginated = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetCommentsOnRecipePaginated

    override suspend fun getCommentsByRecipePaginated(recipeId: Int, page: Int) {
        _functionGetCommentsOnRecipePaginated.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getCommentsByRecipePaginated(recipeId,page)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionGetCommentsOnRecipePaginated.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionGetCommentsOnRecipePaginated.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetCommentsOnRecipePaginated.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionCreateCommentOnRecipe = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionPostCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>
        get() = _functionCreateCommentOnRecipe

    override suspend fun createCommentOnRecipe(recipeId: Int,comment: CreateCommentRequest) {
        _functionCreateCommentOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.createComments(recipeId,comment)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionCreateCommentOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionCreateCommentOnRecipe.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionCreateCommentOnRecipe.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    // LiveData for searched recipes
    private val _functionGetSizedCommentsOnRecipePaginated = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetSizedCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetSizedCommentsOnRecipePaginated


    override suspend fun getSizedCommentsByRecipePaginated(recipeId: Int, page: Int, pageSize: Int) {
        _functionGetSizedCommentsOnRecipePaginated.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getSizedCommentsByRecipePaginated(recipeId,page,pageSize)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionGetSizedCommentsOnRecipePaginated.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionGetSizedCommentsOnRecipePaginated.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetSizedCommentsOnRecipePaginated.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    // LiveData for searched recipes
    private val _functionGetCommentsByClientPaginated = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetCommentsByClientPaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetCommentsByClientPaginated


    override suspend fun getCommentsByClientPaginated(clientId: Int, page: Int) {
        _functionGetCommentsByClientPaginated.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getCommentsByClientPaginated(clientId,page)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            _functionGetSizedCommentsOnRecipePaginated.postValue(Event(NetworkResult.Success(
                response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _functionGetCommentsByClientPaginated.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetCommentsByClientPaginated.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }



    // NOT OTIMIZED


}