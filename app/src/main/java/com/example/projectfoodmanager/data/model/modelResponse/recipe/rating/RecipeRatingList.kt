package com.example.projectfoodmanager.data.model.modelResponse.recipe.rating

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeRatingList(
    val _metadata: Metadata,
    val result: MutableList<RecipeRating>
): Parcelable