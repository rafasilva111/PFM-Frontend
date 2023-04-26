package com.example.projectfoodmanager.presentation.recipe.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import com.example.projectfoodmanager.databinding.FragmentNutritionTabBinding
import com.example.projectfoodmanager.databinding.FragmentRecipeTabBinding

class NutritionTabFragment(recipe: RecipeResponse) : Fragment() {

    lateinit var binding: FragmentNutritionTabBinding
    var objRecipe: RecipeResponse? = recipe

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::binding.isInitialized){
            return binding.root
        }else {

            // Inflate the layout for this fragment
            binding = FragmentNutritionTabBinding.inflate(layoutInflater)

            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        updateUI()
        Log.d("TAG", "onViewCreated: ")
    }

    private fun updateUI() {

        //Nutrition

        /*if(recipe.nutrition_informations!=null){

            /*//-->Resume nutrition
            binding.TVRDoseEnergia.text=recipe.nutrition_informations.get(NutritionTable.ENERGIA)
            binding.TVRPercEnergia.text=recipe.nutrition_informations.get(NutritionTable.ENERGIA_PERC)
            binding.TVRDoseGordura.text=recipe.nutrition_informations.get(NutritionTable.GORDURA)
            binding.TVRPercGordura.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_PERC)
            binding.TVRDoseGordSat.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_SAT)
            binding.TVRPercGordSat.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_SAT_PERC)

            //-->Table_Nutrition
            binding.TVDoseEnergia.text=recipe.nutrition_informations.get(NutritionTable.ENERGIA)
            binding.TVPercEnergia.text=recipe.nutrition_informations.get(NutritionTable.ENERGIA_PERC)
            binding.TVDoseGordura.text=recipe.nutrition_informations.get(NutritionTable.GORDURA)
            binding.TVPercGordura.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_PERC)
            binding.TVDoseGordSat.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_SAT)
            binding.TVPercGordSat.text=recipe.nutrition_informations.get(NutritionTable.GORDURA_SAT_PERC)
            binding.TVDoseHCarbono.text=recipe.nutrition_informations.get(NutritionTable.HIDRATOS_CARBONO)
            binding.TVPercHCarbono.text=recipe.nutrition_informations.get(NutritionTable.HIDRATOS_CARBONO_PERC)
            binding.TVDoseHCAcucar.text=recipe.nutrition_table.get(NutritionTable.HIDRATOS_CARBONO_ACUCARES)
            binding.TVPercHCAcucar.text=recipe.nutrition_table.get(NutritionTable.HIDRATOS_CARBONO_ACUCARES_PERC)
            binding.TVDoseFibra.text=recipe.nutrition_table.get(NutritionTable.FIBRA)
            binding.TVPercFibra.text=recipe.nutrition_table.get(NutritionTable.FIBRA_PERC)
            binding.TVDoseProteina.text=recipe.nutrition_table.get(NutritionTable.PROTEINA)
            binding.TVPercProteina.text=recipe.nutrition_table.get(NutritionTable.PROTEINA_PERC)*/

            binding.LLContNutrition.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

            binding.CVTitleNutrition.setOnClickListener{
                val state = if(binding.LLContNutrition.visibility== View.GONE) View.VISIBLE else View.GONE


                TransitionManager.beginDelayedTransition(binding.LLContNutrition, AutoTransition())
                binding.LLContNutrition.visibility= state



                if(state==View.VISIBLE){
                    binding.IVArrowNutri.animate().rotationBy(90F).setDuration(5).start()
                    //   binding.SRLDetails.fullScroll(View.AUTOFILL_TYPE_LIST)
                }else{
                    binding.IVArrowNutri.animate().rotationBy(-90F).setDuration(5).start()
                }
            }

        }*/
    }
}