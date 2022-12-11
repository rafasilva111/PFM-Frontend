package com.example.projectfoodmanager.ui.recipe


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.ui.auth.AuthViewModel

import com.example.projectfoodmanager.util.UiState
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.show
import com.example.projectfoodmanager.util.toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    val TAG: String = "ReceitaDetailFragment"
    lateinit var binding: FragmentRecipeDetailBinding
    var objRecipe: Recipe? = null
    val viewModel: RecipeViewModel by viewModels()
    val authModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::binding.isInitialized){
            return binding.root
        }else {
            // Inflate the layout for this fragment
            binding = FragmentRecipeDetailBinding.inflate(layoutInflater)
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    private fun updateUI() {

        objRecipe = arguments?.getParcelable("note")

        objRecipe?.let { recipe ->
            binding.TVTitle.text = recipe.title
            binding.TVTime.text = recipe.time
            binding.TVDifficulty.text = recipe.difficulty
            binding.TVPortion.text = recipe.portion
          //  binding.tvRateExt.text = "Classifcação: " + recipe.remote_rating
          //  binding.tvRateInt.text = "not implemented"
            binding.TVDescriptionInfo.text = recipe.desc
            binding.TVIngridientsInfo.text = parse_hash_maps(recipe.ingredients)
           // binding.tvPreparationInfo.text = parse_hash_maps(recipe.preparation)
           // binding.tvSourceCompany.text = recipe.company
            //binding.tvSourceLink.text = recipe.source
           // binding.tvPreparationInfo.text = parse_hash_maps(recipe.preparation)
            val imgRef = Firebase.storage.reference.child(recipe.img)
            imgRef.downloadUrl.addOnSuccessListener {Uri->
                val imageURL = Uri.toString()
                Glide.with(binding.IVRecipe.context).load(imageURL).into(binding.IVRecipe)
            }




        }
    }

    private fun observer() {
        authModel.updateFavoriteList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    //todo
                }
                is UiState.Failure -> {

                    toast(state.error)
                }
                is UiState.Success -> {
                    toast(state.data.second)
                }
            }
        }
    }

    private fun parse_hash_maps(ingredients: HashMap<String, String>): CharSequence? {
        var helper_text: String = ""
        for (item in ingredients.keys){

            helper_text = helper_text + item+": " + ingredients.get(item) + "\n"
        }
        return helper_text
    }


}