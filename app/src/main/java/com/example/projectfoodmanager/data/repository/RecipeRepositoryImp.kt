package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.recipe.RecipeList
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
    override suspend fun getRecipes(page: Int,pageSize: Int,userId: Int?,searchString:String,searchTag: String, by:String) {


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
     * Comments Section
     */

    private val _functionGetComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionGetComment: LiveData<Event<NetworkResult<Comment>>>
        get() = _functionGetComment

    private val _functionCreateComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionPostComment: LiveData<Event<NetworkResult<Comment>>>
        get() = _functionCreateComment

    private val _functionPatchComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionPatchComment:  LiveData<Event<NetworkResult<Comment>>>
        get() = _functionPatchComment


    private val _functionDeleteComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionDeleteComment:  LiveData<Event<NetworkResult<Comment>>>
        get() = _functionDeleteComment


    override suspend fun getComment(commentId: Int) {
        handleApiResponse(
            _functionGetComment
        ) {
            remoteDataSource.getComment(commentId)
        }
    }

    override suspend fun postComment(recipeId: Int, comment: CommentDTO) {
        handleApiResponse(
            _functionCreateComment
        ) {
            remoteDataSource.createComments(recipeId,comment)
        }
    }

    override suspend fun patchComment(commentId: Int,comment: CommentDTO) {
        handleApiResponse(
            _functionPatchComment
        ) {
            remoteDataSource.patchComment(commentId,comment)
        }
    }

    override suspend fun deleteComment(commentId: Int) {
        handleApiResponse(
            _functionDeleteComment
        ) {
            remoteDataSource.deleteComment(commentId)
        }
    }

    private val _functionPostLikeOnComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionPostLikeOnComment:  LiveData<Event<NetworkResult<Comment>>>
        get() = _functionPostLikeOnComment

    private val _functionDeleteLikeOnComment = MutableLiveData<Event<NetworkResult<Comment>>>()
    override val functionDeleteLikeOnComment:  LiveData<Event<NetworkResult<Comment>>>
        get() = _functionDeleteLikeOnComment

    override suspend fun postLikeOnComment(commentId: Int) {
        handleApiResponse(
            _functionPostLikeOnComment
        ) {
            remoteDataSource.postLikeOnComment(commentId)
        }
    }

    override suspend fun deleteLikeOnComment(commentId: Int) {
        handleApiResponse(
            _functionDeleteLikeOnComment
        ) {
            remoteDataSource.deleteLikeOnComment(commentId)
        }
    }


    private val _functionGetCommentsByRecipe = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetCommentsByRecipe: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetCommentsByRecipe

    private val _functionGetCommentsByClient = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetCommentsByClient: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetCommentsByClient

    override suspend fun getCommentsByRecipe(recipeId: Int, page: Int, pageSize: Int) {
        handleApiResponse(
            _functionGetCommentsByRecipe
        ) {
            remoteDataSource.getCommentsByRecipe(recipeId,page,pageSize)
        }
    }

    override suspend fun getCommentsByClient(clientId: Int, page: Int) {

        handleApiResponse(
            _functionGetCommentsByClient
        ) {
            remoteDataSource.getCommentsByClient(clientId,page)
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

}