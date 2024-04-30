package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {




    // general
    val recipes: LiveData<Event<NetworkResult<RecipeList>>>
    val recipesCommentedByUser: LiveData<Event<NetworkResult<RecipeList>>>

    suspend fun getRecipes(page: Int,pageSize: Int,userId:Int,searchString:String,searchTag: String, by:String)
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
    val functionGetCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
    val functionGetSizedCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
    val functionGetCommentsByClientPaginated: LiveData<Event<NetworkResult<CommentList>>>

    val functionPostCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>
    val functionPatchComment: LiveData<Event<NetworkResult<Comment>>>
    val functionDeleteComment: LiveData<Event<NetworkResult<Comment>>>

    suspend fun getCommentsByRecipePaginated(recipeId: Int, page: Int)
    suspend fun getSizedCommentsByRecipePaginated(recipeId: Int,page: Int,pageSize:Int)
    suspend fun getCommentsByClientPaginated(clientId: Int, page: Int)

    suspend fun postComment(recipeId: Int, comment: CommentDTO)
    suspend fun patchComment(commentId: Int,comment: CommentDTO)
    suspend fun deleteComment(commentId: Int)

    val functionPostLikeOnComment: LiveData<Event<NetworkResult<Comment>>>
    val functionDeleteLikeOnComment: LiveData<Event<NetworkResult<Comment>>>

    suspend fun postLikeOnComment(commentId: Int)
    suspend fun deleteLikeOnComment(commentId: Int)






}