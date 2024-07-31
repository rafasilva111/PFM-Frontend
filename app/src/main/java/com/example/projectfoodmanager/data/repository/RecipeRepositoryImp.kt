package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import retrofit2.Response
import javax.inject.Inject

class RecipeRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
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


    private val _functionGetRecipes = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val functionGetRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _functionGetRecipes

    private val _functionGetRecipe= MutableLiveData<Event<NetworkResult<Recipe>>>()
    override val functionGetRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = _functionGetRecipe


    // Function to get paginated and sorted recipes
    override suspend fun getRecipes(page: Int,pageSize: Int,userId: Int?,searchString:String,searchTag: String, by:String) {


        handleApiResponse(
            _functionGetRecipes
        ) {
            remoteDataSource.getRecipes(userId,by,searchString,searchTag, page,pageSize)
        }
    }




    // Function to get paginated and sorted recipes
    override suspend fun getRecipe(id: Int) {


        handleApiResponse(
            _functionGetRecipe
        ) {
            remoteDataSource.getRecipe(id)
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


    private val _functionGetComments = MutableLiveData<Event<NetworkResult<CommentList>>>()
    override val functionGetComments: LiveData<Event<NetworkResult<CommentList>>>
        get() = _functionGetComments


    override suspend fun getComments(recipeId: Int?, clientId: Int?, page: Int, pageSize: Int) {
        handleApiResponse(
            _functionGetComments
        ) {
            remoteDataSource.getComments(recipeId, clientId, page,pageSize)
        }
    }

    /**
     * Like Function
     */

    private val _userLikedRecipesResponseLiveData = MutableLiveData<Event<NetworkResult<RecipeList>>>()
    override val functionGetLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = _userLikedRecipesResponseLiveData


    override suspend fun getLikedRecipes(page: Int,pageSize: Int,searchString: String) {
        _userLikedRecipesResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "AuthRepositoryImp - getUserLikedRecipes: Making request.")
        val response =remoteDataSource.getLikedRecipes(page,pageSize,searchString)

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


    private val _functionLikeOnRecipe = MutableLiveData<Event<NetworkResult<Recipe>>>()
    override val functionLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = _functionLikeOnRecipe

    private val _functionRemoveLikeOnRecipe = MutableLiveData<Event<NetworkResult<Recipe>>>()
    override val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = _functionRemoveLikeOnRecipe


    override suspend fun addLikeOnRecipe(recipeId: Int) {

        _functionLikeOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.addLike(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            sharedPreference.addLikeToSavedRecipes(response.body()!!)
            _functionLikeOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
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
        if (response.isSuccessful && response.code() == 200) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.removeLikeFromSavedRecipes(response.body()!!)
            _functionRemoveLikeOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
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


    private val _functionAddSaveOnRecipe = MutableLiveData<Event<NetworkResult<Recipe>>>()
    override val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = _functionAddSaveOnRecipe

    private val _functionRemoveSaveOnRecipe = MutableLiveData<Event<NetworkResult<Recipe>>>()
    override val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = _functionRemoveSaveOnRecipe


    override suspend fun addSaveOnRecipe(recipeId: Int) {
        _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.addSave(recipeId)
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            Log.i(TAG, "handleResponse: response body -> ${response.body()}")
            sharedPreference.addSavedRecipe(response.body()!!)
            _functionAddSaveOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
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
        if (response.isSuccessful && response.code() == 200) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.removeSavedRecipe(response.body()!!)
            _functionRemoveSaveOnRecipe.postValue(Event(NetworkResult.Success(
                response.body()!!
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