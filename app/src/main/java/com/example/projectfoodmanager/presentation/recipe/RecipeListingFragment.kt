package com.example.projectfoodmanager.presentation.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {

    // constantes (cuidado com esta merda)


    private var recipeList: MutableList<Recipe> = mutableListOf()

    private var currentPage:Int = 1
    private var nextPage:Boolean = true

    private var stringToSearch: String? = null
    private var newSearch: Boolean = false
    // this needs to happen otherwise we will have a spam of toast
    private var noMoreRecipesMessagePresented = false

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var isFirstTimeCall = true
    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var searchMode: Boolean = false
    private var refreshPage: Int = 0

    private val TAG: String = "RecipeListingFragment"
    lateinit var binding: FragmentRecipeListingBinding
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->
                // use pos to reset current page to pos page, so it will refresh the pos page
                refreshPage =  ceil((pos+1).toFloat()/5).toInt()
                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })

                changeVisibilityMenu(false)
            },
            onLikeClicked = {item,like ->
                if(like){
                    recipeViewModel.addLikeOnRecipe(item.id)
                }
                else{
                    recipeViewModel.removeLikeOnRecipe(item.id)
                }

            },
            onSaveClicked = {item,saved ->
                if(saved){
                    recipeViewModel.addSaveOnRecipe(item.id)
                }
                else{
                    recipeViewModel.removeSaveOnRecipe(item.id)
                }

            },
            this.recipeViewModel,
            sharedPreference
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //observer()
        //todo check for internet connection

        var flags: Int = requireActivity().window.decorView.systemUiVisibility

        bindObservers()

        if (this::binding.isInitialized){
            return binding.root
        }else {

            // load profile image

            val userSession: User? = sharedPreference.getUserSession()
            if (userSession != null && userSession.img_source.isNotEmpty()){
                val imgRef = Firebase.storage.reference.child(userSession.img_source)
                imgRef.downloadUrl.addOnSuccessListener { Uri ->
                    val imageURL = Uri.toString()
                    Glide.with(binding.ivProfilePic.context).load(imageURL).into(binding.ivProfilePic)
                }
                    .addOnFailureListener {
                        Glide.with(binding.ivProfilePic.context)
                            .load(R.drawable.good_food_display___nci_visuals_online)
                            .into(binding.ivProfilePic)
                    }
            }


            binding = FragmentRecipeListingBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation=LinearLayoutManager.HORIZONTAL
            manager.reverseLayout=false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)


            setRecyclerViewScrollListener()
            return binding.root
        }
        }




    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // prevent missed calls to api // needs to be reseted on search so it could be a next page

                    if (nextPage){
                        binding.recyclerView.removeOnScrollListener(scrollListener)


                        //val visibleItemCount: Int = manager.childCount
                        val pastVisibleItem: Int =
                            manager.findLastCompletelyVisibleItemPosition()
                        //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                        if ((pastVisibleItem + 1) >= recipeList.size){
                            if (stringToSearch.isNullOrEmpty()) {
                                recipeViewModel.getRecipesPaginated(++currentPage)
                            } else {
                                recipeViewModel.getRecipesByTitleAndTags(stringToSearch!!, ++currentPage)
                            }
                        }
                        //Log.d(TAG, pag_index.toString())
                        //Log.d(TAG, visibleItemCount.toString())
                        Log.d(TAG, pastVisibleItem.toString())
                    }
                    else if (!noMoreRecipesMessagePresented){
                        noMoreRecipesMessagePresented = true
                        toast("Sorry cant find more recipes.")
                    }
                    binding.recyclerView.addOnScrollListener(scrollListener)
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isFirstTimeCall = true
                }

                super.onScrollStateChanged(recyclerView, newState)

            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeVisibilityMenu(true)

        // user data
        val user = sharedPreference.getUserSession()
        if (user!= null){
            binding.tvName.text =  getString(R.string.full_name, user.first_name, user.last_name)
        }


        if (isOnline(view.context)) {
            binding.recyclerView.adapter = adapter
            binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text != null && text != "") {
                        // importante se não não funciona
                        currentPage = 1
                        newSearch = true
                        stringToSearch=text
                        recipeViewModel.getRecipesByTitleAndTags(text, currentPage)
                    } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                    else if (stringToSearch != null && text == ""){
                        stringToSearch=null
                        recipeList = mutableListOf()
                        currentPage = 1
                        recipeViewModel.getRecipesPaginated(currentPage)
                    }
                    else{
                        stringToSearch=null
                    }
                    return true
                }
            })
            if (stringToSearch.isNullOrEmpty())
                if (refreshPage == 0)
                    recipeViewModel.getRecipesPaginated(currentPage)
                else
                    recipeViewModel.getRecipesPaginated(refreshPage)
            else
                if (refreshPage == 0)
                    recipeViewModel.getRecipesByTitleAndTags(stringToSearch!!,currentPage)
                else
                    recipeViewModel.getRecipesByTitleAndTags(stringToSearch!!,refreshPage)



            //nav search toppom

            binding.btnSugestoes.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.btnMelhores.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.btnRecentes.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.btnPersonalizadas.setOnClickListener {
                toast("Em desenvolvimento...")
            }


            //nav search bottom

            binding.IBMeat.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.CARNE
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.CARNE)
            }
            binding.IBFish.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.PEIXE
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.PEIXE)
            }
            binding.IBSoup.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.SOPA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.SOPA)
            }
            binding.IBVegi.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.VEGETARIANA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.VEGETARIANA)
            }
            binding.IBFruit.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.FRUTA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.FRUTA)
            }
            binding.IBDrink.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.BEBIDAS
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.BEBIDAS)
            }
        }
        else{
            binding.offlineTV.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }


    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {
        recipeViewModel.recipeResponseLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                searchMode = false
                when (it) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view

                        if (refreshPage != 0) {
                            binding.progressBar.hide()
                            val lastIndex =
                                if (recipeList.size >= 5) (refreshPage * 5) - 1 else recipeList.size - 1
                            var firstIndex = if (recipeList.size >= 5) lastIndex - 4 else 0

                            recipeList.subList(firstIndex, lastIndex + 1).clear()


                            for (recipe in it.data!!.result) {
                                recipeList.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.updateList(recipeList)

                            //reset control variables
                            refreshPage = 0
                        } else {
                            binding.progressBar.hide()

                            currentPage = it.data!!._metadata.current_page

                            if(it.data.result.isEmpty()){
                                binding.offlineTV.text = "Não existem receitas..."
                                binding.offlineTV.visibility=View.VISIBLE
                            }else{
                                binding.offlineTV.visibility=View.GONE

                            }


                            // check next page to failed missed calls to api
                            nextPage = it.data._metadata.next != null


                            for (recipe in it.data.result) {
                                recipeList.add(recipe)
                            }
                            adapter.updateList(recipeList)
                        }


                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()

                    }
                }
            }
        }

        // Search Function

        recipeViewModel.recipeSearchByTitleAndTagsResponseLiveData.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {


                        // isto é usado para atualizar os likes caso o user vá a detail view

                        if (refreshPage != 0) {
                            binding.progressBar.hide()
                            val lastIndex =
                                if (recipeList.size >= 5) (refreshPage * 5) - 1 else recipeList.size - 1
                            var firstIndex = if (recipeList.size >= 5) lastIndex - 4 else 0

                            recipeList.subList(firstIndex, lastIndex + 1).clear()

                            if (newSearch)
                                recipeList = mutableListOf()

                            for (recipe in it.data!!.result) {
                                recipeList.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.updateList(recipeList)

                            //reset control variables
                            refreshPage = 0
                        } else {
                            binding.progressBar.hide()

                            // numa nova procura resetar a lista de receitas
                            if (newSearch)
                                recipeList = mutableListOf()

                            for (recipe in it.data!!.result) {
                                recipeList.add(recipe)
                            }
                            adapter.updateList(recipeList)
                            newSearch = false

                            // check next page to failed missed calls to api
                            nextPage = it.data._metadata.next != null
                            // safe call for debaunce
                            currentPage = it.data._metadata.current_page
                            // se houver next page soma se não não faz nada
                        }

                        binding.progressBar.hide()

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }


        // Like function


        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()) {
                            if (item.id == it.data) {
                                item.likes++
                                sharedPreference.addLikeToUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item), item)
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()) {
                            if (item.id == it.data) {
                                item.likes--
                                sharedPreference.removeLikeFromUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item), item)
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_saved))

                        // updates local list
                        for (item in recipeList.toMutableList()) {
                            if (item.id == it.data) {
                                sharedPreference.addSaveToUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item), item)
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_from_saves))

                        // updates local list
                        for (item in recipeList.toMutableList()) {
                            if (item.id == it.data) {
                                sharedPreference.removeSaveFromUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item), item)
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

    }


    /*fun observer() {
        authModel.user.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Loading -> {
                    Log.i(TAG,"Loading...")
                }
                is Resource.Success -> {
                    response.data
                    val user = response.data
                    binding.tvName.text = user!!.first_name + " " + user!!.last_name
                    Log.i(TAG,"${response.data}")
                }
                is Resource.Error -> {
                    if (response.code == ERROR_CODES.SESSION_INVALID){
                        findNavController().navigate(R.id.action_recipeListingFragment_to_loginFragment)
                        //todo delete user prefs
                        toast(getString(R.string.invalid_session))
                    }
                    Log.i(TAG,"${response.message}")
                }
                else -> {}
            }
        }
    }*/

    private fun changeVisibilityMenu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

}