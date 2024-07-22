package com.example.projectfoodmanager.presentation.favorites

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
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
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFavoritesBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.FragmentRecipeLikesChipsTag.COMMENTED
import com.example.projectfoodmanager.util.FragmentRecipeLikesChipsTag.LAST_SEEN
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    // binding
    lateinit var binding: FragmentFavoritesBinding

    // viewModels
    private val userViewModel: UserViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    // constants
    val TAG: String = "FavoritesFragmentFragment"
    private var isFirstTimeCall = true
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private var scrollListener: RecyclerView.OnScrollListener? = null

    private var stringToSearch: String? = null
    private var newSearch: Boolean = false
    private var onlineChipFilter: Boolean = false

    private var searchMode: Boolean = false
    lateinit var user: User
    private var oldFilterTag: String =""

        // pagination
    private var refreshPage: Int = 0
    private var currentPage: Int = 0
    private var nextPage:Boolean = true
    private var noMoreRecipesMessagePresented = false


    // chip filter

    private var firstSelectedChip: String? = null
    private lateinit var chipSelected: Chip
    private var filteredTag: String? = null


    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    // adapters
    private val adapter by lazy {
        FavoritesRecipeListingAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_favoritesFragment_to_receitaDetailFragment,Bundle().apply {
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
            userViewModel,
            recipeViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        firstSelectedChip= if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            arguments?.getString("chip")
        } else {
            arguments?.getString("chip")
        }

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentFavoritesBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            manager.reverseLayout = false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)


            //setRecyclerViewScrollListener()

            binding.root
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bindObservers()
        user = sharedPreference.getUserSession()
        //valida shared preferences


        try {

            if (user.likedRecipes.isNullOrEmpty()) {
                Log.d(TAG, "onViewCreated: user.saved_recipes is empty")
                //Mensagem sem receitas
                binding.tvNoRecipes.visibility = View.VISIBLE

            } else {
                //Mensagem com receitas
                binding.tvNoRecipes.visibility = View.GONE

                adapter.updateList(user.likedRecipes, user)
            }
        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: User had no shared prefences...")
            // se não tiver shared preferences o user não tem sessão válida
            //tera um comportamento diferente offilne
            userViewModel.logoutUser()
        }


        /**
         * Search filter
         */

        binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null && text != "") {

                    // debouncer
                    val handler = Handler()
                    handler.postDelayed({
                        if (stringToSearch == text) {


                            if (chipSelected.tag.toString().toInt() == COMMENTED){
                                recipeViewModel.getRecipesCommentedByUserPaginated(clientId = user.id, searchString = text)
                            }
                            else if (chipSelected.tag.toString().toInt() == LAST_SEEN){
                                toast("Not implemented yet")
                            }
                            else{
                                adapter.updateList(searchRecipes(getChipList(chipSelected),text))
                            }
                        }
                    }, 400)

                    stringToSearch=text

                } // se já fez pesquisa, limpou o texto e text vazio ( stringToSearch != null)
                else if (stringToSearch != null && text == ""){
                    updateView(chipSelected)
                }
                else{
                    stringToSearch=null
                }
                return true
            }
        })

        /**
         * Chip filters
         */

        val chipGroup: ChipGroup = binding.chipGroup
        chipSelected = chipGroup.findViewById(chipGroup.checkedChipId)
        if (firstSelectedChip != null){
            chipSelected.isChecked =false
            chipSelected = chipGroup.selectChipByTag(getString(R.string.tab_created))!!

            chipSelected.isChecked = true
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedId ->

            if (checkedId.isNotEmpty()) {
                group.findViewById<Chip>(checkedId[0])?.let {
                    filteredTag?.let { tag -> filterOnClick(tag) }
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
         * Bottom Add Recipe
         */

        // todo
        binding.addRecipeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_newRecipeFragment)
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

        binding.recyclerView.adapter = adapter

        // coisas que só faz online

        if (isOnline(view.context)) {

            // todo check if recipes removed from sharedPreferences ( in case user in offline mode removes like)
            // caso sim atualizar a bd

            recipeViewModel.getUserLikedRecipes()


            //validação da shared preferences feita no observer



            // todo atualiza a lista de comments mediante http://{{dev}}/api/v1/recipe/comments
            //authViewModel.getUserBackgrounds()


        } else {
            // TODO offline mode

            toast("Está offline")



        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Helper.changeTheme(false, activity, context)
        super.onStart()
    }

    private fun updateView(currentTabSelected: View) {
        changeRecyclerViewScrollListener(false)
        val recipes = getChipList(currentTabSelected)

        binding.addRecipeBtn.visibility = View.GONE

        when(currentTabSelected){
            binding.chipCurtidas -> {

                onlineChipFilter = false

                //list
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_liked)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)

            }
            binding.chipGuardados ->{

                onlineChipFilter = false

                //list
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_saved)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)
            }
            binding.chipCriadas ->{

                onlineChipFilter = false

                binding.addRecipeBtn.visibility = View.VISIBLE

                //list
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_created)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)

            }
            binding.chipCommented ->{

                onlineChipFilter = true
                changeRecyclerViewScrollListener(true)
            }
            binding.chipLastSeem ->{
                // todo
                return

                onlineChipFilter = true

                adapter.updateList(recipes, user)

                changeRecyclerViewScrollListener(true)
            }
        }
    }

    private fun changeRecyclerViewScrollListener(state: Boolean) {
        if (scrollListener == null){
            scrollListener = object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // prevent missed calls to api // needs to be reseted on search so it could be a next page

                        if (nextPage){
                            changeRecyclerViewScrollListener(false)
                            noMoreRecipesMessagePresented = false

                            val pastVisibleItem: Int =
                                manager.findLastCompletelyVisibleItemPosition()

                            if ((pastVisibleItem + 1) >= adapter.list.size){


                                if (stringToSearch.isNullOrEmpty()) {
                                    recipeViewModel.getRecipesCommentedByUserPaginated(page = ++currentPage, clientId = user.id)
                                } else {
                                    recipeViewModel.getRecipesCommentedByUserPaginated(page = ++currentPage, clientId = user.id, searchString = stringToSearch)
                                }
                            }

                            Log.d(TAG, pastVisibleItem.toString())
                            changeRecyclerViewScrollListener(true)
                        }
                        else if (!noMoreRecipesMessagePresented){
                            changeRecyclerViewScrollListener(false)
                            noMoreRecipesMessagePresented = true
                            toast("Sorry cant find more recipes.",ToastType.ALERT)
                        }



                    }

                    super.onScrollStateChanged(recyclerView, newState)

                }
            }
        }


        if (state)
            binding.recyclerView.addOnScrollListener(scrollListener!!)
        else
            binding.recyclerView.removeOnScrollListener(scrollListener!!)

    }

    private fun getChipList(currentTabSelected: View): MutableList<Recipe> {
        when(currentTabSelected){
            binding.chipCurtidas -> {
                return user.createdRecipes
            }
            binding.chipGuardados ->{
                return user.savedRecipes
            }
            binding.chipCriadas ->{
                return user.createdRecipes
            }
            binding.chipCommented ->{
                recipeViewModel.getRecipesCommentedByUserPaginated(clientId = user.id)

            }
            binding.chipLastSeem ->{
                // todo not implemented
                return mutableListOf()
            }
        }
        return mutableListOf()
    }

    private fun showValidationErrors(error: String) {
        toast(error)
        Log.d(TAG, "bindObservers: $error.")

    }

    private fun bindObservers() {

        recipeViewModel.recipesCommentedByUserSearchPaginated.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        // check if list empty

                        if(it.data!!.result.isEmpty()){
                            binding.tvNoRecipes.text = getString(R.string.no_recipes_comented)
                            binding.tvNoRecipes.visibility = View.VISIBLE

                            return@let
                        }else{
                            binding.tvNoRecipes.visibility=View.GONE
                        }

                        // sets page data

                        currentPage = it.data._metadata.page
                        nextPage = it.data._metadata.next != null

                        // updates recipe list

                        if (adapter.list.isNotEmpty() && currentPage == 1){
                            adapter.updateList(it.data.result)
                        }
                        else{
                            adapter.concatList(it.data.result)
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
        }

        recipeViewModel.recipesCommentedByUserPaginated.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()

                        // check if list empty

                        if(it.data!!.result.isEmpty()){
                            binding.tvNoRecipes.text = getString(R.string.no_recipes_comented)
                            binding.tvNoRecipes.visibility = View.VISIBLE
                            adapter.updateList(mutableListOf())
                            return@let
                        }else{
                            binding.tvNoRecipes.visibility=View.GONE
                        }

                        // sets page data

                        currentPage = it.data._metadata.page
                        nextPage = it.data._metadata.next != null

                        // updates recipe list

                        if (adapter.list.isNotEmpty() && currentPage == 1){
                            adapter.updateList(it.data.result)
                        }
                        else{
                            adapter.concatList(it.data.result)
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
        }

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
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_liked))

                        val listOnAdapter = adapter.getAdapterList()
                        // updates local list
                        for (item in listOnAdapter) {
                            if (item.id == it.data) {
                                item.likes++
                                adapter.updateItem(
                                    listOnAdapter.indexOf(item),
                                    item,
                                    sharedPreference.addRecipeToLikedList(item)
                                )
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
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_liked))

                        val listOnAdapter = adapter.getAdapterList()


                        // updates local list
                        for (item in listOnAdapter.toMutableList()) {
                            if (item.id == it.data) {
                                item.likes--
                                adapter.updateItem(
                                    listOnAdapter.indexOf(item),
                                    item,
                                    sharedPreference.removeRecipeFromLikedList(item)
                                )
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
        }

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_saved))

                        val listOnAdapter = adapter.getAdapterList()

                        // updates local list
                        for (item in listOnAdapter) {
                            if (item.id == it.data) {
                                adapter.updateItem(listOnAdapter.indexOf(item), item, sharedPreference.addSaveToUserSession(item))
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
        }

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_from_saves))

                        val listOnAdapter = adapter.getAdapterList()

                        // updates local list
                        for (item in listOnAdapter) {
                            if (item.id == it.data) {
                                adapter.updateItem(listOnAdapter.indexOf(item), item, sharedPreference.removeSaveFromUserSession(item))
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
        }

        // comments function

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_from_saves))

                        val listOnAdapter = adapter.getAdapterList()

                        // updates local list
                        for (item in listOnAdapter) {
                            if (item.id == it.data) {
                                adapter.updateItem(listOnAdapter.indexOf(item), item, sharedPreference.removeSaveFromUserSession(item))
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
        }

    }

    private fun changeFilterSearch(tag: String){
        if (onlineChipFilter){
            if (filteredTag == tag){
                filteredTag=null
                updateView(chipSelected)
            }
            else{
                filteredTag=tag
            }
        }
        else{
            if (filteredTag == tag){
                filteredTag=null
                updateView(chipSelected)
            }
            else{
                filteredTag=tag
                adapter.updateList(filterRecipes(getChipList(chipSelected),tag))
            }
        }
    }

    private fun filterOnClick(tag: String) {
        // Find views related to the oldFilterTag
        val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(oldFilterTag + "CL") as? ConstraintLayout
        val tvToUpdate: TextView? = binding.root.findViewWithTag(oldFilterTag + "TV") as? TextView
        val ibToUpdate: ImageButton? = binding.root.findViewWithTag(oldFilterTag + "_filt_IB") as? ImageButton

        // Update the oldFilterTag views if they exist
        clToUpdate?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            elevation = 0f
        }

        tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))
        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))

        // If the same tag is clicked again, clear the oldFilterTag and reload recipes
        if (oldFilterTag == tag) {
            oldFilterTag = ""
            recipeViewModel.getRecipes()
            // TODO: Rafa-> No recipe listing acrescentou se isto, mas aqui como esta diferente não sei
            // currentPage = 1
            return
        }

        // Update the new filter tag
        oldFilterTag = tag

        // Find views related to the new filter tag
        val cl: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tv: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ib: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton

        // Update the new filter tag views if they exist
        cl?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            elevation = 3f
        }

        tv?.setTextColor(resources.getColor(R.color.white))

        ib?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_1))
    }

    private fun filterRecipes(recipeList: MutableList<Recipe>, string: String): MutableList<Recipe>{
        val filteredList = mutableListOf<Recipe>()
        for (recipe in recipeList)
            for (tag in recipe.tags)
                if (tag.title.lowercase() == string){
                    filteredList.add(recipe)
                    break
                }
        return filteredList
    }

    private fun searchRecipes(recipeList: MutableList<Recipe>, string: String): MutableList<Recipe>{
        val filteredList = mutableListOf<Recipe>()
        for (recipe in recipeList){
            if (recipe.title.lowercase().contains(string.lowercase()) ||recipe.id.toString().contains(string.lowercase())){
                filteredList.add(recipe)
                continue
            }


            for (tag in recipe.tags)
                if (
                    tag.title.lowercase().contains(string.lowercase())){
                    filteredList.add(recipe)
                    break
                }
        }

        return filteredList
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

    override fun onResume() {
        updateView(chipSelected)
        changeMenuVisibility(true,requireActivity())
        super.onResume()
    }
}

