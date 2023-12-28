package com.example.projectfoodmanager.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList

import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor (
    val repository: RecipeRepository
): ViewModel() {

    /**
     * Recipes
     */

    val recipesResponseLiveData: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.recipes

    fun getRecipes(page: Int = 1,pageSize: Int = 10,userId: Int =-1,searchString: String = "", searchTag: String = "", by:String= ""){

        viewModelScope.launch {
            repository.getRecipes(page,pageSize,userId,searchString,searchTag,by)
        }

    }

    val recipesCommentedByUserPaginated: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.recipesCommentedByUser

    val recipesCommentedByUserSearchPaginated: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.recipesCommentedByUser

    fun getRecipesCommentedByUserPaginated(page: Int = 1, clientId: Int, searchString: String? = null){
        viewModelScope.launch {
            repository.getRecipesCommentedByUser(page,clientId,searchString)
        }
    }

    /**
     * Like Function
     */

    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionLikeOnRecipe

    val userLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.userLikedRecipes

    fun getUserLikedRecipes() {
        viewModelScope.launch {
            repository.getUserLikedRecipes()
        }
    }

    fun addLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addLikeOnRecipe(recipeId)
        }
    }

    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionRemoveLikeOnRecipe

    fun removeLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeLikeOnRecipe(recipeId)
        }
    }

    /**
     * Save Function
     */

    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionAddSaveOnRecipe

    fun addSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addSaveOnRecipe(recipeId)
        }
    }

    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionRemoveSaveOnRecipe

    fun removeSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeSaveOnRecipe(recipeId)
        }
    }

    /**
     * Comments Function
     */

    val functionGetCommentsByClientPaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetCommentsOnRecipePaginated

    fun getCommentsByClientPaginated(clientId: Int, page: Int=1) {
        viewModelScope.launch {
            repository.getCommentsByClientPaginated(clientId,page)
        }
    }

    val functionGetCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetCommentsOnRecipePaginated

    fun getCommentsByRecipePaginated(recipeId: Int, page: Int=1) {
        viewModelScope.launch {
            repository.getCommentsByRecipePaginated(recipeId,page)
        }
    }

    val functionGetSizedCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetSizedCommentsOnRecipePaginated

    fun getSizedCommentsByRecipePaginated(recipeId: Int,page: Int=1,pageSize:Int=5) {
        viewModelScope.launch {
            repository.getSizedCommentsByRecipePaginated(recipeId,page,pageSize)
        }
    }

    val functionPostCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionPostCommentOnRecipe

    fun postCommentOnRecipe(recipeId: Int, comment: CreateCommentRequest) {
        viewModelScope.launch {
            repository.createCommentOnRecipe(recipeId,comment)
        }
    }




}