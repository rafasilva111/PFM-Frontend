package com.example.projectfoodmanager.presentation.calendar

import android.view.View
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener

class RecipeListingAdapter(
    val onItemClicked: (Int, CalenderEntry) -> Unit,
    val onDoneClicked: (Boolean, CalenderEntry) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<RecipeSimplified, ItemRecipeLayoutBinding>(
    ItemRecipeLayoutBinding::inflate
) {


    override fun bind(binding: ItemRecipeLayoutBinding, item: RecipeSimplified, position: Int) {


        /**
         * Loading Images
         */

        // Load Recipe img
        loadRecipeImage(binding.imageView, item.imgSource){
            imageLoadingListener.onImageLoaded()
        }

        // Load Author img
        loadUserImage(binding.imgAuthorIV, item.createdBy.imgSource){
            imageLoadingListener.onImageLoaded()
        }


    }

}
