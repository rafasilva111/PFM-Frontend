package com.example.projectfoodmanager.presentation.recipe.details


import android.animation.LayoutTransition
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.presentation.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.NutritionTable
import com.example.projectfoodmanager.util.RecipeListingFragmentFilters
import com.google.android.material.chip.Chip
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    val TAG: String = "ReceitaDetailFragment"
    lateinit var binding: FragmentRecipeDetailBinding
    var objRecipe: RecipeResponse? = null
    val viewModel: RecipeViewModel by viewModels()
    val authModel: AuthViewModel by viewModels()
    lateinit var manager: LinearLayoutManager



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

            //var fragmentAdapter = FragmentAdapter(supportFragmentManager)

            binding.TVTitle.text = recipe.title
            binding.TVDate.text = recipe.created_date
            binding.TVTime.text = recipe.time
            binding.TVDifficulty.text = recipe.difficulty
            binding.TVPortion.text = recipe.portion


            // TODO: Falta implementar o rating externo
          //  binding.tvRateExt.text = "Classifcação: " + recipe.remote_rating
          //  binding.tvRateInt.text = "not implemented"
            binding.TVDescriptionInfo.text = recipe.description

            val list : List<String> = recipe.tags


            // TODO: Obter a lista ordenada da base de dados
            val list_orderByLenght : List<String> = list.sortedBy { it.length }
            val mutList : MutableList<String> = list_orderByLenght.toMutableList()
            mutList.removeAt(0)


            //al with = resources.getDimension(R.dimen.text_margin).toInt()

            //layoutParams.setMargins(margin, margin, margin, margin)



            for (item: String in mutList) {

                val chip = Chip(context)

                chip.apply {
                    text = item
                    textSize= 12F
                    chipEndPadding=0F

                    textStartPadding=0F
                    textAlignment=View.TEXT_ALIGNMENT_CENTER

                    when (item.lowercase()) {
                        RecipeListingFragmentFilters.CARNE -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_carne, null)
                        RecipeListingFragmentFilters.PEIXE -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_peixe, null)
                        RecipeListingFragmentFilters.SOPA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sopa, null)
                        RecipeListingFragmentFilters.VEGETARIANA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_vegeteriana, null)
                        RecipeListingFragmentFilters.FRUTA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_fruta, null)
                        RecipeListingFragmentFilters.BEBIDAS -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_bebida, null)
                        RecipeListingFragmentFilters.SALADA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_salada, null)
                        RecipeListingFragmentFilters.PIZZA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_pizza, null)
                        RecipeListingFragmentFilters.SOBREMESA -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sobremesa, null)
                        RecipeListingFragmentFilters.SANDES -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_sandes, null)
                        RecipeListingFragmentFilters.LANCHE -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_lanche, null)
                        RecipeListingFragmentFilters.PEQUENO_ALMOCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_peq_almoco, null)
                        RecipeListingFragmentFilters.JANTAR -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_jantar, null)
                        RecipeListingFragmentFilters.ALMOCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_almoco, null)
                        RecipeListingFragmentFilters.PETISCO -> chipBackgroundColor =
                            context.resources.getColorStateList(R.color.catg_petiscos, null)
                    }

                    isClickable = false
                    isCheckable = false
                    binding.apply {
                        CHTags.addView(chip as View)
                        chip.setOnCloseIconClickListener {
                            CHTags.removeView(chip as View)
                        }
                    }

                }
           /*     chip.text = item.toString()
                chip.isCloseIconVisible = true
                chip.setBackgroundColor(resources.getColor(R.color.red))
                chip.setChipIconResource(R.drawable.ic_like_red)
                chip.setOnCloseIconClickListener{
                    binding.CHTags.addView(chip)
                }*/

            }


            //List_Ingredients
            binding.LVIngridientsInfo.isClickable = false
            val itemsAdapterIngrtedients: IngridientsListingAdapter? =
                this.context?.let { IngridientsListingAdapter(it,recipe.ingredients) }
            binding.LVIngridientsInfo.adapter = itemsAdapterIngrtedients
            setListViewHeightBasedOnChildren(binding.LVIngridientsInfo)

            binding.LLContIngredients.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            binding.CVTitleIngredients.setOnClickListener {

                val state = if(binding.LVIngridientsInfo.visibility== View.GONE) View.VISIBLE else View.GONE


                TransitionManager.beginDelayedTransition(binding.LLContIngredients, AutoTransition())
                binding.LVIngridientsInfo.visibility= state



                if(state==View.VISIBLE){
                    binding.IVArrowIngrid.animate().rotationBy(90F).setDuration(5).start()
                    binding.SRLDetails.fullScroll(View.FOCUS_DOWN)
                }else{
                    binding.IVArrowIngrid.animate().rotationBy(-90F).setDuration(5).start()

                }

            }



            //List_Preparation
            binding.LVPreparationInfo.isClickable = false
            val itemsAdapterPreparation: PreparationListingAdapter? =
                this.context?.let { PreparationListingAdapter(it,recipe.preparation) }
            binding.LVPreparationInfo.adapter = itemsAdapterPreparation

           setListViewHeightBasedOnChildren(binding.LVPreparationInfo)

            binding.LLContPreparation.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

            binding.CVTitlePreparation.setOnClickListener{
                val state = if(binding.LVPreparationInfo.visibility== View.GONE) View.VISIBLE else View.GONE


                TransitionManager.beginDelayedTransition(binding.LLContPreparation, AutoTransition())
                binding.LVPreparationInfo.visibility= state



                if(state==View.VISIBLE){
                    binding.IVArrowPrep.animate().rotationBy(90F).setDuration(5).start()
                    //   binding.SRLDetails.fullScroll(View.AUTOFILL_TYPE_LIST)
                }else{
                    binding.IVArrowPrep.animate().rotationBy(-90F).setDuration(5).start()
                }
            }

            //Nutrition

            if(recipe.nutrition_informations!=null){

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

            }



            // TODO: Inserir imagem do autor da receita
            binding.TVAutor.text=recipe.company
            binding.IVSource.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.source_link))
                startActivity(browserIntent)
            }
            binding.TVRef.text = "Ref: " + recipe.id

            val imgRef = Firebase.storage.reference.child(recipe.img_source)
            imgRef.downloadUrl.addOnSuccessListener {Uri->
                val imageURL = Uri.toString()
                Glide.with(binding.IVRecipe.context).load(imageURL).into(binding.IVRecipe)
            }


            // TODO: Falta registar o numero de comentarios
            // TODO: Falta registar o numero de gostos
            binding.CVComments.setOnClickListener {
                findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment)
            }
            
            //like function

            /*authModel.getUserSession_old { user ->
                if (user != null) {
                    if ( user.liked_recipes.indexOf(recipe)!=-1){
                        binding.likeIB.setImageResource(R.drawable.ic_like_red)
                    }
                }
            }*/


            /*binding.likeIB.setOnClickListener {
                authModel.getUserSession_old { user ->
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

            authModel.getUserSession_old { user ->
                if (user != null) {
                    if ( user.favorite_recipes.indexOf(recipe)!=-1){
                        binding.favoritesIB.setImageResource(R.drawable.ic_favorito_white)
                    }
                }
            }

            binding.favoritesIB.setOnClickListener {
                authModel.getUserSession_old { user ->
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
            }*/

            binding.backIB.setOnClickListener {
                findNavController().navigateUp()
            }


        }
    }

  /*  private fun observer() {
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
    }*/



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