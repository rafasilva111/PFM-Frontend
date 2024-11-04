package com.example.projectfoodmanager.presentation.recipe.details.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentNutritionTabBinding
import com.example.projectfoodmanager.util.Helper.Companion.formatPercent

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

                binding.energiaDoseTV.text = "${formatNumber(recipe.nutritionInformation.energia.toString())} Kcal"
                recipe.nutritionInformation.energia_perc?.let {
                    binding.energiaPB.progress = it.toDouble().toInt()
                    binding.energiaPercTV.text = formatPercent(it.toDouble())
                }


                binding.fibraDoseTV.text = "${formatNumber(recipe.nutritionInformation.fibra.toString())} g"
                recipe.nutritionInformation.fibra_perc?.let {
                    binding.fibraPB.progress = it.toDouble().toInt()
                    binding.fibraPercTV.text = formatPercent(it.toDouble())
                }

                binding.proteinaDoseTV.text = "${formatNumber(recipe.nutritionInformation.proteina.toString())} g"
                //binding.proteinaPercTV.text = recipe.nutrition_informations.proteina_perc
                //binding.proteinaPB.progress = recipe.nutrition_informations.proteina_perc.dropLast(1).toInt()


                binding.gorduraDoseTV.text = "${formatNumber(recipe.nutritionInformation.gordura.toString())} g"
                recipe.nutritionInformation.gordura_perc?.let {
                    binding.gorduraPB.progress = it.toDouble().toInt()
                    binding.gorduraPercTV.text = formatPercent(it.toDouble())
                }

                binding.gorduraSaturadosDoseTV.text = "${formatNumber(recipe.nutritionInformation.gordura_saturada.toString())} g"
                recipe.nutritionInformation.gordura_saturada_perc?.let {
                    binding.gorduraSaturadosPB.progress = it.toDouble().toInt()
                    binding.gorduraSaturadosPercTV.text = formatPercent(it.toDouble())
                }

                binding.hidratosCarbonoDoseTV.text = "${formatNumber(recipe.nutritionInformation.hidratos_carbonos.toString())} g"
                //binding.hidratosCarbonoPercTV.text = recipe.nutrition_informations.hidratos_carbonos_perc
                //binding.hidratosCarbonoPB.progress = recipe.nutrition_informations.hidratos_carbonos_perc.dropLast(1).toInt()

                binding.hidratosAcucaresDoseTV.text = "${formatNumber(recipe.nutritionInformation.hidratos_carbonos_acucares.toString())} g"
               // binding.hidratosAcucaresPercTV.text = recipe.nutritionInformation.hidratos_carbonos_acucares_perc
                recipe.nutritionInformation.hidratos_carbonos_acucares_perc?.let {
                    binding.hidratosAcucaresPB.progress = it.toDouble().toInt()
                    binding.hidratosAcucaresPercTV.text = formatPercent(it.toDouble())
                }
            }
        }


    }


    fun formatNumber(value: String): Any {
        return try {
            // Converte a String para Double
            val numericValue = value.toDouble()

            // Verifica se o valor é inteiro ou decimal
            if (numericValue % 1.0 == 0.0) {
                numericValue.toInt()  // Retorna um inteiro se for inteiro
            } else {
                numericValue  // Retorna o valor como double se for decimal
            }
        } catch (e: NumberFormatException) {
            "Invalid number"  // Retorna uma mensagem de erro se a conversão falhar
        }
    }
}