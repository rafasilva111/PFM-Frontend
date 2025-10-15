package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentNutritionTabBinding
import com.example.projectfoodmanager.util.Helper.Companion.enableEdgeToEdge

class NutritionTabFragment(recipe: Recipe) : Fragment() {

    lateinit var binding: FragmentNutritionTabBinding
    var objRecipe: Recipe? = recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return if (this::binding.isInitialized) {
            val layoutParams = binding.recipeTabParentConstraint.layoutParams
            layoutParams.height = 200 // Hardcoded value for height
            binding.recipeTabParentConstraint.layoutParams = layoutParams
            binding.root
        } else {

            // Inflate the layout for this fragment
            binding = FragmentNutritionTabBinding.inflate(layoutInflater)

            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        enableEdgeToEdge(requireActivity(),requireView())
        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    private fun updateUI() {


        val nutrition = objRecipe?.nutritionInformation ?: return

        // Energy
        binding.energyDoseTV.text = getString(R.string.COMMON_NUTRITION_KCAL, nutrition.energyKcal)
        binding.energyPercTV.text = getString(R.string.COMMON_NUTRITION_PERC, nutrition.energyPerc)
        binding.energyPB.progress = nutrition.energyPerc.toInt()

        // Fat
        binding.fatDoseTV.text = getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.fatG)
        binding.fatPercTV.text = getString(R.string.COMMON_NUTRITION_PERC, nutrition.fatPerc)
        binding.fatPB.progress = nutrition.fatPerc.toInt()

        // Saturated Fat
        binding.fatSaturatedDoseTV.text =
            getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.saturatesG)
        binding.fatSaturatedPercTV.text =
            getString(R.string.COMMON_NUTRITION_PERC, nutrition.saturatesPerc)
        binding.fatSaturatedPB.progress = nutrition.saturatesPerc.toInt()

        // Carbohydrates
        binding.carbohydrateDoseTV.text =
            getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.carbohydratesG)
        binding.carbohydratePercTV.text =
            getString(R.string.COMMON_NUTRITION_PERC, nutrition.carbohydratesPerc)
        binding.carbohydratePB.progress = nutrition.carbohydratesPerc.toInt()

        // Sugars
        binding.sugarsDoseTV.text =
            getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.sugarsG)
        binding.sugarsPercTV.text =
            getString(R.string.COMMON_NUTRITION_PERC, nutrition.sugarsPerc)
        binding.sugarsPB.progress = nutrition.sugarsPerc.toInt()

        // Protein
        binding.proteinDoseTV.text = getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.proteinG)
        binding.proteinPercTV.text =
            getString(R.string.COMMON_NUTRITION_PERC, nutrition.proteinPerc)
        binding.proteinPB.progress = nutrition.proteinPerc.toInt()

        // Salt
        binding.salDoseTV.text = getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.saltG)
        binding.salPercTV.text = getString(R.string.COMMON_NUTRITION_PERC, nutrition.saltPerc)
        binding.salPB.progress = nutrition.saltPerc.toInt()

        // Fiber
        binding.fiberDoseTV.text = getString(R.string.COMMON_NUTRITION_GRAMS, nutrition.fiberG)
    }
}