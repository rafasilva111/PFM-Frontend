package com.example.projectfoodmanager.ui.recipe


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.example.projectfoodmanager.util.UiState
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
            binding.TVDate.text = recipe.date
            binding.TVTime.text = recipe.time
            binding.TVDifficulty.text = recipe.difficulty
            binding.TVPortion.text = recipe.portion
          //  binding.tvRateExt.text = "Classifcação: " + recipe.remote_rating
          //  binding.tvRateInt.text = "not implemented"
            binding.TVDescriptionInfo.text = recipe.desc
            binding.LVIngridientsInfo.isClickable = false

            val itemsAdapterIngrtedients: IngridientsListingAdapter? =
                this.context?.let { IngridientsListingAdapter(it,parse_hash_maps(recipe.ingredients)) }
            binding.LVIngridientsInfo.adapter = itemsAdapterIngrtedients

            setListViewHeightBasedOnChildren(binding.LVIngridientsInfo)

            binding.LVPreparationInfo.isClickable = false
            val itemsAdapterPreparation: PreparationListingAdapter? =
                this.context?.let { PreparationListingAdapter(it,recipe.preparation) }
            binding.LVPreparationInfo.adapter = itemsAdapterPreparation

           setListViewHeightBasedOnChildren(binding.LVPreparationInfo)

            //binding.TVSource.text=recipe.company
            //binding.TVLink.text=recipe.source
            Log.d(TAG, "=> remote_rating: "+ recipe.remote_rating)
            Log.d(TAG, "=> app_rating: "+ recipe.app_rating)


            //binding.TVRef.text = "Ref: " + recipe.id

            val imgRef = Firebase.storage.reference.child(recipe.img)
            imgRef.downloadUrl.addOnSuccessListener {Uri->
                val imageURL = Uri.toString()
                Glide.with(binding.IVRecipe.context).load(imageURL).into(binding.IVRecipe)
            }

            //like function

            authModel.getUserSession { user ->
                if (user != null) {
                    if ( user.liked_recipes.indexOf(recipe)!=-1){
                        binding.likeIB.setImageResource(R.drawable.ic_like_red)
                    }
                }
            }

            binding.likeIB.setOnClickListener {
                authModel.getUserSession { user ->
                    if (user != null){
                        if ( user.liked_recipes.indexOf(recipe)!=-1){
                            authModel.removeLikeOnRecipe(recipe)
                            viewModel.removeLikeOnRecipe(user.id,recipe)
                            binding.likeIB.setImageResource(R.drawable.ic_like_black)
                            toast("Removido o seu gosto da receita...")
                        }
                        else{
                            authModel.addLikeOnRecipe(recipe)
                            viewModel.addLikeOnRecipe(user.id,recipe)
                            binding.likeIB.setImageResource(R.drawable.ic_like_red)
                            toast("Adicionado o seu gosto à receita.")
                        }
                    }
                }
            }

            //favorite function

            authModel.getUserSession { user ->
                if (user != null) {
                    if ( user.favorite_recipes.indexOf(recipe)!=-1){
                        binding.favoritesIB.setImageResource(R.drawable.ic_favorito_white)
                    }
                }
            }

            binding.favoritesIB.setOnClickListener {
                authModel.getUserSession { user ->
                    if (user != null){
                        if ( user.favorite_recipes.indexOf(recipe)!=-1){
                            authModel.removeFavoriteRecipe(recipe)
                            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_black)
                            toast("Receita removida dos favoritos.")
                        }
                        else{
                            authModel.addFavoriteRecipe(recipe)
                            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_white)
                            toast("Receita adicionada aos favoritos.")
                        }
                    }
                }
            }

            binding.backIB.setOnClickListener {
                findNavController().navigateUp()
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



    private fun parse_hash_maps(ingredients: HashMap<String, String>): ArrayList<String> {

        var arrayList = ArrayList<String>()
        for (item in ingredients.keys){

            arrayList.add(item+": " + ingredients.get(item))
        }
        return arrayList
    }




    fun setListViewHeightBasedOnChildren(myListView: ListView?) {
        val adapter: ListAdapter = myListView!!.getAdapter()
        var totalHeight = 0
        for (i in 0 until adapter.getCount()) {
            val item: View = adapter.getView(i, null, myListView)
            item.measure(0, 0)

            totalHeight += item.measuredHeight
        }
        val params: ViewGroup.LayoutParams = myListView.getLayoutParams()
        params.height = totalHeight + myListView.getDividerHeight() * (adapter.getCount() - 1)
        myListView.setLayoutParams(params)
        myListView.requestLayout()
    }

}