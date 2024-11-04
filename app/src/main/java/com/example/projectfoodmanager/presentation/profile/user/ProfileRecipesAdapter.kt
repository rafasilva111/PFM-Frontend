package com.example.projectfoodmanager.presentation.profile.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.databinding.ItemProfileRecipeLayoutBinding
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutBinding
import com.example.projectfoodmanager.databinding.ItemRecipeLayoutNewBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import java.time.LocalDate


class ProfileRecipesAdapter(
    private val onItemClicked: (Int, RecipeSimplified) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<RecipeSimplified, ItemRecipeLayoutNewBinding>(
    ItemRecipeLayoutNewBinding::inflate
) {

    override fun bind(binding: ItemRecipeLayoutNewBinding, item: RecipeSimplified, position: Int) {


        /**
         * Loading Images
         */

        // Load Recipe img
        loadRecipeImage(binding.imageView, item.imgSource){
            imageLoadingListener.onImageLoaded()
        }

        /**
         * Details
         */


        binding.recipeTitleTV.text = item.title

        // string -> localTimeDate
        //binding.recipeDescriptionTV.text = item.description
        binding.itemLayout.setOnClickListener {

            onItemClicked.invoke(position, item)
        }

        if (!item.verified){
            binding.verifyRecipeIV.visibility= View.INVISIBLE
            binding.verifyRecipeTV.visibility= View.INVISIBLE
        }else{
            binding.verifyRecipeIV.visibility= View.VISIBLE
            binding.verifyRecipeTV.visibility= View.VISIBLE
        }


        //binding.ratingRecipeRB.rating = item.sourceRating.toFloat()
        binding.ratingMedTV.text = item.sourceRating


        /**
         * Likes Function
         */

        //Switch when likes more than 1000 to K


        binding.numLikesTV.text = if (item.likes > 0) item.likes.toString() else ""


        if (item.likes >= 1000) {
            // Convert to "K" format and round to one decimal if needed
            String.format("%.1fK", item.likes / 1000.0).replace(".0K", "K")
        } else {
            item.likes.toString()
        }


        binding.timeTV.text = item.time
        binding.difficultyTV.text = item.difficulty
        binding.portionTV.text = item.portion


    }

}