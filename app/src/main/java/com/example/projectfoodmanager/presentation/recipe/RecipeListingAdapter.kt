package com.example.projectfoodmanager.presentation.recipe

import android.view.View
import com.example.projectfoodmanager.R
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
    val onItemClicked: (Int, RecipeSimplified) -> Unit,
    val onLikeClicked: (RecipeSimplified, Boolean) -> Unit,
    val onSaveClicked: (RecipeSimplified, Boolean) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<RecipeSimplified, ItemRecipeLayoutBinding>(
    ItemRecipeLayoutBinding::inflate
) {

    private val TAG: String = "RecipeListingAdapter"

    fun updateItem(item: RecipeSimplified){
        for ((index, recipe) in itemList.withIndex()){
            if (recipe.id == item.id) {
                itemList[index] = item
                notifyItemChanged(index)
                break
            }
        }
    }

    fun updateItem(item: Recipe){
        for ((index, recipe) in itemList.withIndex()){
            if (recipe.id == item.id) {
                itemList[index] = item.toRecipeSimplified()
                notifyItemChanged(index)
                break
            }
        }
    }


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
        /**
         * Details
         */

        //Load Author name
        binding.nameAuthorTV.text = formatNameToNameUpper(item.createdBy.name)

        //Validate that the author is verified
        if (item.createdBy.verified){
            binding.verifyUserIV.visibility=View.VISIBLE
        }else{
            binding.verifyUserIV.visibility=View.INVISIBLE
        }

        binding.dateTV.text = formatServerTimeToDateString(item.createdDate)
        binding.recipeTitleTV.text = item.title
        binding.idTV.text = item.id.toString()

        // string -> localTimeDate
        binding.recipeDescriptionTV.text = item.description
        binding.itemLayout.setOnClickListener {

            onItemClicked.invoke(position, item)
        }
        binding.nLikeTV.text = item.likes.toString()

        if (!item.verified){
            binding.verifyRecipeIV.visibility= View.INVISIBLE
            binding.verifyRecipeTV.visibility= View.INVISIBLE
        }else{
            binding.verifyRecipeIV.visibility= View.VISIBLE
            binding.verifyRecipeTV.visibility= View.VISIBLE
        }


        //--> RATING
        binding.ratingRecipeRB.rating = item.sourceRating.toFloat()
        binding.ratingMedTV.text = item.sourceRating.toString()



         /**
         * Likes Function
         */

        if(item.liked)
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)


        binding.likeIB.setOnClickListener {
            if(item.liked)
                onLikeClicked.invoke(item, false)
            else
                onLikeClicked.invoke(item, true)

        }


        /**
         * Saves Function
         */

        if(item.saved)
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

        binding.favoritesIB.setOnClickListener {
            if(item.saved)
                onSaveClicked.invoke(item, false)
            else
                onSaveClicked.invoke(item, true)

        }

    }

}
