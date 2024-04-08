package com.example.projectfoodmanager.presentation.recipe.details


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {


    // binding
    lateinit var binding: FragmentRecipeDetailBinding

    // viewModels
    private val recipeViewModel: RecipeViewModel by viewModels()

    // constants
    val TAG: String = "ReceitaDetailFragment"
    private var objRecipe: Recipe? = null
    private lateinit var user: User


    lateinit var manager: LinearLayoutManager

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference



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

           objRecipe = if (Build.VERSION.SDK_INT >= 33) {
               arguments?.getParcelable("Recipe", Recipe::class.java)
           } else {
               arguments?.getParcelable("Recipe")
           }

           userPortion = arguments?.getFloat("UserPortion",-1F)!!

            // todo rafa isto deve ser tratado na base de dados
           recipePortion = if (objRecipe!!.portion.lowercase().contains("pessoas"))
               objRecipe!!.portion.split(" ")[0].toFloat()
           else
               -1F
        }

        super.onCreate(savedInstanceState)
    }


    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {




        if (objRecipe != null) {
            if (isOnline(view.context)) {

                // selecionar as 2/3 primeiras imagens para colocar na zona dos comments
                recipeViewModel.getSizedCommentsByRecipePaginated(objRecipe!!.id, pageSize = 2)

/*
                binding.commentsFB.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        val badgeDrawable = BadgeDrawable.create(requireContext())
                        badgeDrawable.number = objRecipe!!.comments
                        badgeDrawable.horizontalOffset = 30
                        badgeDrawable.verticalOffset = 20

                        BadgeUtils.attachBadgeDrawable(
                            badgeDrawable,
                            binding.commentsFB,
                            null
                        )

                        // Remove the listener once it's no longer needed
                        binding.commentsFB.viewTreeObserver.removeOnGlobalLayoutListener(
                            this
                        )
                    }
                })*/
            }

            setUI(objRecipe!!)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }

    private fun setUI(recipe: Recipe) {

        /**
         *  General
         * */

        // Remove status abr limits
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        user = sharedPreference.getUserSession()

        /** Recipe Info */

        loadRecipeImage(binding.IVRecipe,recipe.img_source)

        binding.TVRef.text = recipe.id.toString()
        binding.dateTV.text = formatServerTimeToDateString(recipe.created_date)
        binding.titleTV.text = recipe.title
        binding.ratingRecipeRB.rating = recipe.source_rating.toFloat()
        binding.ratingMedTV.text = recipe.source_rating
        binding.commentsTV.text = recipe.comments.toString() +" "+ getString(R.string.nr_comments)
        binding.numberLikeTV.text = recipe.likes.toString()
        binding.timeTV.text = recipe.time
        binding.dificultyTV.text = recipe.difficulty
        binding.portionTV.text = recipe.portion

        when(recipe.difficulty){
            RecipeDifficultyConstants.LOW->{
                binding.IV2.setImageResource(R.drawable.low_difficulty)
            }
            RecipeDifficultyConstants.MEDIUM->{
                binding.IV2.setImageResource(R.drawable.medium_difficulty)
            }
            RecipeDifficultyConstants.HIGH->{
                binding.IV2.setImageResource(R.drawable.high_difficulty)
            }
        }

        /** User Info */

        loadUserImage(binding.imageAuthorIV, recipe.created_by.imgSource)

        binding.nameAuthorTV.text = formatNameToNameUpper(recipe.created_by.name)

        if(recipe.created_by.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        // likes
        updateLikeUI()


        binding.likeIB.setOnClickListener {
            if (sharedPreference.getUserSession().checkIfLiked(recipe) == -1) {
                recipeViewModel.addLikeOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeLikeOnRecipe(recipe.id)
            }
        }

        // Favorites
        updateSaveUI()


        binding.favoritesIB.setOnClickListener {
            if (sharedPreference.getUserSession().checkIfSaved(recipe) == -1) {
                recipeViewModel.addSaveOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeSaveOnRecipe(recipe.id)
            }
        }



        /**
         *  Navigation
         * */

        // go to Comments
        binding.commentsCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment,Bundle().apply {
                putInt("recipe_id", recipe.id)
                putInt("user_id", recipe.id)
            })
        }

        // go to creater profile
        binding.profileAuthorCV.setOnClickListener{
            findNavController().navigate(R.id.action_receitaDetailFragment_to_profileFragment,Bundle().apply {
                putInt("user_id",recipe.created_by.id)
            })
        }

        // go to create calender entry
        binding.calenderIB.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_newCalenderEntryFragment,Bundle().apply {
                putParcelable("Recipe",objRecipe)
            })
        }

        // go back
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }


        /**
         *  Tab Layout
         * */

        // enables back from comments
        binding.fragmentRecipeDetailViewPager.isSaveEnabled = false

        if (binding.fragmentRecipeDetailTabLayout.tabCount != 2){
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

        binding.fragmentRecipeDetailViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.fragmentRecipeDetailTabLayout.selectTab(binding.fragmentRecipeDetailTabLayout.getTabAt(position))
            }
        })



    }


    private fun loadUI() {
        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeStatusBarColor(false, activity, requireContext())

    }

    private fun updateLikeUI(){

        objRecipe?.let { recipe ->

            binding.numberLikeTV.text = recipe.likes.toString()

            if (user.checkIfLiked(recipe) != -1) {
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            } else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)
        }
    }

    private fun updateSaveUI() {
        objRecipe?.let { recipe ->

            if (user.checkIfSaved(recipe) != -1) {
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
            } else
                binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)
        }
    }

    private fun bindObservers() {

        // Like function
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.recipe_liked))

                        objRecipe!!.likes++
                        user = sharedPreference.addRecipeToLikedList(objRecipe!!)
                        updateLikeUI()

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.recipe_removed_liked))

                        objRecipe!!.likes--
                        user = sharedPreference.removeRecipeFromLikedList(objRecipe!!)
                        updateLikeUI()
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

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.recipe_saved))

                        user = sharedPreference.addSaveToUserSession(objRecipe!!)
                        updateSaveUI()

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.recipe_removed_saved))

                        user = sharedPreference.removeSaveFromUserSession(objRecipe!!)
                        updateSaveUI()

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        // comments function

        recipeViewModel.functionGetSizedCommentsOnRecipePaginated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // selecionar as 2/3 primeiras imagens
                        if (result.data!!.result.isNotEmpty()) {
                            try {
                                result.data.result[0].user?.imgSource?.let { img ->
                                    binding.userComent1IV.visibility = View.VISIBLE
                                    loadUserImage(binding.userComent1IV, img)
                                }
                                result.data.result[1].user?.imgSource?.let { img ->
                                    binding.userComent1IV.visibility = View.VISIBLE
                                    loadUserImage(binding.userComent2IV, img)
                                }
                            } catch (_: IndexOutOfBoundsException) {
                            }

                        }
                        else{
                            binding.userComent1IV.visibility = View.GONE
                            binding.userComent2IV.visibility = View.INVISIBLE
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

    private fun showValidationErrors(toString: String) {
        Log.d(TAG, "showValidationErrors: " + toString)
    }


    override fun onPause() {
        super.onPause()
        //destroy variables
        userPortion = -1F
        recipePortion = -1F
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    companion object{
        var userPortion: Float = -1F
        var recipePortion: Float = -1F
    }





}

/* binding.profileAuthorCV.setOnClickListener {


           *//*     val view : View = layoutInflater.inflate(R.layout.modal_bottom_sheet_profile,null)
                   val dialog = BottomSheetDialog(requireContext())
                   dialog.behavior.state=BottomSheetBehavior.STATE_COLLAPSED
                   dialog.behavior.peekHeight=650

                   dialog.setContentView(view)
                   dialog.show()*//*
            //findNavController().navigate(R.id.action_receitaDetailFragment_to_followerFragment,bundle)

            *//*           findNavController().navigate(R.id.action_receitaDetailFragment_to_profileBottomSheetDialog,Bundle().apply {
                           putParcelable("User", recipe.created_by)
                       })*//*



            *//*        findNavController().navigate(R.id.action_receitaDetailFragment_to_followerFragment,Bundle().apply {
                          putInt("userID",recipe.created_by.id)
                          putString("userName",recipe.created_by.name)
                          putInt("followType",FollowType.FOLLOWERS)
                      })
          *//*
        }*/

/*        //val standardBottomSheet = findViewById<FrameLayout>(R.id.standard_bottom_sheet)
        val adaptiveViewBS = BottomSheetBehavior.from(binding.adaptiveViewBS!!)
        // Use this to programmatically apply behavior attributes; eg.
        adaptiveViewBS.saveFlags = BottomSheetBehavior.SAVE_ALL
        adaptiveViewBS.setState(STATE_HIDDEN);*/

/*        binding.commentsCV.setOnClickListener {

            //adaptiveViewBS.setState(STATE_COLLAPSED);

            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)

            //standardBottomSheetBehavior.isDraggable=false
*//*            val newFragment = CommentsFragment()
            newFragment.arguments = bundle
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentFL, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()*//*
        }*/

/*   adaptiveViewBS.addBottomSheetCallback(object : BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // Handle state change
            val bottomSheet = BottomSheetBehavior.from(bottomSheet)
            if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                // Bottom sheet is in full-screen mode
                //toast("FULL-MODE")
              //  bottomSheet.isDraggable=false
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Handle slide offset change
        }
    })

    binding.dragHandle!!.setOnTouchListener { view, motionEvent ->
        if(motionEvent.action == MotionEvent.ACTION_DOWN) {
          //     adaptiveViewBS.isDraggable=true
        }
        true
    }*/

/*      binding.IVSource.setOnClickListener {
          val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.source_link))
          startActivity(browserIntent)
      }*/
