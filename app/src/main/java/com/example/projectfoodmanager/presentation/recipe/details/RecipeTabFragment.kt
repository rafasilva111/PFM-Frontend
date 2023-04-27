package com.example.projectfoodmanager.presentation.recipe.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import com.example.projectfoodmanager.databinding.FragmentRecipeTabBinding


class RecipeTabFragment(recipe: RecipeResponse) : Fragment() {
    lateinit var binding: FragmentRecipeTabBinding
    var objRecipe: RecipeResponse? = recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::binding.isInitialized){
            return binding.root
        }else {

            // Inflate the layout for this fragment
            binding = FragmentRecipeTabBinding.inflate(layoutInflater)

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

            val teste = recipe.description
            binding.descriptionTV.text = recipe.description

            val itemsAdapterIngrtedients: IngridientsListingAdapter? =
            this.context?.let { IngridientsListingAdapter(it,recipe.ingredients) }
            binding.LVIngridientsInfo.adapter = itemsAdapterIngrtedients
            //setListViewHeightBasedOnChildren(binding.LVIngridientsInfo)


        }


        //val list : List<String> = recipe.tags


        // TODO: Obter a lista ordenada da base de dados
//        val list_orderByLenght : List<String> = list.sortedBy { it.length }
//        val mutList : MutableList<String> = list_orderByLenght.toMutableList()
//        mutList.removeAt(0)


        //al with = resources.getDimension(R.dimen.text_margin).toInt()

        //layoutParams.setMargins(margin, margin, margin, margin)



//        for (item: String in mutList) {
//
//            val chip = Chip(context)
//
//            chip.apply {
//                text = item
//                textSize= 12F
//                chipEndPadding=0F
//
//                textStartPadding=0F
//                textAlignment=View.TEXT_ALIGNMENT_CENTER
//
//                when (item.lowercase()) {
//                    RecipeListingFragmentFilters.CARNE -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_carne, null)
//                    RecipeListingFragmentFilters.PEIXE -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_peixe, null)
//                    RecipeListingFragmentFilters.SOPA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_sopa, null)
//                    RecipeListingFragmentFilters.VEGETARIANA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_vegeteriana, null)
//                    RecipeListingFragmentFilters.FRUTA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_fruta, null)
//                    RecipeListingFragmentFilters.BEBIDAS -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_bebida, null)
//                    RecipeListingFragmentFilters.SALADA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_salada, null)
//                    RecipeListingFragmentFilters.PIZZA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_pizza, null)
//                    RecipeListingFragmentFilters.SOBREMESA -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_sobremesa, null)
//                    RecipeListingFragmentFilters.SANDES -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_sandes, null)
//                    RecipeListingFragmentFilters.LANCHE -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_lanche, null)
//                    RecipeListingFragmentFilters.PEQUENO_ALMOCO -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_peq_almoco, null)
//                    RecipeListingFragmentFilters.JANTAR -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_jantar, null)
//                    RecipeListingFragmentFilters.ALMOCO -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_almoco, null)
//                    RecipeListingFragmentFilters.PETISCO -> chipBackgroundColor =
//                        context.resources.getColorStateList(R.color.catg_petiscos, null)
//                }
//
//                isClickable = false
//                isCheckable = false
//                binding.apply {
//                    CHTags.addView(chip as View)
//                    chip.setOnCloseIconClickListener {
//                        CHTags.removeView(chip as View)
//                    }
//                }
//
//            }
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
}