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



            binding.numberCategoriasTV.text = mutList.size.toString() + " Categorias"
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

                        //TODO: é para ser visto melhor
                       /* RecipeListingFragmentFilters.LANCHE -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_lanche, null)
                        RecipeListingFragmentFilters.PEQUENO_ALMOCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_peq_almoco, null)
                        RecipeListingFragmentFilters.JANTAR -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_jantar, null)
                        RecipeListingFragmentFilters.ALMOCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_almoco, null)
                        RecipeListingFragmentFilters.PETISCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_petiscos, null)*/
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
//            *//*     chip.text = item.toString()
//            chip.isCloseIconVisible = true
//            chip.setBackgroundColor(resources.getColor(R.color.red))
//            chip.setChipIconResource(R.drawable.ic_like_red)
//            chip.setOnCloseIconClickListener{
//                binding.CHTags.addView(chip)
//            }*//*
//
//        }
//
//
//        //List_Ingredients
//        binding.LVIngridientsInfo.isClickable = false
//        val itemsAdapterIngrtedients: IngridientsListingAdapter? =
//            this.context?.let { IngridientsListingAdapter(it,recipe.ingredients) }
//        binding.LVIngridientsInfo.adapter = itemsAdapterIngrtedients
//        setListViewHeightBasedOnChildren(binding.LVIngridientsInfo)
//
//        binding.LLContIngredients.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//        binding.CVTitleIngredients.setOnClickListener {
//
//            val state = if(binding.LVIngridientsInfo.visibility== View.GONE) View.VISIBLE else View.GONE
//
//
//            TransitionManager.beginDelayedTransition(binding.LLContIngredients, AutoTransition())
//            binding.LVIngridientsInfo.visibility= state
//
//
//
//            if(state==View.VISIBLE){
//                binding.IVArrowIngrid.animate().rotationBy(90F).setDuration(5).start()
//                binding.SRLDetails.fullScroll(View.FOCUS_DOWN)
//            }else{
//                binding.IVArrowIngrid.animate().rotationBy(-90F).setDuration(5).start()
//
//            }
//
//        }
//
//
//
//        //List_Preparation
//        binding.LVPreparationInfo.isClickable = false
//        val itemsAdapterPreparation: PreparationListingAdapter? =
//            this.context?.let { PreparationListingAdapter(it,recipe.preparation) }
//        binding.LVPreparationInfo.adapter = itemsAdapterPreparation
//
//        setListViewHeightBasedOnChildren(binding.LVPreparationInfo)
//
//        binding.LLContPreparation.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
//
//        binding.CVTitlePreparation.setOnClickListener{
//            val state = if(binding.LVPreparationInfo.visibility== View.GONE) View.VISIBLE else View.GONE
//
//
//            TransitionManager.beginDelayedTransition(binding.LLContPreparation, AutoTransition())
//            binding.LVPreparationInfo.visibility= state
//
//
//
//            if(state==View.VISIBLE){
//                binding.IVArrowPrep.animate().rotationBy(90F).setDuration(5).start()
//                //   binding.SRLDetails.fullScroll(View.AUTOFILL_TYPE_LIST)
//            }else{
//                binding.IVArrowPrep.animate().rotationBy(-90F).setDuration(5).start()
//            }
//        }
            }

            binding.numberIngridientsTV.text = recipe.ingredients?.size.toString() + " Ingredientes"

            recipeIngredientsAdapter.updateList(recipe.ingredients)

            binding.numberPreparationTV.text = recipe.preparation.size.toString() + " Passos"

            recipePreparationAdapter.updateList(recipe.preparation)


        }

    }
    override fun onResume() {
        super.onResume()

        // Update UI elements for status bar and navigation bar
        Helper.updateSystemBarsAppearance(requireActivity(), requireContext())
    }

}