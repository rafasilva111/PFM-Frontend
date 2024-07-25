package com.example.projectfoodmanager.presentation.recipe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.di.notification.MyFirebaseMessagingService
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.UserViewModel
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
    private val userViewModel by activityViewModels<UserViewModel>()

    // constants
    private val TAG: String = "RecipeListingFragment"


    private var newSearch: Boolean = false
    private var noMoreRecipesMessagePresented = false

    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var refreshPage: Int = 0
    private var oldFilerTag: String =""
    private var numberOfNotifications: Int = 0



    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            numberOfNotifications += 1
            changeNotificationNumber()
        }
    }

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val adapter by lazy {
        RecipeListingAdapter(
            requireContext(),
            onItemClicked = {pos,recipe ->
                // use pos to reset current page to pos page, so it will refresh the pos page
                refreshPage =  ceil((pos+1).toFloat()/PaginationNumber.DEFAULT).toInt()

                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putInt("recipe_id",recipe.id)
                })

                changeMenuVisibility(false,activity)
            },
            onLikeClicked = {recipe,like ->


                if (like) {
                    recipeViewModel.addLikeOnRecipe(recipe.id)
                } else {
                    recipeViewModel.removeLikeOnRecipe(recipe.id)
                }


            },
            onSaveClicked = {recipe,saved ->

                if (saved) {
                    recipeViewModel.addSaveOnRecipe(recipe.id)
                } else {
                    recipeViewModel.removeSaveOnRecipe(recipe.id)
                }

            }
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
            binding.recyclerView.itemAnimator = null
            snapHelper.attachToRecyclerView(binding.recyclerView)

            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         *
         * Notification Navigation
         *
         */

        val activity = requireActivity()
        if (activity.intent.hasExtra("fragmentToOpen")) {

            val notification: Notification? = activity.intent.getParcelableExtra("object")
            when (activity.intent.getIntExtra("fragmentToOpen",-1)) {
                FragmentsToOpen.FRAGMENT_COMMENTS -> {
                    // Handle notification for recipe created
                    findNavController().navigate(R.id.action_recipeListingFragment_to_receitaCommentsFragment,Bundle().apply {
                        notification?.let {
                            putInt("recipe_id",notification.recipe!!.id)
                            putInt("comment_id",notification.comment!!.id)
                        }
                    })
                }
                // Add more cases if you have other fragments to navigate to
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onStart() {


        loadUI()
        super.onStart()
    }

    private fun setUI() {


        /**
         * General
         */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false,activity,requireContext())

        setRecyclerViewScrollListener()

        //Get User in SharedPreferences
        val user = sharedPreference.getUserSession()

        binding.tvName.text = formatNameToNameUpper(getString(R.string.full_name, user.name))

        //VIP HEADER
        if (user.userType != "V") {
            binding.profileCV.foreground = null
            binding.vipIV.visibility = View.INVISIBLE
        }

        //VERIFIED HEADER
        if (user.verified)
            binding.verifyUserHeaderIV.visibility = View.VISIBLE

        //Set Profile Image
        loadUserImage(binding.ivProfilePic, user.imgSource)


        if (isOnline(requireContext())) {
            binding.recyclerView.adapter = adapter



            recipeViewModel.getRecipes(page = currentPage, searchString= searchString,searchTag= searchTag, by = sortedBy)

            // get recipes for first time


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
                                    searchString =searchString,
                                    searchTag = searchTag,
                                    by = sortedBy
                                )

                            }
                        }, 400)

                        searchString = text.lowercase()

                    } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                    else if (searchString != "" && text == "") {
                        searchString = text
                        recipeListed = mutableListOf()
                        currentPage = 1

                        recipeViewModel.getRecipes(
                            page = currentPage,
                            searchString = searchString,
                            searchTag = searchTag,
                            by = sortedBy
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


            activateSearchChip(chipGroup)



            chipGroup.setOnCheckedStateChangeListener { group, checkedId ->
                if (checkedId.isNotEmpty()) {
                    group.findViewById<Chip>(checkedId[0])?.let {
                        chipSelected!!.isChecked = false
                        chipSelected = it
                        updateView(chipSelected!!)
                    }
                } else {
                    // If no chip is selected, select the last selected one
                    chipSelected!!.isChecked = true
                }
            }

            /**
             * Notifications
             */

            userViewModel.getNotifications(pageSize = 1)

            /**
             * Bottom Tag Filters
             */

            if (searchTag.isNotEmpty())
                activateSearchTag(searchTag)

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

    private fun loadUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false, activity, requireContext())

    }


    override fun onResume() {
        super.onResume()
        // Register the broadcast receiver
        context?.registerReceiver(notificationReceiver, IntentFilter(MyFirebaseMessagingService.ACTION_NOTIFICATION_RECEIVED))
    }

    override fun onPause() {
        super.onPause()
        // Unregister the broadcast receiver to avoid memory leaks
        context?.unregisterReceiver(notificationReceiver)
    }

    private fun activateSearchChip(chipGroup: ChipGroup) {
        for (i in 0 until chipGroup.childCount) {
            val chip: Chip = chipGroup.getChildAt(i) as Chip

            if (chip.tag == sortedBy) {
                chip.isChecked = true
                chipSelected = chip
                break
            }

        }
    }

    private fun activateSearchTag(tag: String) {
        val cl: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tv: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ib: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton


        cl?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            elevation = 3f
        }

        tv?.setTextColor(resources.getColor(R.color.white))

        ib?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_1))
        oldFilerTag = tag
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    val pastVisibleItemSinceLastFetch: Int = manager.findLastCompletelyVisibleItemPosition()


                    // if User is on the penultimate recipe of currenct page, get next page
                    if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 2))
                        if (nextPage ){
                            //val visibleItemCount: Int = manager.childCount
                            //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())



                                // verifica se tag está a ser uusada se não pesquisa a string nas tags da receita
                            if (searchTag.isEmpty())
                                recipeViewModel.getRecipes(page = ++currentPage, searchString = searchString,searchTag= searchString, by = sortedBy)
                            else{
                                recipeViewModel.getRecipes(page = ++currentPage, searchString = searchString,searchTag= searchTag, by = sortedBy)
                            }

                        }

                    // if User is on the last recipe of currenct page, and no next page present notice to user
                    if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 1))
                        if (!nextPage && !noMoreRecipesMessagePresented){
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
            recipeViewModel.getRecipes(page = currentPage, searchString = searchString, by = sortedBy)
            return
        }

        activateSearchTag(tag)

    }

    private fun bindObservers() {

        /**
         * Recipes
         */

        recipeViewModel.functionGetRecipes.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view

                        if (refreshPage != 0) {
                            binding.progressBar.hide()
                            val lastIndex =
                                if (recipeListed.size >= PaginationNumber.DEFAULT) (refreshPage * PaginationNumber.DEFAULT) - 1 else recipeListed.size - 1
                            var firstIndex = if (recipeListed.size >= PaginationNumber.DEFAULT) lastIndex - 4 else 0

                            recipeListed.subList(firstIndex, lastIndex + 1).clear()


                            for (recipe in it.data!!.result) {
                                recipeListed.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.updateList(recipeListed)

                            //reset control variables
                            refreshPage = 0
                        }
                        else {
                            binding.progressBar.hide()

                            // sets page data

                            currentPage = it.data!!._metadata.page
                            nextPage = it.data._metadata.nextPage != null

                            noMoreRecipesMessagePresented = nextPage

                            // check if list empty

                            if(it.data.result.isEmpty()){
                                binding.noRecipesTV.visibility=View.VISIBLE
                                adapter.updateList(mutableListOf())
                                return@let
                            }else{
                                binding.noRecipesTV.visibility=View.GONE

                            }

                            // checks if new search

                            if (currentPage == 1){
                                recipeListed = it.data.result

                            }
                            else{
                                recipeListed += it.data.result
                            }

                            adapter.updateList(recipeListed)
                        }

                        binding.progressBar.hide()

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.noRecipesTV.visibility = View.GONE
                        binding.offlineTV.visibility = View.GONE
                        binding.progressBar.show()
                    }
                }
            }
        }

        /**
         * Like function
         */


        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        result.data?.let {
                            adapter.updateItem(it)
                        }


                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
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
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /**
         * Save function
         */

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
            when (result) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()

                        result.data?.let {
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
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
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /**
         * Notifications
         */

        userViewModel.getNotificationsResponseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        numberOfNotifications = result.data!!.notSeen

                        changeNotificationNumber()



                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

    }

    private fun changeNotificationNumber(){
        if (numberOfNotifications>0 ) {
            binding.notificationsBadgeTV.visibility = View.VISIBLE
            binding.notificationsBadgeTV.text = numberOfNotifications.toString()
        } else{
            binding.notificationsBadgeTV.visibility =View.GONE
        }
    }

    private fun changeFilterSearch(tag: String){
        if (searchTag == tag){
           searchTag =""
            updateView(chipSelected!!)
        }
        else{
            searchTag =tag
            recipeViewModel.getRecipes(searchTag = tag, by = sortedBy)
        }
    }

    private fun updateView(currentTabSelected: View) {

        when(currentTabSelected){
            binding.recipeListingFilterVerified -> {
                sortedBy = RecipesSortingType.VERIFIED
            }
            binding.recipeListingFilterAll-> {
                sortedBy = RecipesSortingType.ALL

            }
            binding.recipeListingFilterRecent-> {
                sortedBy = RecipesSortingType.DATE
            }
            binding.recipeListingFilterSugestions-> {
                toast("Sorry, not implement yet...")
                return
            }
            binding.recipeListingFilterPersonalizedSugestions-> {
                toast("Sorry, not implement yet...")
                return
            }
            binding.recipeListingFilterRandom-> {
                sortedBy = RecipesSortingType.RANDOM
            }
            binding.recipeListingFilterMostLiked-> {
                sortedBy = RecipesSortingType.LIKES
            }
            binding.recipeListingFilterMostSaved-> {
                sortedBy = RecipesSortingType.SAVES
            }
        }
        recipeViewModel.getRecipes(page = currentPage, searchString= searchString,searchTag= searchTag, by = sortedBy)
        //slowly move to position 0
        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
    }

    companion object {

        private var recipeListed: MutableList<RecipeSimplified> = mutableListOf()

        // pagination
        private var currentPage:Int = 1
        private var nextPage:Boolean = true

        // Filters
        private var searchTag: String = ""
        private var searchString: String = ""
        private var chipSelected: Chip? = null
        private var sortedBy: String = RecipesSortingType.VERIFIED

    }

}