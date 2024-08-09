package com.example.projectfoodmanager.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.PaginationNumber
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

    val functionGetRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.functionGetRecipes

    fun getRecipes(page: Int = 1, pageSize: Int = PaginationNumber.DEFAULT, userId: Int? = null, searchString: String = "", searchTag: String = "", by:String= ""){

        viewModelScope.launch {
            repository.getRecipes(page,pageSize,userId,searchString,searchTag,by)
        }

    }

    val functionGetRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = repository.functionGetRecipe

    fun getRecipe(id:Int){

        viewModelScope.launch {
            repository.getRecipe(id)
        }

    }



    /**
     * Like Function
     */


    val functionGetLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
        get() = repository.functionGetLikedRecipes

    fun getLikedRecipes(page: Int = 1,pageSize: Int = 10, searchString: String = "", searchTag: String = "") {
        viewModelScope.launch {
            repository.getLikedRecipes(page,pageSize,searchString,searchTag)
        }
    }

    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = repository.functionLikeOnRecipe

    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = repository.functionRemoveLikeOnRecipe


    fun addLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addLikeOnRecipe(recipeId)
        }
    }



    fun removeLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeLikeOnRecipe(recipeId)
        }
    }

    /**
     * Save Function
     */

    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = repository.functionAddSaveOnRecipe



    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
        get() = repository.functionRemoveSaveOnRecipe

    fun addSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addSaveOnRecipe(recipeId)
        }
    }

    fun removeSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeSaveOnRecipe(recipeId)
        }
    }

    /**
     * Comments Section
     */

    /** Special Gets */

    val functionGetComments: LiveData<Event<NetworkResult<CommentList>>>
        get() = repository.functionGetComments


    fun getCommentsByRecipe(recipeId: Int? = null,clientId: Int? = null, page: Int=1, pageSize:Int=8) {
        viewModelScope.launch {
            repository.getComments(recipeId, clientId,page,pageSize)
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