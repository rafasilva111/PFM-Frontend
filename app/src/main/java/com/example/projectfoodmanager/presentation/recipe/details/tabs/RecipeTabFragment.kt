package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Tag
import com.example.projectfoodmanager.databinding.FragmentRecipeTabBinding
import com.example.projectfoodmanager.presentation.recipe.details.IngredientListingAdapter
import com.example.projectfoodmanager.presentation.recipe.details.PreparationListingAdapter
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.RecipeListingFragmentFilters
import com.google.android.material.chip.Chip


class RecipeTabFragment(recipe: Recipe) : Fragment() {

    // binding
    lateinit var binding: FragmentRecipeTabBinding

    // constants
    var objRecipe: Recipe? = recipe

    // adapters
    private val recipePreparationAdapter by lazy {
        PreparationListingAdapter(
            requireContext()
        )
    }

    private val recipeIngredientsAdapter by lazy {
        IngredientListingAdapter(
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized){
            return binding.root
        }else {

            // Inflate the layout for this fragment
            binding = FragmentRecipeTabBinding.inflate(layoutInflater)

            // Inflate adapters
            binding.recipePreparationRV.layoutManager = LinearLayoutManager(activity)
            binding.recipePreparationRV.adapter = recipePreparationAdapter

            binding.recipeIngredientsRV.layoutManager = LinearLayoutManager(activity)
            binding.recipeIngredientsRV.adapter = recipeIngredientsAdapter

            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        updateUI()
        Log.d("TAG", "onViewCreated: ")
    }

    private fun updateUI() {

        objRecipe?.let { recipe ->

            binding.descriptionTV.text = recipe.description


            val list: List<Tag> = recipe.tags!!


            // TODO: Obter a lista ordenada da base de dados
            val mutList: MutableList<Tag> = list.sortedBy { it.text.length }.toMutableList()



            binding.numberCategoriesTV.text = if(mutList.size> 1){
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_CATEGORIES, mutList.size)
            } else {
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_CATEGORY)
            }

            for (item: Tag in mutList) {

                val chip = Chip(context)

                chip.apply {
                    text = item.text
                    textSize = 12F
                    chipEndPadding = 0F

                    textStartPadding = 0F
                    textAlignment = View.TEXT_ALIGNMENT_CENTER

                    when (item.text.lowercase()) {
                        RecipeListingFragmentFilters.MEAT -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_carne, null)
                        RecipeListingFragmentFilters.FISH -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_peixe, null)
                        RecipeListingFragmentFilters.SOUP -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sopa, null)
                        RecipeListingFragmentFilters.VEGAN -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_vegeteriana, null)
                        RecipeListingFragmentFilters.FRUIT -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_fruta, null)
                        RecipeListingFragmentFilters.DRINK -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_bebida, null)
                        RecipeListingFragmentFilters.SALAD -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_salada, null)
                        RecipeListingFragmentFilters.PIZZA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_pizza, null)
                        RecipeListingFragmentFilters.DESSERT -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sobremesa, null)
                        RecipeListingFragmentFilters.SANDWICH -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sandes, null)

                        else ->
                            chipBackgroundColor =
                                context.resources.getColorStateList(R.color.grey, null)
                    }

                    isClickable = false
                    isCheckable = false
                    binding.apply {
                        CHTags.addView(chip as View)
                        chip.setOnCloseIconClickListener {
                            CHTags.removeView(chip as View)
                        }
                    }

                }
            }

            binding.numberIngridientsTV.text = if (recipe.ingredients.size > 1){
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_INGREDIENTS, recipe.ingredients.size)
            } else {
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_INGREDIENT)
            }

            recipeIngredientsAdapter.updateList(recipe.ingredients)

            binding.numberPreparationTV.text = if (recipe.preparation.size > 1){
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_PREPARATIONS, recipe.preparation.size)
            } else {
                getString(R.string.FRAGMENT_RECIPE_TAB_NUMBER_PREPARATION)
            }

            recipePreparationAdapter.updateList(recipe.preparation)


        }

    }
    override fun onResume() {
        super.onResume()

        // Update UI elements for status bar and navigation bar
        Helper.updateSystemBarsAppearance(requireActivity(), requireContext())
    }

}