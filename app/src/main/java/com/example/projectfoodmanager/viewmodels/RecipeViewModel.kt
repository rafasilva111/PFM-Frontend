package com.example.projectfoodmanager.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.recipe.RecipeList
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

    fun getRecipes(page: Int = 1,pageSize: Int = 10,userId: Int? = null,searchString: String = "", searchTag: String = "", by:String= ""){

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
     * Comments Section
     */

    /** Special Gets */

    val functionGetCommentsByClient: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetCommentsByClient

    val functionGetCommentsByRecipe: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetCommentsByRecipe

    fun getCommentsByClient(clientId: Int, page: Int=1) {
        viewModelScope.launch {
            repository.getCommentsByClient(clientId,page)
        }
    }

    fun getCommentsByRecipe(recipeId: Int, page: Int=1, pageSize:Int=8) {
        viewModelScope.launch {
            repository.getCommentsByRecipe(recipeId,page,pageSize)
        }
    }

    /** General */

    val functionGetComment: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionGetComment

    val functionPostCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionPostComment

    val functionPatchComment: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionPatchComment

    val functionDeleteComment: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionDeleteComment

    fun getComment(commentId: Int) {
        viewModelScope.launch {
            repository.getComment(commentId)
        }
    }

    fun postCommentOnRecipe(recipeId: Int, comment: CommentDTO) {
        viewModelScope.launch {
            repository.postComment(recipeId,comment)
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    fun patchComment(commentId:Int,comment: CommentDTO) {
        viewModelScope.launch {
            repository.patchComment(commentId,comment)
        }
    }

    /** Comment Like Function */

    val functionPostLikeOnComment: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionPostLikeOnComment

    val functionDeleteLikeOnComment: LiveData<Event<NetworkResult<Comment>>>
        get() = repository.functionDeleteLikeOnComment

    fun postLikeOnComment(commentId: Int) {
        viewModelScope.launch {
            repository.postLikeOnComment(commentId)
        }
    }

    fun deleteLikeOnComment(commentId: Int) {
        viewModelScope.launch {
            repository.deleteLikeOnComment(commentId)
        }
    }


}