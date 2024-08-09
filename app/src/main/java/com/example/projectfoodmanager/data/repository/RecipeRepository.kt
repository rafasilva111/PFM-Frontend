package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult

interface RecipeRepository {




    // general
    val functionGetRecipes: LiveData<Event<NetworkResult<RecipeList>>>
    val functionGetRecipe: LiveData<Event<NetworkResult<Recipe>>>

    suspend fun getRecipes(page: Int,pageSize: Int,userId:Int?,searchString:String,searchTag: String, by:String)
    suspend fun getRecipe(id: Int)

    // like
    val functionGetLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Recipe>>>

    suspend fun getLikedRecipes(page: Int,pageSize: Int,searchString: String,searchTag: String)
    suspend fun addLikeOnRecipe(recipeId: Int)
    suspend fun removeLikeOnRecipe(recipeId: Int)

    // save
    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>
    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Recipe>>>

    suspend fun removeSaveOnRecipe(recipeId: Int)
    suspend fun addSaveOnRecipe(recipeId: Int)

    // comment
    val functionGetComments: LiveData<Event<NetworkResult<CommentList>>>

    val functionGetComment: LiveData<Event<NetworkResult<Comment>>>
    val functionPostComment: LiveData<Event<NetworkResult<Comment>>>
    val functionPatchComment: LiveData<Event<NetworkResult<Comment>>>
    val functionDeleteComment: LiveData<Event<NetworkResult<Comment>>>

    suspend fun getComments(recipeId: Int?,clientId: Int?, page: Int, pageSize:Int)

    suspend fun getComment(commentId: Int)
    suspend fun postComment(recipeId: Int, comment: CommentDTO)
    suspend fun patchComment(commentId: Int,comment: CommentDTO)
    suspend fun deleteComment(commentId: Int)

    val functionPostLikeOnComment: LiveData<Event<NetworkResult<Comment>>>
    val functionDeleteLikeOnComment: LiveData<Event<NetworkResult<Comment>>>

    suspend fun postLikeOnComment(commentId: Int)
    suspend fun deleteLikeOnComment(commentId: Int)






}