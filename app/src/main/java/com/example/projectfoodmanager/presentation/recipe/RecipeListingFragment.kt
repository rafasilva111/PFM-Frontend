package com.example.projectfoodmanager.presentation.recipe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.toRecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.user.UserType
import com.example.projectfoodmanager.data.model.notification.Notification
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
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RecipeListingFragment : Fragment(), ImageLoadingListener {



    /** Binding */
    lateinit var binding: FragmentRecipeListingBinding

    /** ViewModels */
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    /** Constants */
    private val TAG: String = "RecipeListingFragment"

    /** Layout Manager & Preloader */
    private var snapHelper : SnapHelper = PagerSnapHelper()
    private lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var preloadModelProvider: RecipePreloadModelProvider

    /** Pagination */
    private var noMoreRecipesMessagePresented = false

    /** Chip Filters */
    private var selectedTab: String = SelectedTab.VERIFIED
    private lateinit var chipSelected: Chip

    /** Tag Filters */
    private var previousSelectTag: String =""

    /** Search Debouncer */
    private var debounceJob: Job? = null

    /** Notifications */
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

                currentPosition =  pos


                findNavController().navigate(R.id.action_recipeListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putInt("recipe_id",recipe.id)
                })
                binding.progressBar.show()
                binding.recyclerView.visibility = View.GONE

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
            if (binding.recyclerView.visibility != View.VISIBLE && binding.recyclerView.visibility != View.GONE) {
                adapter.imagesLoaded++

                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
                val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1

                // If all visible images are loaded, hide the progress bar
                if (adapter.imagesLoaded >= visibleItemCount * DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD) {
                    showRecyclerView()
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Android Lifecycle
    // -----------------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindObservers()

        /** Inflate binding */
        binding = FragmentRecipeListingBinding.inflate(inflater, container, false)

        /** Set up RecyclerView LayoutManager */
        manager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            reverseLayout = false
        }
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.itemAnimator = null
        snapHelper.attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /** Setup UI and bind observers */
        setUI()
        bindObservers()
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

        /** Load data into UI */
        loadUI()
        super.onStart()
    }

    override fun onResume() {

        /** Register the broadcast receiver */
        context?.registerReceiver(notificationReceiver, IntentFilter(MyFirebaseMessagingService.ACTION_NOTIFICATION_RECEIVED))
        super.onResume()
    }

    override fun onPause() {

        /** Unregister the broadcast receiver to avoid memory leaks */
        context?.unregisterReceiver(notificationReceiver)
        super.onPause()
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 UI Setup
    // -----------------------------------------------------------------------------------------

    private fun loadUI() {

        /** General */
        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false,activity,requireContext())

        /** Check Internet Connection */
        if (!isOnline(requireContext())) {
            binding.offlineTV.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }else{

            /** Update Recipe List */
            if (currentPosition == -1)
                fetchRecipes()
            else{
                // If user is coming back from entering in a recipe, update the item in the list
                recipeViewModel.getRecipe(adapter.getItems()[currentPosition].id)
            }

            /** Update Notifications */
            userViewModel.getNotifications(pageSize = 1)
        }


    }


    private fun setUI() {

        /** General */
        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false,activity,requireContext())

        /** Set Adapters */
        binding.recyclerView.adapter = adapter

        setRecyclerViewScrollListener()

        /** Load User Info */
        val user = sharedPreference.getUserSession()

        /** Set User Info */
        binding.tvName.text = formatNameToNameUpper(getString(R.string.full_name, user.name))

        if (user.userType == UserType.PREMIUM) {
            binding.profileCV.foreground = getDrawable(requireContext(), R.drawable.border_vip)
            binding.vipIV.visibility = View.VISIBLE
        }

        if (user.verified)
            binding.verifyUserHeaderIV.visibility = View.VISIBLE

        loadUserImage(binding.ivProfilePic, user.imgSource)

        /**
         * Search filter
         */

        binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null && text != "") {
                    // Control Variables
                    currentPage = 1
                    searchString = text.lowercase()

                    // Cancel the previous debounce job if it exists
                    debounceJob?.cancel()

                    // Start a new debounce job
                    debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(DEBOUNCER_STRING_SEARCH)

                        // If user haven't change the search string for a while,
                        // then it's a new search
                        if (searchString == text) {
                            hideRecyclerView()
                            recipeViewModel.getRecipes(
                                page = currentPage,
                                searchString =searchString,
                                searchTag = searchTag,
                                by = sortedBy
                            )

                        }
                    }
                }  else if (searchString != "" && text == "") {
                    // If user searched for something and them cleaned the text

                    // Reset Control Variables
                    searchString = ""
                    currentPage = 1
                    itemsListed = mutableListOf() // TODO this needs to be reviewed
                    hideRecyclerView()


                    // Get recipes with empty searchString
                    recipeViewModel.getRecipes(
                        page = currentPage,
                        searchString = searchString,
                        searchTag = searchTag,
                        by = sortedBy
                    )
                } else {
                    // Reset Control Variables
                    searchString = ""
                }

                // Slowly move to position 0
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


        chipSelected = binding.chipGroup.selectChipByTag(selectedTab)!!

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedId ->

            if (checkedId.isNotEmpty()) {
                group.findViewById<Chip>(checkedId[0])?.let {
                    chipSelected.isChecked = false
                    chipSelected = it

                    selectedTab = chipSelected.tag as String
                    fetchRecipes()
                }
            } else {
                // If no chip is selected, select the last selected one
                chipSelected.isChecked = true
            }
        }


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


                        // Control Variables
                        currentPage = result.data!!._metadata.page
                        nextPage = result.data._metadata.nextPage != null

                        noMoreRecipesMessagePresented = nextPage

                        // Check if list empty
                        if(result.data.result.isEmpty()){
                            binding.progressBar.hide()
                            binding.noRecipesTV.visibility=View.VISIBLE
                            binding.recipeCardCL.visibility=View.INVISIBLE
                            return@let
                        }else{
                            binding.noRecipesTV.visibility=View.GONE
                            binding.recipeCardCL.visibility=View.VISIBLE

                        }

                        // Checks if new search
                        if (currentPage == 1){
                            adapter.setItems(result.data.result)
                            setItemsToImagePreload(result.data.result)
                        }
                        else{
                            adapter.addItems(result.data.result)
                        }



                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        binding.noRecipesTV.hide()
                        binding.offlineTV.hide()
                    }
                }
            }
        }

        recipeViewModel.functionGetRecipe.observe(viewLifecycleOwner
        ) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        result.data?.let {
                            adapter.updateItem(currentPosition,
                                it.toRecipeSimplified())
                        }

                        hideRecyclerView()
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        binding.noRecipesTV.visibility = View.GONE
                        binding.offlineTV.visibility = View.GONE
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



    // -----------------------------------------------------------------------------------------
    // 🔹 Core Logic
    // -----------------------------------------------------------------------------------------

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {

            // Track if a background prefetch is scheduled
            private var prefetchJob: Job? = null

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItem = manager.findLastVisibleItemPosition()
                val totalItemCount = adapter.itemCount

                // 🎯 Prefetch when the user is within the last 8 items
                if (lastVisibleItem >= totalItemCount - 8 && nextPage) {
                    nextPage = false // prevent duplicate calls
                    recipeViewModel.getRecipes(
                        page = ++currentPage,
                        searchString = searchString,
                        searchTag = searchTag,
                        by = sortedBy
                    )
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lastVisibleItem = manager.findLastVisibleItemPosition()
                val totalItemCount = adapter.itemCount

                // 💤 When user stops scrolling, schedule a background prefetch (like Instagram)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && nextPage) {
                    prefetchJob?.cancel()
                    prefetchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(1500) // 1.5s after user stops scrolling
                        if (lastVisibleItem >= totalItemCount - 10 && nextPage) {
                            recipeViewModel.getRecipes(
                                page = ++currentPage,
                                searchString = searchString,
                                searchTag = searchTag,
                                by = sortedBy
                            )
                            nextPage = false
                        }
                    }
                }

                // 📭 Show "no more recipes" message if user reaches the end
                if (lastVisibleItem == totalItemCount - 1 && !nextPage && !noMoreRecipesMessagePresented) {
                    noMoreRecipesMessagePresented = true
                    toast("Sorry, can't find more recipes.", ToastType.ALERT)
                }
            }
        }

        binding.recyclerView.apply {
            addOnScrollListener(scrollListener)
            setItemViewCacheSize(10)
        }

    }

    private fun fetchRecipes() {

        hideRecyclerView()

        when(selectedTab){
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
                binding.progressBar.hide()
                return
            }
            SelectedTab.PERSONALIZED_SUGGESTIONS-> {
                toast("Sorry, not implement yet...")
                binding.progressBar.hide()
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

        recipeViewModel.getRecipes(
            page = 1,
            searchString = searchString,
            searchTag = searchTag,
            by = sortedBy
        )

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

    private fun showRecyclerView() {
        binding.progressBar.hide()
        binding.recyclerView.visibility = View.VISIBLE
        // Reset the number of images loaded
        adapter.imagesLoaded = 0

        if ( currentPosition != -1) {
            binding.recyclerView.scrollToPosition(currentPosition)

            // Reset current position
            currentPosition = -1
        }
    }

    private fun hideRecyclerView() {
        binding.progressBar.show()
        // note: don't use hide() here, because hide sets the visibility to GONE, and then the preloader can't calculate the visible items
        binding.recyclerView.visibility = View.INVISIBLE
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Preloading Helpers
    // -----------------------------------------------------------------------------------------

    private fun setItemsToImagePreload(recipeList: MutableList<RecipeSimplified>) {
        if (! ::preloadModelProvider.isInitialized){
            initImagePreload(recipeList)
        }
        preloadModelProvider.setItems(recipeList)
    }

    private fun initImagePreload(recipeList: MutableList<RecipeSimplified>) {
        preloadModelProvider = RecipePreloadModelProvider(recipeList, requireContext())
        val preloader = RecyclerViewPreloader(
            Glide.with(this),
            preloadModelProvider,
            ViewPreloadSizeProvider(),
            10, /* maxPreload */
        )
        binding.recyclerView.addOnScrollListener(preloader)
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Filters Controls
    // -----------------------------------------------------------------------------------------

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

    private fun changeTagFilter(tag: String){

        // Alter Tag
        searchTag = if (searchTag == tag){
            ""
        }
        else{
            tag
        }

        // Update Recipe List
        fetchRecipes()

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

        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.tag_off_color))
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Companion Object
    // -----------------------------------------------------------------------------------------

    companion object {

        private var itemsListed: MutableList<RecipeSimplified> = mutableListOf()

        // pagination
        private var currentPage:Int = 1
        private var currentPosition:Int = -1
        private var nextPage:Boolean = true

        // Filters
        private var searchTag: String = ""
        private var searchString: String = ""
        private var sortedBy: String = RecipesSortingType.VERIFIED


        object SelectedTab {
            const val VERIFIED = "VERIFIED"
            const val ALL = "ALL"
            const val SUGGESTIONS = "SUGGESTIONS"
            const val MOST_SAVED = "MOST_SAVED"
            const val MOST_LIKED = "MOST_LIKED"
            const val MOST_RECENT = "MOST_RECENT"
            const val RANDOM = "RANDOM"
            const val PERSONALIZED_SUGGESTIONS = "PERSONALIZED_SUGGESTIONS"
        }
    }

}