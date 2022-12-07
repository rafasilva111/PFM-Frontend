package com.example.projectfoodmanager.ui.recipe


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.UiState
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.show
import com.example.projectfoodmanager.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    val TAG: String = "ReceitaDetailFragment"
    lateinit var binding: FragmentRecipeDetailBinding
    val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentRecipeDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener{
//            if (validation()){
//                viewModel.addRecipe(
//                    Recipe_info(
//                        id = 123,
//                        description = binding.noteMsg.text.toString(),
//                        title = "Not implemented yet",
//                        date = ""
//                    )
//                )
//            }
        }
        viewModel.addRecipe.observe(viewLifecycleOwner){state ->
            when(state){
                is UiState.Loading ->{
                    binding.btnProgressAr.show()

                }
                is UiState.Success -> {
                    binding.btnProgressAr.hide()
                    toast("Note has been created successfully")
                }
                is UiState.Failure -> {
                    binding.btnProgressAr.hide()
                    toast(state.error)
                }
            }
        }
    }

    private fun validation(): Boolean{
        var isValid = true
        if ( binding.noteMsg.text.toString().isNullOrEmpty()){
            isValid = false
            toast("Enter message")
        }
        return isValid
    }
}