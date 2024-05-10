package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentNutritionTabBinding

class NutritionTabFragment(recipe: Recipe) : Fragment() {

    lateinit var binding: FragmentNutritionTabBinding
    var objRecipe: Recipe? = recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (this::binding.isInitialized){
            val layoutParams = binding.recipeTabParentConstraint.layoutParams
            layoutParams.height = 200 // Hardcoded value for height
            binding.recipeTabParentConstraint.layoutParams = layoutParams
            binding.root
        }else {

            // Inflate the layout for this fragment
            binding = FragmentNutritionTabBinding.inflate(layoutInflater)

            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    private fun updateUI() {

        //Nutrition
        objRecipe?.let { recipe ->
            recipe.nutritionInformation?.let {
            binding.energiaDoseTV.text = recipe.nutritionInformation.energia
            binding.energiaPercTV.text = recipe.nutritionInformation.energia_perc
            binding.energiaPB.progress = recipe.nutritionInformation.energia_perc.dropLast(1).toInt()

            binding.fibraDoseTV.text = recipe.nutritionInformation.fibra
            binding.fibraPercTV.text = recipe.nutritionInformation.fibra_perc
            binding.fibraPB.progress = recipe.nutritionInformation.fibra_perc.dropLast(1).toInt()

            binding.proteinaDoseTV.text = recipe.nutritionInformation.proteina
            //binding.proteinaPercTV.text = recipe.nutrition_informations.proteina_perc
            //binding.proteinaPB.progress = recipe.nutrition_informations.proteina_perc.dropLast(1).toInt()

            binding.gorduraDoseTV.text = recipe.nutritionInformation.gordura
            binding.gorduraPercTV.text = recipe.nutritionInformation.gordura_perc
            binding.gorduraPB.progress = recipe.nutritionInformation.gordura_perc.dropLast(1).toInt()

            binding.gorduraSaturadosDoseTV.text = recipe.nutritionInformation.gordura_saturada
            binding.gorduraSaturadosPercTV.text = recipe.nutritionInformation.gordura_saturada_perc
            binding.gorduraSaturadosPB.progress = recipe.nutritionInformation.gordura_saturada_perc.dropLast(1).toInt()

            binding.hidratosCarbonoDoseTV.text = recipe.nutritionInformation.hidratos_carbonos
            //binding.hidratosCarbonoPercTV.text = recipe.nutrition_informations.hidratos_carbonos_perc
            //binding.hidratosCarbonoPB.progress = recipe.nutrition_informations.hidratos_carbonos_perc.dropLast(1).toInt()

            binding.hidratosAcucaresDoseTV.text = recipe.nutritionInformation.hidratos_carbonos_acucares
            binding.hidratosAcucaresPercTV.text = recipe.nutritionInformation.hidratos_carbonos_acucares_perc
            binding.hidratosAcucaresPB.progress = recipe.nutritionInformation.hidratos_carbonos_acucares_perc.dropLast(1).toInt()
            }
        }


    }
}