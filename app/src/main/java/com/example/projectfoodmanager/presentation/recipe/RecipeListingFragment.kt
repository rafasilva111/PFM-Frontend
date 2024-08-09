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
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil


@AndroidEntryPoint
class RecipeListingFragment : Fragment(), ImageLoadingListener {



    /** Binding */
    lateinit var binding: FragmentRecipeListingBinding

    /** ViewModels */
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    /** Constants */
    private val TAG: String = "RecipeListingFragment"

    // RecyclerView
    private var snapHelper : SnapHelper = PagerSnapHelper()
    private lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener


    // Reloading current page
    private var refreshPage: Int = 0
    private var refreshPosition: Int = 0

    // Pagination
    private var noMoreRecipesMessagePresented = false

    // Filters

    // Chip Filters
    private var selectedTab: String = SelectedTab.VERIFIED

    // Tag Filters
    private var previousSelectTag: String =""

    // Search
    private var newSearch: Boolean = false

    // Notifications
    private var numberOfNotifications: Int = 0
    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            numberOfNotifications += 1
            changeNotificationNumber()
        }
    }

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager


    /** Adapters */

    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,recipe ->
                // use pos to reset current page to pos page, so it will refresh the pos page

                refreshPosition =  pos

                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putInt("recipe_id",recipe.id)
                })

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

    override fun onPause() {

        // reset adapter
        adapter.removeItems()
        binding.recyclerView.visibility = View.INVISIBLE

        // Unregister the broadcast receiver to avoid memory leaks
        context?.unregisterReceiver(notificationReceiver)
        super.onPause()
    }

    override fun onResume() {


        // Register the broadcast receiver
        context?.registerReceiver(notificationReceiver, IntentFilter(MyFirebaseMessagingService.ACTION_NOTIFICATION_RECEIVED))
        super.onResume()
    }

    /**
     *  General
     * */

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


            binding.chipGroup.setOnCheckedStateChangeListener { group, checkedId ->
                if (checkedId.isNotEmpty()) {
                    group.findViewById<Chip>(checkedId[0])?.let {
                        it.isChecked = false
                        updateView(it.tag as String)
                    }
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
                activateTag(searchTag)

            binding.meatFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.MEAT)
            }
            binding.fishFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.FISH)
            }
            binding.soupFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.SOUP)
            }
            binding.vegiFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.VEGAN)
            }
            binding.fruitFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.FRUIT)
            }
            binding.drinkFiltIB.setOnClickListener {
                changeTagFilter(RecipeListingFragmentFilters.DRINK)
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

    private fun bindObservers() {

        /**
         * Recipes
         */

        recipeViewModel.functionGetRecipes.observe(viewLifecycleOwner
        ) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // todo isto não está a fazer diferença
                        if (refreshPosition != 0) {
                            val refreshPage =  ceil((refreshPosition+1).toFloat()/PaginationNumber.DEFAULT).toInt()

                            val lastIndex = if (recipeListed.size >= PaginationNumber.DEFAULT) (refreshPage * PaginationNumber.DEFAULT) else adapter.itemCount
                            val firstIndex = if (recipeListed.size >= PaginationNumber.DEFAULT) (lastIndex - PaginationNumber.DEFAULT) else 0

                            recipeListed.subList(firstIndex, lastIndex).clear()


                            recipeListed.addAll(firstIndex, result.data!!.result)

                            adapter.setItems(recipeListed)

                            //reset control variables
                            refreshPosition = 0
                        }
                        else {

                            // sets page data

                            currentPage = result.data!!._metadata.page
                            nextPage = result.data._metadata.nextPage != null

                            noMoreRecipesMessagePresented = nextPage

                            // check if list empty

                            if(result.data.result.isEmpty()){
                                binding.progressBar.hide()
                                binding.noRecipesTV.visibility=View.VISIBLE
                                adapter.removeItems()
                                return@let
                            }else{
                                binding.noRecipesTV.visibility=View.GONE

                            }

                            // checks if new search

                            if (currentPage == 1){
                                recipeListed = result.data.result
                                adapter.setItems(result.data.result)
                            }
                            else{
                                recipeListed += result.data.result
                                adapter.addItems(result.data.result)
                            }
                        }


                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
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


                        result.data?.let {
                            adapter.updateItem(it)
                        }


                    }
                    is NetworkResult.Error -> {
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

                        result.data?.let {
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        toast(result.message.toString(), type = ToastType.ERROR)
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


                        result.data?.let {
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        toast(result.message.toString(), type = ToastType.ERROR)
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

                        result.data?.let {
                            adapter.updateItem(it)
                        }
                    }
                    is NetworkResult.Error -> {
                        toast(result.message.toString(), type = ToastType.ERROR)
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

    /**
     *  Functions
     * */

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    val pastVisibleItemSinceLastFetch: Int = manager.findLastCompletelyVisibleItemPosition()


                    // if User is on the penultimate recipe of currenct page, get next page
                    if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 3))
                        if (nextPage ){
                            //val visibleItemCount: Int = manager.childCount
                            //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())


                            recipeViewModel.getRecipes(page = ++currentPage, searchString = searchString,searchTag= searchTag, by = sortedBy)

                            // prevent double request, this variable is change after response from getRecipes
                            nextPage = false
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

    private fun updateView(currentTabSelected: String) {

        when(currentTabSelected){
            SelectedTab.VERIFIED -> {
                sortedBy = RecipesSortingType.VERIFIED
            }
            SelectedTab.ALL-> {
                sortedBy = RecipesSortingType.ALL

            }
            SelectedTab.MOST_RECENT-> {
                sortedBy = RecipesSortingType.DATE
            }
            SelectedTab.SUGGESTIONS-> {
                toast("Sorry, not implement yet...")
                return
            }
            SelectedTab.PERSONALIZED_SUGGESTIONS-> {
                toast("Sorry, not implement yet...")
                return
            }
            SelectedTab.RANDOM-> {
                sortedBy = RecipesSortingType.RANDOM
            }
            SelectedTab.MOST_LIKED-> {
                sortedBy = RecipesSortingType.LIKES
            }
            SelectedTab.MOST_SAVED-> {
                sortedBy = RecipesSortingType.SAVES
            }
        }
        recipeViewModel.getRecipes(page = 1, searchString= searchString,searchTag= searchTag, by = sortedBy)
        //slowly move to position 0
        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)
    }

    private fun changeNotificationNumber(){
        if (numberOfNotifications>0 ) {
            binding.notificationsBadgeTV.visibility = View.VISIBLE
            binding.notificationsBadgeTV.text = numberOfNotifications.toString()
        } else{
            binding.notificationsBadgeTV.visibility =View.GONE
        }
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

    /** Tab Filters */

    private fun changeTagFilter(tag: String){

        // Alter Tag
        searchTag = if (searchTag == tag){
            ""
        }
        else{
            tag
        }

        // Update Recipe List
        updateView(selectedTab)

        // Scroll to 0 position
        binding.recyclerView.layoutManager?.smoothScrollToPosition(binding.recyclerView, null, 0)

        // Alter button color

        // Change last colors
        if (previousSelectTag.isNotEmpty()) {
            deactivateTag(previousSelectTag)
        }

        // if double clicked reset search
        if (previousSelectTag == tag) {
            previousSelectTag = ""
            currentPage = 1
            recipeViewModel.getRecipes(page = currentPage, searchString = searchString, by = sortedBy)
            return
        }

        activateTag(tag)
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
        previousSelectTag = tag
    }

    private fun deactivateTag(tag: String) {
        val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tvToUpdate: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ibToUpdate: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton

        clToUpdate?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            elevation = 0f
        }

        tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))
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
        private var sortedBy: String = RecipesSortingType.VERIFIED


        object SelectedTab {
            const val VERIFIED = "1"
            const val ALL = "2"
            const val SUGGESTIONS = "3"
            const val MOST_SAVED = "4"
            const val MOST_LIKED = "4"
            const val MOST_RECENT = "4"
            const val RANDOM = "4"
            const val PERSONALIZED_SUGGESTIONS = "5"
        }
    }

}