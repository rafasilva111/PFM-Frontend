package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
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
            recipe.nutrition_information?.let {
            binding.energiaDoseTV.text = recipe.nutrition_information.energia
            binding.energiaPercTV.text = recipe.nutrition_information.energia_perc
            binding.energiaPB.progress = recipe.nutrition_information.energia_perc.dropLast(1).toInt()

            binding.fibraDoseTV.text = recipe.nutrition_information.fibra
            binding.fibraPercTV.text = recipe.nutrition_information.fibra_perc
            binding.fibraPB.progress = recipe.nutrition_information.fibra_perc.dropLast(1).toInt()

            binding.proteinaDoseTV.text = recipe.nutrition_information.proteina
            //binding.proteinaPercTV.text = recipe.nutrition_informations.proteina_perc
            //binding.proteinaPB.progress = recipe.nutrition_informations.proteina_perc.dropLast(1).toInt()

            binding.gorduraDoseTV.text = recipe.nutrition_information.gordura
            binding.gorduraPercTV.text = recipe.nutrition_information.gordura_perc
            binding.gorduraPB.progress = recipe.nutrition_information.gordura_perc.dropLast(1).toInt()

            binding.gorduraSaturadosDoseTV.text = recipe.nutrition_information.gordura_saturada
            binding.gorduraSaturadosPercTV.text = recipe.nutrition_information.gordura_saturada_perc
            binding.gorduraSaturadosPB.progress = recipe.nutrition_information.gordura_saturada_perc.dropLast(1).toInt()

            binding.hidratosCarbonoDoseTV.text = recipe.nutrition_information.hidratos_carbonos
            //binding.hidratosCarbonoPercTV.text = recipe.nutrition_informations.hidratos_carbonos_perc
            //binding.hidratosCarbonoPB.progress = recipe.nutrition_informations.hidratos_carbonos_perc.dropLast(1).toInt()

            binding.hidratosAcucaresDoseTV.text = recipe.nutrition_information.hidratos_carbonos_acucares
            binding.hidratosAcucaresPercTV.text = recipe.nutrition_information.hidratos_carbonos_acucares_perc
            binding.hidratosAcucaresPB.progress = recipe.nutrition_information.hidratos_carbonos_acucares_perc.dropLast(1).toInt()
            }
        }


    }
}