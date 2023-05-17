package com.example.projectfoodmanager.presentation.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {

    // constantes (cuidado com esta merda)

    private var current_page:Int = 1
    private var next_page:Boolean = true

    private var recipeList: MutableList<Recipe> = mutableListOf()

    private var stringToSearch: String? = null
    private var newSearch: Boolean = false
    // this needs to happen otherwise we will have a spam of toast
    private var no_more_recipes_message_presented = false

    @Inject
    lateinit var sharedPreference: SharedPreference

    private var isFirstTimeCall = true
    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var searchMode: Boolean = false

    val TAG: String = "RecipeListingFragment"
    lateinit var binding: FragmentRecipeListingBinding
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val authViewModel: AuthViewModel by viewModels()
    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->

                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })

                changeVisib_Menu(false)
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
    ): View? {
        //observer()
        //todo check for internet connection

        bindObservers()

        if (this::binding.isInitialized){
            return binding.root
        }else {

            // load profile image

            val userSession: User? = sharedPreference.getUserSession()
            if (userSession != null && !userSession.img_source.isNullOrEmpty()){
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


    override fun onResume() {
        changeVisib_Menu(true)
        super.onResume()
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // prevent missed calls to api // needs to be reseted on search so it could be a next page

                    if (next_page){

                        Log.d(TAG, "onScrollStateChanged: $next_page")
                        binding.recyclerView.removeOnScrollListener(scrollListener)


                        //val visibleItemCount: Int = manager.childCount
                        val pastVisibleItem: Int =
                            manager.findLastCompletelyVisibleItemPosition()
                        //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                        if ((pastVisibleItem + 1) >= recipeList.size){
                            if (stringToSearch.isNullOrEmpty()) {
                                recipeViewModel.getRecipesPaginated(++current_page)
                            } else {
                                recipeViewModel.getRecipesByTitleAndTags(stringToSearch!!, ++current_page)
                            }
                        }
                        //Log.d(TAG, pag_index.toString())
                        //Log.d(TAG, visibleItemCount.toString())
                        Log.d(TAG, pastVisibleItem.toString())
                    }
                    else if (no_more_recipes_message_presented == false){
                        no_more_recipes_message_presented = true
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
        changeVisib_Menu(true)

        // user data
        val user = sharedPreference.getUserSession()
        if (user!= null){
            binding.tvName.text = user.first_name+" "+ user.last_name
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
                        current_page = 1
                        newSearch = true
                        stringToSearch=text
                        recipeViewModel.getRecipesByTitleAndTags(text, current_page)
                    }
                    else if ( text == ""){
                        stringToSearch=null
                        current_page = 1
                        recipeViewModel.getRecipesPaginated(current_page)
                    }
                    else{
                        stringToSearch=null
                        recipeViewModel.getRecipesPaginated(current_page)
                    }
                    return true
                }
            })
            recipeViewModel.getRecipesPaginated(current_page)


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
            binding.offlineText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }


    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {
        recipeViewModel.recipeResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                searchMode = false

                when (it) {

                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()

                        current_page = it.data!!._metadata.current_page

                        // check next page to failed missed calls to api
                        next_page = it.data._metadata.next!=null


                        for (recipe in it.data.result) {
                            recipeList.add(recipe)
                        }
                        adapter.updateList(recipeList)


                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()

                    }
                }
            }
        })
        recipeViewModel.recipeSearchByTitleAndTagsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        if (it.data != null){
                            // numa nova procura resetar a lista de receitas
                            if (newSearch)
                                recipeList = mutableListOf()

                            for (recipe in it.data.result) {
                                recipeList.add(recipe)
                            }
                            adapter.updateList(recipeList)
                            newSearch = false

                            // check next page to failed missed calls to api
                            next_page = it.data._metadata.next!=null
                            // safe call for debaunce
                            current_page = it.data!!._metadata.current_page
                            // se houver next page soma se não não faz nada

                        }
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
        })


        // Like function


        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()){
                            if (item.id == it.data){
                                item.likes ++
                                sharedPreference.addLikeToUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item),item)
                                break
                            }
                        }
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
        })

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()){
                            if (item.id == it.data){
                                item.likes --
                                sharedPreference.removeLikeFromUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item),item)
                                break
                            }
                        }
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
        })

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()){
                            if (item.id == it.data){
                                item.likes ++
                                sharedPreference.addSaveToUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item),item)
                                break
                            }
                        }
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
        })

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_liked))

                        // updates local list
                        for (item in recipeList.toMutableList()){
                            if (item.id == it.data){
                                item.likes --
                                sharedPreference.removeSaveFromUserSession(item)
                                adapter.updateItem(recipeList.indexOf(item),item)
                                break
                            }
                        }
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
        })

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

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

}