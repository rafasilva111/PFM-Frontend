package com.example.projectfoodmanager.presentation.recipe.details


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.recipe.rating.RecipeRatingRequest
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.rating.RecipeRating
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.Helper.Companion.enableEdgeToEdge
import com.example.projectfoodmanager.util.Helper.Companion.restoreViewLimits
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RecipeDetailFragment : Fragment(), ImageLoadingListener {


    /** Binding */
    lateinit var binding: FragmentRecipeDetailBinding

    /** ViewModels */
    private val recipeViewModel: RecipeViewModel by viewModels()

    /** Constants */
    val TAG: String = "ReceitaDetailFragment"
    private var recipeId: Int = -1
    private var imagesLoaded: Int = 0

    // RecyclerView
    lateinit var manager: LinearLayoutManager

    /** Injections */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Interfaces */
    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            if (binding.mainView.visibility != View.VISIBLE) {
                imagesLoaded++

                // If all visible images are loaded, hide the progress bar
                if (imagesLoaded >= DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD) {
                    showRecyclerView()
                }
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

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            // Inflate the layout for this fragment
            binding = FragmentRecipeDetailBinding.inflate(layoutInflater)

            binding.root
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         *  Arguments
         * */

        arguments?.let {

            recipeId = arguments?.getInt("recipe_id")!!

            userPortion = arguments?.getFloat("user_portion", -1F)!!

            // todo rafa isto deve ser tratado na base de dados ps: não está
            /* recipePortion = if (objRecipe!!.portion.lowercase().contains("pessoas"))
                 objRecipe!!.portion.split(" ")[0].toFloat()
             else
             -1F*/
        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isOnline(requireView().context)) {

            recipeViewModel.getRecipe(id = recipeId)
            // selecionar as 2/3 primeiras imagens para colocar na zona dos comments
            recipeViewModel.getCommentsByRecipe(recipeId, pageSize = 2)

        }

        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        //destroy variables
        userPortion = -1F
        recipePortion = -1F

        /** Restore Status Bar and Navigation Bar view limits */
        restoreViewLimits(requireActivity(), requireContext())
    }


    /**
     *  General
     * */


    private fun setUI() {

        /**
         *  General
         * */

        /** Remove Status Bar and Navigation Bar view limits */
        enableEdgeToEdge(requireActivity())

        /**
         *  Navigations
         * */

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun loadUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeTheme(false, activity, requireContext())

    }

    private fun bindObservers() {

        /**
         * Get Recipe
         */

        recipeViewModel.functionGetRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {


                        result.data?.let { updateRecipeUI(it) }

                    }

                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }

                    is NetworkResult.Loading -> {
                    }
                }
            }
        }


        /** Like function */
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        result.data?.let { updateLikeUI(it) }

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


                        result.data?.let { updateLikeUI(it) }
                    }

                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }

                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /** Save function */
        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {


                        result.data?.let { updateSaveUI(it) }


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
                        result.data?.let { updateSaveUI(it) }

                    }

                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }

                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /** Comments function */
        recipeViewModel.functionGetComments.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        if (result.data!!.result.size == 1)
                            binding.commentsTV.text = getString(
                                R.string.FRAGMENT_RECIPE_DETAIL_NR_COMMENT,
                                result.data.result.size
                            );
                        else
                            binding.commentsTV.text = getString(
                                R.string.FRAGMENT_RECIPE_DETAIL_NR_COMMENTS,
                                result.data.result.size
                            );


                        if (result.data.result.size > 0) {
                            result.data.result[0].user?.imgSource?.let { img ->
                                binding.userComent2IV.visibility = View.VISIBLE
                                loadUserImage(binding.userComent2IV, img)
                            }
                        }


                        if (result.data.result.size > 1) {
                            result.data.result[1].user?.imgSource?.let { img ->
                                binding.userComent1IV.visibility = View.VISIBLE
                                loadUserImage(binding.userComent1IV, img)
                            }
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

        /** Rate function */
        recipeViewModel.functionPostRecipeRating.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.data?.let { updateRateUI(it) }

                    }

                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
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

    private fun updateRecipeUI(recipe: Recipe) {

        /** Recipe Info */

        /** Loads the recipe image and triggers the image loaded callback */
        loadRecipeImage(binding.IVRecipe, recipe.imgSource) {
            onImageLoaded()
        }

        /** Sets recipe basic info */
        binding.TVRef.text = recipe.id.toString()
        binding.dateTV.text = formatServerTimeToDateString(recipe.createdDate)
        binding.titleTV.text = recipe.title
        binding.numberLikeTV.text = recipe.likes.toString()
        binding.dificultyTV.text = recipe.difficulty

        /** Sets recipe time */
        binding.timeTV.text = if (recipe.time > 60) {
            "${recipe.time / 60}h ${recipe.time % 60}m"
        } else {
            "${recipe.time}m"
        }

        /** Sets recipe rating */
        binding.ratingRecipeRB.rating = recipe.rating.toFloat()
        binding.ratingMedTV.text = recipe.rating.toString()

        /** Sets recipe portion info */
        binding.portionTV.text = if (recipe.portionUpper == null) {
            "${recipe.portionLower} ${recipe.portionUnits}"
        } else {
            "${recipe.portionLower}-${recipe.portionUpper} ${recipe.portionUnits}"
        }

        /** Sets difficulty icon */
        when (recipe.difficulty) {
            RecipeDifficultyConstants.LOW -> {
                binding.IV2.setImageResource(R.drawable.low_difficulty)
            }

            RecipeDifficultyConstants.MEDIUM -> {
                binding.IV2.setImageResource(R.drawable.medium_difficulty)
            }

            RecipeDifficultyConstants.HIGH -> {
                binding.IV2.setImageResource(R.drawable.high_difficulty)
            }
        }

        /** Loads author image and info */
        loadUserImage(binding.imageAuthorIV, recipe.createdBy.imgSource) {
            onImageLoaded()
        }
        binding.nameAuthorTV.text = formatNameToNameUpper(recipe.createdBy.name)
        binding.verifyUserIV.visibility =
            if (recipe.createdBy.verified) View.VISIBLE else View.INVISIBLE

        /** Handles like button UI and logic */
        updateLikeUI(recipe)
        binding.likeIB.setOnClickListener {
            if (recipe.liked) {
                recipeViewModel.removeLikeOnRecipe(recipe.id)
            } else {
                recipeViewModel.addLikeOnRecipe(recipe.id)
            }
        }

        /** Handles save button UI and logic */
        updateSaveUI(recipe)
        binding.favoritesIB.setOnClickListener {
            if (recipe.saved) {
                recipeViewModel.removeSaveOnRecipe(recipe.id)
            } else {
                recipeViewModel.addSaveOnRecipe(recipe.id)
            }
        }

        /** Handles rating dialog and logic */
        var selectedRating = recipe.rated ?: 0f
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Rate this recipe")
            .setView(layoutInflater.inflate(R.layout.dialog_rate_recipe, null))
            .setPositiveButton("Submit") { _, _ ->
                recipeViewModel.postRecipeRating(
                    recipeId,
                    RecipeRatingRequest(rating = selectedRating)
                )
            }
            .setNegativeButton("Cancel", null)
            .create()

        binding.recipeRatingLL.setOnClickListener {
            dialog.show()
        }

        /** Navigation actions */

        /** Navigates to comments fragment */
        binding.commentsCV.setOnClickListener {
            findNavController().navigate(
                R.id.action_receitaDetailFragment_to_receitaCommentsFragment,
                Bundle().apply {
                    putInt("recipe_id", recipe.id)
                    putInt("user_id", recipe.id)
                }
            )
        }

        /** Navigates to author profile fragment */
        binding.profileAuthorCV.setOnClickListener {
            findNavController().navigate(
                R.id.action_receitaDetailFragment_to_profileFragment,
                Bundle().apply {
                    putInt("user_id", recipe.createdBy.id)
                }
            )
        }

        /** Navigates to calendar entry creation fragment */
        binding.calenderIB.setOnClickListener {
            findNavController().navigate(
                R.id.action_receitaDetailFragment_to_newCalenderEntryFragment,
                Bundle().apply {
                    putParcelable("Recipe", recipe)
                }
            )
        }

        /** Tab Layout setup */

        /** Disables ViewPager2 state saving for back navigation from comments */
        binding.fragmentRecipeDetailViewPager.isSaveEnabled = false

        /** Adds tabs if not present */
        if (binding.fragmentRecipeDetailTabLayout.tabCount != 2) {
            binding.fragmentRecipeDetailTabLayout.addTab(
                binding.fragmentRecipeDetailTabLayout.newTab().setText("Recipe")
            )
            binding.fragmentRecipeDetailTabLayout.addTab(
                binding.fragmentRecipeDetailTabLayout.newTab().setText("Nutrition")
            )
        }

        /** Sets up ViewPager2 adapter */
        binding.fragmentRecipeDetailViewPager.adapter =
            RecipeDetailTabAdapter(requireActivity().supportFragmentManager, lifecycle, recipe)

        /** Handles tab selection and syncs with ViewPager2 */
        binding.fragmentRecipeDetailTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.fragmentRecipeDetailViewPager.currentItem = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        /** Adjusts ViewPager2 height based on selected tab content */
        binding.fragmentRecipeDetailViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.fragmentRecipeDetailViewPager.post {
                    val recyclerView =
                        binding.fragmentRecipeDetailViewPager.getChildAt(0) as RecyclerView
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    val view = viewHolder?.itemView

                    binding.fragmentRecipeDetailTabLayout.selectTab(
                        binding.fragmentRecipeDetailTabLayout.getTabAt(position)
                    )

                    view?.let {
                        val wMeasureSpec =
                            View.MeasureSpec.makeMeasureSpec(it.width, View.MeasureSpec.EXACTLY)
                        val hMeasureSpec =
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        it.measure(wMeasureSpec, hMeasureSpec)

                        if (binding.fragmentRecipeDetailViewPager.layoutParams.height != it.measuredHeight) {
                            binding.fragmentRecipeDetailViewPager.layoutParams =
                                binding.fragmentRecipeDetailViewPager.layoutParams.apply {
                                    height = it.measuredHeight
                                }
                        }
                    }
                }
            }
        })

    }

    /**
     * UI Update
     */

    private fun updateLikeUI(recipe: Recipe) {

        binding.numberLikeTV.text = recipe.likes.toString()

        if (recipe.liked) {
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        } else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)

    }

    private fun updateSaveUI(recipe: Recipe) {

        if (recipe.saved)
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

    }

    private fun updateRateUI(recipeRating: RecipeRating) {
        binding.ratingRecipeRB.rating = recipeRating.rating
        binding.ratingMedTV.text =
            getString(R.string.FRAGMENT_RECIPE_DETAIL_NR_COMMENTS, recipeRating.rating)
    }

    private fun showValidationErrors(toString: String) {
        Log.d(TAG, "showValidationErrors: " + toString)
    }

    private fun showRecyclerView() {
        binding.fragmentSplash.visibility = View.INVISIBLE
        binding.mainView.visibility = View.VISIBLE
    }

    private fun hideRecyclerView() {
        binding.fragmentSplash.visibility = View.VISIBLE
        binding.mainView.visibility = View.INVISIBLE
    }

    /**
     *  Object
     * */

    companion object {
        var userPortion: Float = -1F
        var recipePortion: Float = -1F
    }


}
