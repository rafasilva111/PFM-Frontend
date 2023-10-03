package com.example.projectfoodmanager.presentation.recipe.details

import android.os.Bundle
import android.util.Log
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