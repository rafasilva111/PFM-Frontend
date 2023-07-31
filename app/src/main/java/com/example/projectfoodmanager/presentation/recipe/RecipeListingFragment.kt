package com.example.projectfoodmanager.presentation.recipe

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private var oldFiltTag: String =""
    lateinit var binding: FragmentRecipeListingBinding
    private val recipeViewModel by activityViewModels<RecipeViewModel>()

    // chips filter

    private var chipSelected: String? = null


    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->
                // use pos to reset current page to pos page, so it will refresh the pos page
                refreshPage =  ceil((pos+1).toFloat()/PaginationNumber.DEFAULT).toInt()
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

                            if (chipSelected != null){
                                recipeViewModel.getRecipesPaginatedSorted(++currentPage,chipSelected!!)
                            }
                            else if (stringToSearch.isNullOrEmpty()) {
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

        binding.tvName.text =  getString(R.string.full_name, user.name)

        //VIP HEADER
        if(user.user_type != "V"){
            binding.profileCV.foreground=null
            binding.vipIV.visibility=View.INVISIBLE
        }

        //VERIFIED HEADER
        if(user.verified)
            binding.verifyUserHeaderIV.visibility=View.VISIBLE


        if (user.img_source.contains("avatar")){
            val avatar= Avatar.getAvatarByName(user.img_source)
            binding.ivProfilePic.setImageResource(avatar!!.imgId)

        }else{
            val imgRef = Firebase.storage.reference.child("${FireStorage.user_profile_images}${user.img_source}")
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                Glide.with(binding.ivProfilePic.context).load(Uri.toString()).into(binding.ivProfilePic)
            }

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



            //filtros mas com as chipViews
            val chipGroup: ChipGroup = binding.chipGroup
            chipGroup.setOnCheckedStateChangeListener { group, checkedId ->
                if (checkedId.isNotEmpty())
                    group.findViewById<Chip>(checkedId[0])?.let { updateView(it) }
            }


            //nav search bottom

            binding.meatFiltIB.setOnClickListener {

                changeFilterSearch(RecipeListingFragmentFilters.CARNE)
                filterOnClick("meat")

            }
            binding.fishFiltIB.setOnClickListener {
                changeFilterSearch(RecipeListingFragmentFilters.PEIXE)
                filterOnClick("fish")
            }

            binding.soupFiltIB.setOnClickListener {
                changeFilterSearch(RecipeListingFragmentFilters.SOPA)
                filterOnClick("soup")
            }
            binding.vegiFiltIB.setOnClickListener {
                changeFilterSearch(RecipeListingFragmentFilters.VEGETARIANA)
                filterOnClick("vegi")
            }
            binding.fruitFiltIB.setOnClickListener {
                changeFilterSearch(RecipeListingFragmentFilters.FRUTA)
                filterOnClick("fruit")
            }
            binding.drinkFiltIB.setOnClickListener {
                changeFilterSearch(RecipeListingFragmentFilters.BEBIDAS)
                filterOnClick("drink")
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


    private fun filterOnClick(tag:String){

        val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(oldFiltTag + "CL") as? ConstraintLayout
        val tvToUpdate: TextView? = binding.root.findViewWithTag(oldFiltTag + "TV") as? TextView
        val ibToUpdate: ImageButton? = binding.root.findViewWithTag(oldFiltTag + "_filt_IB") as? ImageButton

        clToUpdate?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            elevation = 0f
        }

        tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))

        if (oldFiltTag == tag) {
            oldFiltTag = ""
            recipeViewModel.getRecipesPaginated(1)
            currentPage = 1
            return
        }

        oldFiltTag = tag

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
        recipeViewModel.recipeResponseLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                searchMode = false
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

                            // check if list empty

                            if(it.data!!.result.isEmpty()){
                                //TODO: Não funciona com os filtros
                                binding.offlineTV.text = getString(R.string.no_recipes_found)
                                binding.offlineTV.visibility=View.VISIBLE
                                return@let
                            }else{
                                binding.offlineTV.visibility=View.GONE

                            }

                            // sets page data

                            currentPage = it.data!!._metadata.current_page
                            nextPage = it.data._metadata.next != null

                            // checks if new search

                            if (recipeList.isNotEmpty() && currentPage == 1){
                                recipeList = it.data.result
                            }
                            else{
                                recipeList += it.data.result
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


        // chips filter

        recipeViewModel.recipeSortedResponseLiveData.observe(viewLifecycleOwner
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

                            // check if list empty

                            if(it.data!!.result.isEmpty()){
                                binding.offlineTV.text = getString(R.string.no_recipes_found)
                                binding.offlineTV.visibility=View.VISIBLE
                                return@let
                            }else{
                                binding.offlineTV.visibility=View.GONE

                            }

                            // sets page data

                            currentPage = it.data._metadata.current_page
                            nextPage = it.data._metadata.next != null

                            // checks if new search

                            if (recipeList.isNotEmpty() && currentPage == 1){
                                recipeList = it.data.result
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


        // Like function


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

        // save function

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

    }
    private fun changeFilterSearch(string: String){
        if (stringToSearch == string){
            newSearch = false
            stringToSearch=""
            recipeViewModel.getRecipesPaginated()
        }
        else{
            newSearch = true
            stringToSearch=string
            recipeViewModel.getRecipesByTitleAndTags(string)
        }
    }

    private fun updateView(currentTabSelected: View) {

        when(currentTabSelected){
            binding.recipeListingFilterRecent -> {
                // recent
                if (chipSelected!=null && chipSelected== RecipesSortingType.DATE){
                    chipSelected = null
                    recipeViewModel.getRecipesPaginated()
                }
                else{
                    chipSelected = RecipesSortingType.DATE
                    recipeViewModel.getRecipesPaginatedSorted(by = RecipesSortingType.DATE)
                }



            }
            binding.recipeListingFilterSugestions -> {
                // gostos
                // todo
            }
            binding.recipeListingFilterPersonalizedSugestions -> {
                // gostos
                // todo
            }
            binding.random -> {
                // random
                if (chipSelected!=null && chipSelected== RecipesSortingType.RANDOM){
                    chipSelected = null
                    recipeViewModel.getRecipesPaginated()
                }
                else{
                    chipSelected = RecipesSortingType.RANDOM
                    recipeViewModel.getRecipesPaginatedSorted(by = RecipesSortingType.RANDOM)
                }
            }
            binding.mostLiked -> {
                // gostos

                if (chipSelected!=null && chipSelected== RecipesSortingType.LIKES){
                    chipSelected = null
                    recipeViewModel.getRecipesPaginated()
                }
                else{
                    chipSelected = RecipesSortingType.LIKES
                    recipeViewModel.getRecipesPaginatedSorted(by = RecipesSortingType.LIKES)
                }
            }
            binding.mostSaved -> {
                // saves
                if (chipSelected!=null && chipSelected== RecipesSortingType.SAVES){
                    chipSelected = null
                    recipeViewModel.getRecipesPaginated()
                }
                else{
                    chipSelected = RecipesSortingType.SAVES
                    recipeViewModel.getRecipesPaginatedSorted(by = RecipesSortingType.SAVES)
                }
            }
        }
    }

    private fun changeVisibilityMenu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

}