package com.example.projectfoodmanager.presentation.recipe.create.steps

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.Ingredient
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.databinding.FragmentStepIngredientsBinding
import com.example.projectfoodmanager.presentation.recipe.create.steps.util.StepUtil.Companion.createRecipe


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_RECIPE = "RECIPE"



class StepIngredientsFragment : Fragment() {

    private var position: Int = -1
    private var isUpdate: Boolean = false
    private var itemToUpdated: IngredientQuantity? = null

    // binding
    lateinit var binding: FragmentStepIngredientsBinding

    //private var objRecipe: RecipeRequest? = null

    private val adapter by lazy{
        IngredientsAdapter(
            createRecipe!!.ingredients

        ) { pos, item ->
            isUpdate = true
            itemToUpdated = item
            position = pos

            binding.ingredientET.setText(item.ingredient.name)
            binding.quantityET.setText(item.quantity_original)

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (createRecipe == null)
            findNavController().navigateUp()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentStepIngredientsBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUI()

        binding.addIngredientBTN.setOnClickListener {

            //TODO: Ver o com o rafa
            if(isUpdate){
                //itemToUpdated!!.ingredient.name = binding.ingredientET.text.toString()
                //itemToUpdated!!.quantity_original = binding.quantityET.text.toString()
                adapter.updateItem(position, itemToUpdated!!)
                binding.ingredientET.setText("")
                binding.quantityET.setText("")
                binding.ingredientET.clearFocus()
                binding.quantityET.clearFocus()
                isUpdate=false
            }else{
                // todo
                adapter.addItem(IngredientQuantity(1, Ingredient(1,binding.ingredientET.text.toString()),binding.quantityET.text.toString(),0F,"colheres"))
                binding.ingredientET.setText("")
                binding.quantityET.setText("")
                binding.ingredientET.clearFocus()
                binding.quantityET.clearFocus()
            }
        }

        binding.nextStepBTN.setOnClickListener {
            // Create new fragment and transaction
            //val transaction = parentFragmentManager.beginTransaction()
            findNavController().navigate(R.id.action_stepIngredientsFragment_to_stepPreparationFragment)
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
         /*   transaction.replace(com.example.projectfoodmanager.R.id.frameRecipeFL, StepPreparationFragment())
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()*/
        }

        binding.backIB.setOnClickListener {
/*            val fragmentManager: FragmentManager? = fragmentManager
            fragmentManager!!.popBackStack()*/

            findNavController().navigateUp()
        }

    }

    private fun setUI() {
        val manager = LinearLayoutManager(activity)
        binding.ingredientsRV.layoutManager = manager
        binding.ingredientsRV.adapter = adapter

    }


    override fun onResume() {
        adapter.updateList(createRecipe!!.ingredients.toMutableList())

        super.onResume()
    }

    override fun onPause() {

        //Send ingredientsList to previous Fragment
        if( adapter.getItems().isNotEmpty())
            createRecipe!!.ingredients = adapter.getItems()

        super.onPause()
    }

    /*companion object {
        *//**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StepIngredientsFragment.
         *//*
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StepIngredientsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}