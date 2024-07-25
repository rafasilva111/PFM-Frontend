package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
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
            recipe.nutritionInformation.energia_perc?.let {
                binding.energiaPB.progress = it.toDouble().toInt()
            }


            binding.fibraDoseTV.text = recipe.nutritionInformation.fibra
            binding.fibraPercTV.text = recipe.nutritionInformation.fibra_perc
            recipe.nutritionInformation.fibra_perc?.let {
                binding.fibraPB.progress = it.toDouble().toInt()
            }

            binding.proteinaDoseTV.text = recipe.nutritionInformation.proteina
            //binding.proteinaPercTV.text = recipe.nutrition_informations.proteina_perc
            //binding.proteinaPB.progress = recipe.nutrition_informations.proteina_perc.dropLast(1).toInt()


            binding.gorduraDoseTV.text = recipe.nutritionInformation.gordura
            binding.gorduraPercTV.text = recipe.nutritionInformation.gordura_perc
            recipe.nutritionInformation.gordura_perc?.let {
                binding.gorduraPB.progress = it.toDouble().toInt()
            }

            binding.gorduraSaturadosDoseTV.text = recipe.nutritionInformation.gordura_saturada
            binding.gorduraSaturadosPercTV.text = recipe.nutritionInformation.gordura_saturada_perc
            recipe.nutritionInformation.gordura_saturada_perc?.let {
                binding.gorduraSaturadosPB.progress = it.toDouble().toInt()
            }

            binding.hidratosCarbonoDoseTV.text = recipe.nutritionInformation.hidratos_carbonos
            //binding.hidratosCarbonoPercTV.text = recipe.nutrition_informations.hidratos_carbonos_perc
            //binding.hidratosCarbonoPB.progress = recipe.nutrition_informations.hidratos_carbonos_perc.dropLast(1).toInt()

            binding.hidratosAcucaresDoseTV.text = recipe.nutritionInformation.hidratos_carbonos_acucares
            binding.hidratosAcucaresPercTV.text = recipe.nutritionInformation.hidratos_carbonos_acucares_perc
            recipe.nutritionInformation.hidratos_carbonos_acucares_perc?.let {
                binding.hidratosAcucaresPB.progress = it.toDouble().toInt()
            }
            }
        }


    }
}