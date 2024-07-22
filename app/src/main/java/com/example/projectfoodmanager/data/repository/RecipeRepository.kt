package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.recipe.RecipeList
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {




    // general
    val recipes: LiveData<Event<NetworkResult<RecipeList>>>
    val recipesCommentedByUser: LiveData<Event<NetworkResult<RecipeList>>>

    suspend fun getRecipes(page: Int,pageSize: Int,userId:Int?,searchString:String,searchTag: String, by:String)
    suspend fun getRecipesCommentedByUser(page: Int, clientId: Int, searchString:String?)

    // like
    val userLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>

    suspend fun getUserLikedRecipes()
    suspend fun addLikeOnRecipe(recipeId: Int)
    suspend fun removeLikeOnRecipe(recipeId: Int)

    // save
    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>

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