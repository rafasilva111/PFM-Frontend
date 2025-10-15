package com.example.projectfoodmanager.presentation.recipe.details


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
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
                if (imagesLoaded>= DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD) {
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

    override fun onPause() {
        super.onPause()
        //destroy variables
        userPortion = -1F
        recipePortion = -1F
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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


    /**
     *  General
     * */


    private fun setUI() {

        /**
         *  General
         * */


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

        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

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


        // Like function
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

        // save function

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

        // comments function

        recipeViewModel.functionGetComments.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        if (result.data!!.result.size == 1)
                            binding.commentsTV.text = getString(R.string.FRAGMENT_RECIPE_DETAIL_NR_COMMENT, result.data.result.size);
                        else
                            binding.commentsTV.text = getString(R.string.FRAGMENT_RECIPE_DETAIL_NR_COMMENTS, result.data.result.size);


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
    }

    /**
     *  Functions
     * */

    private fun updateRecipeUI(recipe: Recipe) {

        /** Recipe Info */

        loadRecipeImage(binding.IVRecipe, recipe.imgSource){
            onImageLoaded()
        }

        binding.TVRef.text = recipe.id.toString()
        binding.dateTV.text = formatServerTimeToDateString(recipe.createdDate)
        binding.titleTV.text = recipe.title
        binding.ratingMedTV.text = recipe.sourceRating.toString()
        binding.numberLikeTV.text = recipe.likes.toString()

        binding.dificultyTV.text = recipe.difficulty

        //--> Times
        binding.timeTV.text = if (recipe.time > 60) {
            "${recipe.time / 60}h ${recipe.time % 60}m"
        } else {
            "${recipe.time}m"
        }

        //--> RATING
        binding.ratingRecipeRB.rating = recipe.sourceRating.toFloat()
        binding.ratingMedTV.text = recipe.sourceRating.toString()


        binding.portionTV.text = if (recipe.portionUpper == null) {
            "${recipe.portionLower} ${recipe.portionUnits}"
        } else {
            "${recipe.portionLower}-${recipe.portionUpper} ${recipe.portionUnits}"
        }

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

        /** User Info */

        loadUserImage(binding.imageAuthorIV, recipe.createdBy.imgSource){
            onImageLoaded()
        }

        binding.nameAuthorTV.text = formatNameToNameUpper(recipe.createdBy.name)

        if (recipe.createdBy.verified) {
            binding.verifyUserIV.visibility = View.VISIBLE
        } else {
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        // likes
        updateLikeUI(recipe)


        binding.likeIB.setOnClickListener {
            if (recipe.liked)
                recipeViewModel.removeLikeOnRecipe(recipe.id)
            else
                recipeViewModel.addLikeOnRecipe(recipe.id)


        }

        // Favorites
        updateSaveUI(recipe)


        binding.favoritesIB.setOnClickListener {
            if (recipe.saved)
                recipeViewModel.removeSaveOnRecipe(recipe.id)
            else
                recipeViewModel.addSaveOnRecipe(recipe.id)


        }


        /**
         *  Navigation
         * */

        // go to Comments
        binding.commentsCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment, Bundle().apply {
                putInt("recipe_id", recipe.id)
                putInt("user_id", recipe.id)
            })
        }

        // go to creator profile
        binding.profileAuthorCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_profileFragment, Bundle().apply {
                putInt("user_id", recipe.createdBy.id)
            })
        }

        // go to create calender entry
        binding.calenderIB.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_newCalenderEntryFragment, Bundle().apply {
                putParcelable("Recipe", recipe)
            })
        }

        /**
         *  Tab Layout
         * */

        // enables back from comments
        binding.fragmentRecipeDetailViewPager.isSaveEnabled = false

        if (binding.fragmentRecipeDetailTabLayout.tabCount != 2) {
            binding.fragmentRecipeDetailTabLayout.addTab(binding.fragmentRecipeDetailTabLayout.newTab().setText("Recipe"))
            binding.fragmentRecipeDetailTabLayout.addTab(binding.fragmentRecipeDetailTabLayout.newTab().setText("Nutrition"))
        }

        binding.fragmentRecipeDetailViewPager.adapter = RecipeDetailTabAdapter(requireActivity().supportFragmentManager, lifecycle, recipe)

        binding.fragmentRecipeDetailTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null)
                    binding.fragmentRecipeDetailViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })


        binding.fragmentRecipeDetailViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Delay the view measurement to ensure it's properly laid out
                binding.fragmentRecipeDetailViewPager.post {

                    // Get the RecyclerView inside ViewPager2
                    val recyclerView = binding.fragmentRecipeDetailViewPager.getChildAt(0) as RecyclerView

                    // Get the ViewHolder for the current position
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    val view = viewHolder?.itemView

                    // Update TabLayout selection
                    binding.fragmentRecipeDetailTabLayout.selectTab(binding.fragmentRecipeDetailTabLayout.getTabAt(position))

                    // Measure and adjust ViewPager2 height
                    view?.let {
                        // Measure the view
                        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(it.width, View.MeasureSpec.EXACTLY)
                        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        it.measure(wMeasureSpec, hMeasureSpec)

                        // Adjust the height of ViewPager2 if necessary
                        if (binding.fragmentRecipeDetailViewPager.layoutParams.height != it.measuredHeight) {
                            binding.fragmentRecipeDetailViewPager.layoutParams = binding.fragmentRecipeDetailViewPager.layoutParams.apply {
                                height = it.measuredHeight
                            }
                        }
                    }
                }
            }
        })

    }

    private fun updateLikeUI(recipe: Recipe) {

        binding.numberLikeTV.text = recipe.likes.toString()

        if (recipe.liked) {
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        } else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)

    }

    private fun updateSaveUI(recipe: Recipe) {

        if (recipe.saved)
            binding.favoritesIB.setImageResource(R.drawable.ic_saved_active)
        else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

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
