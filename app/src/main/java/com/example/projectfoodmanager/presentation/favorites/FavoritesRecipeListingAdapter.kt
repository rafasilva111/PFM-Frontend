package com.example.projectfoodmanager.presentation.favorites

import android.view.View
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener


class FavoritesRecipeListingAdapter(
    val onItemClicked: (Int, RecipeSimplified) -> Unit,
    val onLikeClicked: (RecipeSimplified, Boolean) -> Unit,
    val onSaveClicked: (RecipeSimplified, Boolean) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<RecipeSimplified, ItemRecipeLayoutBinding>(
    ItemRecipeLayoutBinding::inflate
) {

    private val TAG = "CalenderEntryAdapter"

    private var list: MutableList<RecipeSimplified> = arrayListOf()


    fun updateItem(item: Recipe){

        for ((index, recipe) in list.withIndex()){
            if (recipe.id == item.id) {
                list[index] = item.toRecipeSimplified()
                notifyItemChanged(index)
                break
            }
        }

    }

    fun removeItem(item: Recipe){
        for ((index, recipe) in list.withIndex()){
            if (recipe.id == item.id) {
                list.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }

    }

    override fun bind(binding: ItemRecipeLayoutBinding, item: RecipeSimplified, position: Int) {

        /**
         * Loading Images
         */

        // Load Recipe img
        if (item.imgSource.isNotEmpty())
            loadRecipeImage(binding.imageView, item.imgSource){
                if (position == 0)
                    imageLoadingListener.onImageLoaded()
            }
        else
            if (position == 0)
                imageLoadingListener.onImageLoaded()

        // Load Author img
        loadUserImage(binding.imgAuthorIV, item.createdBy.imgSource){
            if (position == 0)
                imageLoadingListener.onImageLoaded()
        }

        /**
         * Details
         */

        //------- AUTOR DA RECIPE -------

        binding.nameAuthorTV.text = item.createdBy.name

        if (!item.createdBy.verified) {
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        //AUTHOR-> IMG
        if (item.createdBy.imgSource.contains("avatar")){
            val avatar= Avatar.getAvatarByName(item.createdBy.imgSource)
            binding.imgAuthorIV.setImageResource(avatar!!.imgId)

        }


        //------- INFOS DA RECIPE -------
        binding.idTV.text = item.id.toString()
        binding.dateTV.text = formatServerTimeToDateString(item.createdDate)
        binding.recipeTitleTV.text = item.title
        binding.recipeDescriptionTV.text = item.description
        binding.itemLayout.setOnClickListener { onItemClicked.invoke(position, item) }
        binding.nLikeTV.text = item.likes.toString()



        //------- RECEITA VERIFICADA OU NÃO -------
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

        if (item.liked)
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
