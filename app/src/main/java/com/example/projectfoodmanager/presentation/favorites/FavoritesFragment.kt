package com.example.projectfoodmanager.presentation.favorites

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplifiedList
import com.example.projectfoodmanager.databinding.FragmentFavoritesBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class FavoritesFragment : Fragment(), ImageLoadingListener {

    /** Binding */
    lateinit var binding: FragmentFavoritesBinding

    /** ViewModels */
    private val userViewModel: UserViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    /** Constants */
    val TAG: String = "FavoritesFragmentFragment"

    // Debounce
    private var debounceJob: Job? = null
    private val debounceTime = 800L


    // RecyclerView
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private var scrollListener: RecyclerView.OnScrollListener? = null


    // Reloading current page
    private var refreshPage: Int = 0


    // Pagination
    private val defaultPageSize = 5
    private var noMoreRecipesMessagePresented = false


    // Filters
    // Chip Filters
    private var onlineChipFilter: Boolean = false
    private var selectedTab: String = SelectedTab.LIKED
    private lateinit var chipSelected: Chip

    // Tag Filters
    private var oldFilerTag: String =""

    // Search
    private var newSearch: Boolean = false

    /** Injects */
    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    /** Adapters */
    private val adapter by lazy {
        FavoritesRecipeListingAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_favoritesFragment_to_receitaDetailFragment,Bundle().apply {
                    putInt("recipe_id",item.id)
                })
                changeMenuVisibility(false,activity)

            },
            onLikeClicked = {item,like ->
                if(like)
                    recipeViewModel.addLikeOnRecipe(item.id)
                else
                    recipeViewModel.removeLikeOnRecipe(item.id)

            },
            onSaveClicked = {item,saved ->
                
                if(saved)
                    recipeViewModel.addSaveOnRecipe(item.id)
                else
                    recipeViewModel.removeSaveOnRecipe(item.id)

            },
            this
        )
    }

    /** Interfaces */
    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            adapter.imagesLoaded++
            if (adapter.imagesLoaded == adapter.imagesToLoad) {
                binding.progressBar.hide()
                binding.recyclerView.visibility = View.VISIBLE
            }

        }
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return if (this::binding.isInitialized) {

            binding.root
        } else {

            binding = FragmentFavoritesBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            manager.reverseLayout = false
            binding.recyclerView.layoutManager = manager
            binding.recyclerView.itemAnimator = null
            snapHelper.attachToRecyclerView(binding.recyclerView)

            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        bindObservers()
        super.onViewCreated(view, savedInstanceState)
        setUI()

    }

    override fun onStart() {
        changeTheme(false, activity, context)
        super.onStart()
    }

    override fun onResume() {
        changeMenuVisibility(true,requireActivity())
        super.onResume()
    }

    override fun onPause() {
        // prevent flicker when reloading recipes
        binding.recyclerView.visibility = View.INVISIBLE
        super.onPause()
    }

    /**
     *  General
     * */

    private fun setUI(){



        binding.recyclerView.adapter = adapter

        /**
         * Add Recipe
         */

        // todo
        binding.addRecipeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_newRecipeFragment)
        }

        /**
         * Pagination
         * */

        setRecyclerViewScrollListener()


        /**
         * Search filter
         */

        binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null && text != "") {
                    // importante se não não funciona
                    currentPage = 1
                    newSearch = true

                    // debouncer
                    val handler = Handler()
                    handler.postDelayed({
                        if (searchString == text) {
                            // verifica se tag está a ser usada se não pesquisa a string nas tags da receita

                            recipeViewModel.getRecipes(
                                page = currentPage,
                                pageSize = defaultPageSize,
                                searchString = searchString
                            )

                        }
                    }, 400)

                    searchString = text.lowercase()

                } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                else if (searchString != "" && text == "") {
                    searchString = text
                    currentPage = 1

                    recipeViewModel.getRecipes(
                        page = currentPage,
                        pageSize = defaultPageSize,
                        searchString = searchString
                    )
                } else {
                    searchString = ""
                }

                //slowly move to position 0
                binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
                return true
            }
        })

        /**
         * Chip filters
         */

        chipSelected = binding.chipGroup.selectChipByTag(SelectedTab.LIKED)!!

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedId ->

            if (checkedId.isNotEmpty()) {
                group.findViewById<Chip>(checkedId[0])?.let {
                    chipSelected.isChecked = false
                    chipSelected = it

                    updateView(chipSelected.tag as String )
                }
            } else {
                // If no chip is selected, select the last selected one
                chipSelected.isChecked = true
            }
        }



        /**
         * Bottom Tag Filters
         */

        binding.meatFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.CARNE)
            filterOnClick(RecipeListingFragmentFilters.CARNE)
        }
        binding.fishFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.PEIXE)
            filterOnClick(RecipeListingFragmentFilters.PEIXE)
        }
        binding.soupFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.SOPAS)
            filterOnClick(RecipeListingFragmentFilters.SOPAS)
        }
        binding.vegFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.VEGETARIANA)
            filterOnClick(RecipeListingFragmentFilters.VEGETARIANA)
        }
        binding.fruitFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.FRUTA)
            filterOnClick(RecipeListingFragmentFilters.FRUTA)
        }
        binding.drinkFiltIB.setOnClickListener {
            changeFilterSearch(RecipeListingFragmentFilters.BEBIDAS)
            filterOnClick(RecipeListingFragmentFilters.BEBIDAS)
        }



        // coisas que só faz online

        if (isOnline(requireContext())) {
            updateView(chipSelected.tag as String)

            /**
             * Search filter
             */

            binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text != null && text != "") {
                        // importante se não não funciona
                        currentPage = 1
                        newSearch = true

                        // Update the search string
                        searchString = text.lowercase()

                        // Cancel the previous job if it's still running
                        debounceJob?.cancel()

                        // Start a new debounce job
                        debounceJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(debounceTime)
                            if (searchString == text) {
                                updateView(selectedTab )
                            }
                        }


                    } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                    else if (searchString != "" && text == "") {
                        searchString = text
                        adapter.removeItems()
                        currentPage = 1
                        updateView(selectedTab)
                    } else {
                        searchString = ""
                    }

                    //slowly move to position 0
                    binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
                    return true
                }
            })


        } else {
            // TODO offline mode

            toast("Está offline")



        }

    }

    private fun bindObservers() {

        userViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteSession()
                        sharedPreference.deleteSession()
                        toast(getString(R.string.user_had_no_shared_preferences))
                        findNavController().navigate(R.id.action_profile_to_login)
                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${result.message}.")
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        // Like function

        recipeViewModel.functionGetLikedRecipes.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view
                        if (refreshPage != 0) {

                            val lastIndex = if (recipeListed.size >= PaginationNumber.DEFAULT) (refreshPage * PaginationNumber.DEFAULT) else adapter.itemCount
                            var firstIndex = if (recipeListed.size >= PaginationNumber.DEFAULT) (lastIndex - PaginationNumber.DEFAULT) else 0

                            recipeListed.subList(firstIndex, lastIndex).clear()


                            for (recipe in result.data!!.result) {
                                recipeListed.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.setItems(recipeListed)

                            //reset control variables
                            refreshPage = 0
                        }
                        else {

                            // sets page data

                            currentPage = result.data!!._metadata.page
                            nextPage = result.data._metadata.nextPage != null

                            noMoreRecipesMessagePresented = nextPage

                            // check if list empty

                            if(result.data.result.isEmpty()){
                                binding.progressBar.hide()
                                binding.tvNoRecipes.visibility=View.VISIBLE
                                adapter.removeItems()
                                return@let
                            }else{
                                binding.tvNoRecipes.visibility=View.GONE

                            }

                            // checks if new search

                            if (currentPage == 1){
                                adapter.setItems(result.data.result)
                            }
                            else{
                                adapter.addItems(result.data.result)
                            }

                        }

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()

                        result.data?.let {
                            if (chipSelected == binding.chipCurtidas)
                                adapter.removeItem(it)
                            else
                                adapter.updateItem(it)
                        }

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        result.data?.let {
                            if (chipSelected == binding.chipCurtidas)
                                adapter.removeItem(it)
                            else
                                adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        result.data?.let {
                            if (chipSelected == binding.chipGuardados)
                                adapter.removeItem(it)
                            else
                                adapter.updateItem(it)

                        }

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        result.data?.let {
                            if (chipSelected == binding.chipGuardados)
                                adapter.removeItem(it)
                            else
                                adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        // comments function


    }


    /**
     *  Functions
     * */

    private fun updateView(currentTabSelected: String) {
        // UI related
        binding.recyclerView.visibility = View.INVISIBLE
        binding.progressBar.show()
        changeRecyclerViewScrollListener(false)


        val recipes = getChipList(currentTabSelected,searchTag, searchString)

        binding.addRecipeBtn.visibility = View.GONE
        this.selectedTab = currentTabSelected

        when(currentTabSelected){
            binding.chipCurtidas.tag -> {

                onlineChipFilter = true
                changeRecyclerViewScrollListener(true)

            }
            binding.chipGuardados.tag  ->{


                onlineChipFilter = false

                //list
                if (recipes.isEmpty()){
                    binding.progressBar.hide()
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_saved)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.setItems(recipes)
            }
            binding.chipCriadas.tag  ->{


                onlineChipFilter = false

                binding.addRecipeBtn.visibility = View.VISIBLE

                //list
                if (recipes.isEmpty()){
                    binding.progressBar.hide()
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_created)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.setItems(recipes)

            }
            binding.chipCommented.tag  ->{

                toast("Sorry, not implement yet...")
                binding.progressBar.hide()
                return

                onlineChipFilter = true
                changeRecyclerViewScrollListener(true)
            }
            binding.chipLastSeem.tag  ->{
                toast("Sorry, not implement yet...")
                binding.progressBar.hide()
                return

                onlineChipFilter = true

                adapter.setItems(recipes)

                changeRecyclerViewScrollListener(true)
            }
        }
    }

    private fun changeRecyclerViewScrollListener(state: Boolean) {
        if (state)
            binding.recyclerView.addOnScrollListener(scrollListener!!)
        else
            binding.recyclerView.removeOnScrollListener(scrollListener!!)
    }

    private fun setRecyclerViewScrollListener() {

        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // prevent missed calls to api // needs to be reseted on search so it could be a next page

                    val pastVisibleItemSinceLastFetch: Int = manager.findLastCompletelyVisibleItemPosition()


                    // if User is on the penultimate recipe of currenct page, get next page
                    if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 3))
                        if (nextPage) {
                            //val visibleItemCount: Int = manager.childCount
                            //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                            if (selectedTab == SelectedTab.LIKED)
                                recipeViewModel.getLikedRecipes(
                                    page = ++currentPage,
                                    pageSize = defaultPageSize,
                                    searchString = searchString
                                )
                            else if (selectedTab == SelectedTab.COMMENTED )
                                recipeViewModel.getRecipes(page = ++currentPage, pageSize = defaultPageSize, searchString = searchString)

                            // prevent double request, this variable is change after response from getRecipes
                            nextPage = false
                        }

                    // if User is on the last recipe of currenct page, and no next page present notice to user
                    if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 1))
                        if (!nextPage && !noMoreRecipesMessagePresented) {
                            noMoreRecipesMessagePresented = true
                            toast("Sorry cant find more recipes.", ToastType.ALERT)
                        }




                }

                super.onScrollStateChanged(recyclerView, newState)

            }
        }

        binding.recyclerView.addOnScrollListener(scrollListener!!)

    }

    private fun showValidationErrors(error: String) {
        toast(error)
        Log.d(TAG, "bindObservers: $error.")

    }

    /** Filters */

    /** Chip Filters */

    private fun ChipGroup.selectChipByTag(desiredTag: String): Chip? {
        for (index in 0 until childCount) {
            val chip = getChildAt(index) as Chip
            if (chip.tag == desiredTag) {
                chip.isChecked = true
                return chip
            }
        }
        return null
    }

    private fun getChipList(currentTabSelected: String,filterByTag: String,filterByString: String): MutableList<RecipeSimplified> {

        when(currentTabSelected){
            binding.chipCurtidas.tag  -> {
                recipeViewModel.getLikedRecipes(pageSize = 5, searchString = filterByString, searchTag = filterByTag)
            }
            binding.chipGuardados.tag  ->{

                return when{
                    filterByTag.isNotEmpty() && filterByString.isNotEmpty() -> {
                        sharedPreference.getUserRecipesBackgroundSavedRecipes()
                            .toRecipeSimplifiedList()
                            .filter { recipe ->
                                recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) } &&
                                        recipe.title.contains(filterByString, ignoreCase = true)
                            }
                            .toMutableList()
                    }
                    filterByTag.isNotEmpty() -> {
                        sharedPreference.getUserRecipesBackgroundSavedRecipes().toRecipeSimplifiedList().filter { recipe ->
                            recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) }
                        }.toMutableList()
                    }
                    filterByString.isNotEmpty() ->{
                        sharedPreference.getUserRecipesBackgroundSavedRecipes().toRecipeSimplifiedList().filter { recipe ->
                            recipe.title.contains(filterByString , ignoreCase = true)
                        }.toMutableList()
                    }
                    else ->sharedPreference.getUserRecipesBackgroundSavedRecipes().toRecipeSimplifiedList()
                }

            }
            binding.chipCriadas.tag  ->{
                return when{
                    filterByTag.isNotEmpty() && filterByString.isNotEmpty() -> {
                        sharedPreference.getUserRecipesBackgroundCreatedRecipes()
                            .toRecipeSimplifiedList()
                            .filter { recipe ->
                                recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) } &&
                                        recipe.title.contains(filterByString, ignoreCase = true)
                            }
                            .toMutableList()
                    }
                    filterByTag.isNotEmpty() -> {
                        sharedPreference.getUserRecipesBackgroundCreatedRecipes().toRecipeSimplifiedList().filter { recipe ->
                            recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) }
                        }.toMutableList()
                    }
                    filterByString.isNotEmpty() ->{
                        sharedPreference.getUserRecipesBackgroundCreatedRecipes().toRecipeSimplifiedList().filter { recipe ->
                            recipe.title.contains(filterByString , ignoreCase = true)
                        }.toMutableList()
                    }
                    else ->sharedPreference.getUserRecipesBackgroundCreatedRecipes().toRecipeSimplifiedList()
                }

            }
            binding.chipCommented.tag  ->{
                //recipeViewModel.getRecipesCommentedByUserPaginated(clientId = user.id)
                // todo not implemented
                return mutableListOf()
            }
            binding.chipLastSeem.tag  ->{
                // todo not implemented
                return mutableListOf()
            }
        }
        return mutableListOf()
    }

    /** Tab Filters */

    private fun changeFilterSearch(tag: String){
        searchTag = if (searchTag == tag){
            ""
        } else{
            tag
        }

        updateView(chipSelected.tag as String)
    }

    private fun filterOnClick(tag:String){

        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)

        // Change last colors
        if (oldFilerTag.isNotEmpty()) {
            val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(oldFilerTag + "CL") as? ConstraintLayout
            val tvToUpdate: TextView? = binding.root.findViewWithTag(oldFilerTag + "TV") as? TextView
            val ibToUpdate: ImageButton? = binding.root.findViewWithTag(oldFilerTag + "_filt_IB") as? ImageButton

            clToUpdate?.apply {
                backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
                elevation = 0f
            }

            tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

            ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))
        }

        // if double clicked reset search
        if (oldFilerTag == tag) {
            oldFilerTag = ""
            currentPage = 1

            if (selectedTab ==SelectedTab.LIKED)
                recipeViewModel.getRecipes(page = currentPage, searchString = searchString)
            else if (selectedTab == SelectedTab.CREATED || selectedTab == SelectedTab.SAVED )
                getFilteredRecipesByTag(tag)
            return
        }

        activateTag(tag)

    }

    private fun getFilteredRecipesByTag(tag:String):MutableList<RecipeSimplified>{
        when(selectedTab) {
            SelectedTab.CREATED -> {
                return sharedPreference.getUserRecipesBackgroundCreatedRecipes().toRecipeSimplifiedList().filter { recipe ->
                    recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) }
                }.toMutableList()
            }
            SelectedTab.SAVED -> {
                return sharedPreference.getUserRecipesBackgroundSavedRecipes().toRecipeSimplifiedList().filter { recipe ->
                    recipe.tags.any { recipeTag -> recipeTag.title.equals(tag, ignoreCase = true) }
                }.toMutableList()
            }
        }

        return mutableListOf()
    }

    private fun activateTag(tag: String) {
        val cl: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tv: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ib: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton


        cl?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            elevation = 3f
        }

        tv?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        ib?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_1))
        oldFilerTag = tag
    }


    /**
     *  Object
     * */

    companion object {
        private var recipeListed: MutableList<RecipeSimplified> = mutableListOf()


        // pagination
        private var currentPage:Int = 1
        private var nextPage:Boolean = true

        // Filters
        private var searchTag: String = ""
        private var searchString: String = ""
        private var chipSelected: Chip? = null


        object SelectedTab {
            const val LIKED = "1"
            const val SAVED = "2"
            const val CREATED = "3"
            const val COMMENTED = "4"
            const val LAST_SEEN = "5"
        }

    }
}

