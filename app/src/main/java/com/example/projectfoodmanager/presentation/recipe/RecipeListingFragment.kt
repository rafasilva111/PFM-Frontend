package com.example.projectfoodmanager.presentation.recipe

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil


@AndroidEntryPoint
class RecipeListingFragment : Fragment() {




    // binding
    lateinit var binding: FragmentRecipeListingBinding

    // viewModels
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    // constants
    private val TAG: String = "RecipeListingFragment"

    private var recipeList: MutableList<Recipe> = mutableListOf()

    // pagination
    private var currentPage:Int = 1
    private var nextPage:Boolean = true


    private var newSearch: Boolean = false
    private var noMoreRecipesMessagePresented = false

    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var refreshPage: Int = 0
    private var oldFilertTag: String =""

    // chips filter
    private lateinit var chipSelected: Chip

    private var sortedBy: String = RecipesSortingType.VERIFIED

    // tags filter ( bottom filters)
    private var filteredTag: String = ""

    // search filter
    private var stringToSearch: String = ""

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->
                // use pos to reset current page to pos page, so it will refresh the pos page
                refreshPage =  ceil((pos+1).toFloat()/PaginationNumber.DEFAULT).toInt()
                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })

                changeMenuVisibility(false,activity)
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
            sharedPreference
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindObservers()

        return if (this::binding.isInitialized){
            binding.root
        }else {


            binding = FragmentRecipeListingBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation=LinearLayoutManager.HORIZONTAL
            manager.reverseLayout=false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)

            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setUI() {


        /**
         * General
         */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeStatusBarColor(false,activity,requireContext())




        setRecyclerViewScrollListener()

        //Get User in SharedPreferences
        val user = sharedPreference.getUserSession()

        binding.tvName.text = formatNameToNameUpper(getString(R.string.full_name, user.name))

        //VIP HEADER
        if (user.user_type != "V") {
            binding.profileCV.foreground = null
            binding.vipIV.visibility = View.INVISIBLE
        }

        //VERIFIED HEADER
        if (user.verified)
            binding.verifyUserHeaderIV.visibility = View.VISIBLE

        //Set Profile Image
        loadUserImage(binding.ivProfilePic, user.img_source)


        if (isOnline(requireContext())) {
            binding.recyclerView.adapter = adapter

            // get recipes for first time
            recipeViewModel.getRecipes(page = currentPage, by = RecipesSortingType.VERIFIED)

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
                            if (stringToSearch == text) {
                                // verifica se tag está a ser usada se não pesquisa a string nas tags da receita
                                if (filteredTag.isEmpty())
                                    recipeViewModel.getRecipes(
                                        page = currentPage,
                                        searchString = stringToSearch,
                                        searchTag = stringToSearch,
                                        by = sortedBy
                                    )
                                else {
                                    recipeViewModel.getRecipes(
                                        page = currentPage,
                                        searchString = stringToSearch,
                                        searchTag = filteredTag,
                                        by = sortedBy
                                    )
                                }
                            }
                        }, 400)

                        stringToSearch = text

                    } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                    else if (stringToSearch != "" && text == "") {
                        stringToSearch = text
                        recipeList = mutableListOf()
                        currentPage = 1

                        recipeViewModel.getRecipes(
                            page = currentPage,
                            searchString = stringToSearch,
                            searchTag = filteredTag,
                            by = sortedBy
                        )
                    } else {
                        stringToSearch = ""
                    }

                    //slowly move to position 0
                    binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
                    return true
                }
            })

            /**
             * Notifications
             */

            binding.notificationIV.setOnClickListener {
                findNavController().navigate(R.id.action_recipeListingFragment_to_notificationFragment)
                changeMenuVisibility(false, activity)
            }


            /**
             * Chip filters
             */

            val chipGroup: ChipGroup = binding.chipGroup
            chipSelected = chipGroup.findViewById(chipGroup.checkedChipId)


            chipGroup.setOnCheckedStateChangeListener { group, checkedId ->

                if (checkedId.isNotEmpty()) {
                    group.findViewById<Chip>(checkedId[0])?.let {
                        chipSelected.isChecked = false
                        chipSelected = it
                        updateView(chipSelected)
                    }
                } else {
                    // If no chip is selected, select the last selected one
                    chipSelected.isChecked = true
                }
            }

            /**
             * Notifications
             */

            authViewModel.getNotifications()

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
            binding.vegiFiltIB.setOnClickListener {
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

        } else {
            binding.offlineTV.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // prevent missed calls to api // needs to be reseted on search so it could be a next page

                    if (nextPage){
                        //val visibleItemCount: Int = manager.childCount
                        val pastVisibleItem: Int =
                            manager.findLastCompletelyVisibleItemPosition()
                        //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                        if ((pastVisibleItem + 1) >= recipeList.size){

                            // verifica se tag está a ser uusada se não pesquisa a string nas tags da receita
                            if (filteredTag.isEmpty())
                                recipeViewModel.getRecipes(page = ++currentPage, searchString = stringToSearch,searchTag= stringToSearch, by = sortedBy)
                            else{
                                recipeViewModel.getRecipes(page = ++currentPage, searchString = stringToSearch,searchTag= filteredTag, by = sortedBy)
                            }
                        }
                        //Log.d(TAG, pag_index.toString())
                        //Log.d(TAG, visibleItemCount.toString())
                        //Log.d(TAG, pastVisibleItem.toString())
                    }
                    else if (!noMoreRecipesMessagePresented){
                        noMoreRecipesMessagePresented = true
                        toast("Sorry cant find more recipes.",ToastType.ALERT)
                    }


                }

                super.onScrollStateChanged(recyclerView, newState)

            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

    }

    private fun showValidationErrors(error: String) {
        toast(error, type = ToastType.ERROR)
    }

    private fun filterOnClick(tag:String){

        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)

        val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(oldFilertTag + "CL") as? ConstraintLayout
        val tvToUpdate: TextView? = binding.root.findViewWithTag(oldFilertTag + "TV") as? TextView
        val ibToUpdate: ImageButton? = binding.root.findViewWithTag(oldFilertTag + "_filt_IB") as? ImageButton

        clToUpdate?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            elevation = 0f
        }

        tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))

        if (oldFilertTag == tag) {
            oldFilertTag = ""
            currentPage = 1
            recipeViewModel.getRecipes(page = currentPage, searchString = stringToSearch, by = sortedBy)
            return
        }


        oldFilertTag = tag

        val cl: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tv: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ib: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton


        cl?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            elevation = 3f
        }

        tv?.setTextColor(resources.getColor(R.color.white))

        ib?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_1))
    }

    private fun bindObservers() {

        /**
         * Recipes
         */

        recipeViewModel.recipesResponseLiveData.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view

                        if (refreshPage != 0) {
                            binding.progressBar.hide()
                            val lastIndex =
                                if (recipeList.size >= PaginationNumber.DEFAULT) (refreshPage * PaginationNumber.DEFAULT) - 1 else recipeList.size - 1
                            var firstIndex = if (recipeList.size >= PaginationNumber.DEFAULT) lastIndex - 4 else 0

                            recipeList.subList(firstIndex, lastIndex + 1).clear()


                            for (recipe in it.data!!.result) {
                                recipeList.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.updateList(recipeList)

                            //reset control variables
                            refreshPage = 0
                        }
                        else {
                            binding.progressBar.hide()

                            // sets page data

                            currentPage = it.data!!._metadata.current_page
                            nextPage = it.data._metadata.next != null

                            // check if list empty

                            if(it.data.result.isEmpty()){
                                binding.offlineTV.text = getString(R.string.no_recipes_found)
                                binding.offlineTV.visibility=View.VISIBLE
                                adapter.updateList(mutableListOf())
                                return@let
                            }else{
                                binding.offlineTV.visibility=View.GONE

                            }

                            // checks if new search

                            if (currentPage == 1){
                                recipeList = it.data.result
                                noMoreRecipesMessagePresented = false
                            }
                            else{
                                recipeList += it.data.result
                            }

                            adapter.updateList(recipeList)
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

        /**
         * Like function
         */


        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

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

        /**
         * Save function
         */

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
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

        /**
         * Notifications
         */

        authViewModel.getNotificationsResponseLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        val notificationNumber = it.data!!.result.count { notification ->
                            !notification.seen
                        }

                        if (notificationNumber>0 ) {
                            binding.notificationsBadgeTV.visibility = View.VISIBLE
                            binding.notificationsBadgeTV.text = notificationNumber.toString()
                        } else{
                            binding.notificationsBadgeTV.visibility =View.GONE
                        }

                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

    }

    private fun changeFilterSearch(tag: String){
        if (filteredTag == tag){
            filteredTag=""
            updateView(chipSelected)
        }
        else{
            filteredTag=tag
            recipeViewModel.getRecipes(searchTag = tag, by = sortedBy)
        }
    }

    private fun updateView(currentTabSelected: View) {

        when(currentTabSelected){
            binding.recipeListingFilterVerified -> {
                sortedBy = RecipesSortingType.VERIFIED
                recipeViewModel.getRecipes(by = sortedBy)
            }
            binding.recipeListingFilterAll-> {
                sortedBy = RecipesSortingType.ALL
                recipeViewModel.getRecipes()
            }
            binding.recipeListingFilterRecent-> {
                sortedBy = RecipesSortingType.DATE
                recipeViewModel.getRecipes(by = sortedBy)
            }
            binding.recipeListingFilterSugestions-> {
                // TODO
                //sortedBy = RecipesSortingType.VERIFIED
                //recipeViewModel.getRecipes(by = RecipesSortingType.SUGGESTION)
            }
            binding.recipeListingFilterPersonalizedSugestions-> {
                // TODO
                //sortedBy = RecipesSortingType.VERIFIED
                //recipeViewModel.getRecipes(by = RecipesSortingType.PERSONALIZED_SUGGESTION)
            }
            binding.recipeListingFilterRandom-> {
                sortedBy = RecipesSortingType.RANDOM
                recipeViewModel.getRecipes(by = sortedBy)
            }
            binding.recipeListingFilterMostLiked-> {
                sortedBy = RecipesSortingType.LIKES
                recipeViewModel.getRecipes(by = sortedBy)
            }
            binding.recipeListingFilterMostSaved-> {
                sortedBy = RecipesSortingType.SAVES
                recipeViewModel.getRecipes(by = sortedBy)
            }
        }
        //slowly move to position 0
        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
    }

    private fun ChipGroup.selectChipByTag(desiredTag: String): Chip? {
        for (index in 0 until childCount) {
            val chip = getChildAt(index) as Chip
            if (chip.text == desiredTag) {
                chip.isChecked = true
                return chip
            }
        }
        return null
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

}