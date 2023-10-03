package com.example.projectfoodmanager.presentation.recipe.details


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToFormatDate
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    val TAG: String = "ReceitaDetailFragment"
    lateinit var binding: FragmentRecipeDetailBinding
    private var objRecipe: Recipe? = null
    private val recipeViewModel: RecipeViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    lateinit var manager: LinearLayoutManager



    private lateinit var adapter: FragmentAdapter

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




    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            objRecipe = arguments?.getParcelable("Recipe", Recipe::class.java)
        } else {
            objRecipe = arguments?.getParcelable("Recipe")
        }
        //requireActivity().window.statusBarColor =  requireContext().getColor(R.color.transparent)


        super.onViewCreated(view, savedInstanceState)

        binding.calenderIB.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_newCalenderEntryFragment,Bundle().apply {
                putParcelable("Recipe",objRecipe)
            })
        }

        if (objRecipe != null) {
            if (isOnline(view.context)) {
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


    }


    private fun setUI(recipe: Recipe) {


        //Go CommentsFragment
        binding.commentsCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment,Bundle().apply {
                putInt("recipe_id", recipe.id)
                putInt("user_id", recipe.id)
            })
        }

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

        //AUTHOR-> NAME
        binding.nameAuthorTV.text = formatNameToNameUpper(recipe.created_by.name)

        //AUTHOR-> IMG
        Helper.loadUserImage(binding.imageAuthorIV, recipe.created_by.img_source)

        //AUTHOR-> VERIFIED
        if(recipe.created_by.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }


        binding.profileAuthorCV.setOnClickListener {

     /*     val view : View = layoutInflater.inflate(R.layout.modal_bottom_sheet_profile,null)
            val dialog = BottomSheetDialog(requireContext())
            dialog.behavior.state=BottomSheetBehavior.STATE_COLLAPSED
            dialog.behavior.peekHeight=650

            dialog.setContentView(view)
            dialog.show()*/
            //findNavController().navigate(R.id.action_receitaDetailFragment_to_followerFragment,bundle)

 /*           findNavController().navigate(R.id.action_receitaDetailFragment_to_profileBottomSheetDialog,Bundle().apply {
                putParcelable("User", recipe.created_by)
            })*/

            findNavController().navigate(R.id.action_receitaDetailFragment_to_newRecipeFragment)

            /*        findNavController().navigate(R.id.action_receitaDetailFragment_to_followerFragment,Bundle().apply {
                          putInt("userID",recipe.created_by.id)
                          putString("userName",recipe.created_by.name)
                          putInt("followType",FollowType.FOLLOWERS)
                      })
          */
        }

  /*      binding.IVSource.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.source_link))
            startActivity(browserIntent)
        }*/


        binding.TVRef.text = recipe.id.toString()

        //-> Load Recipe img
        Helper.loadRecipeImage(binding.IVRecipe,recipe.img_source)

        //info

        binding.dateTV.text = formatServerTimeToDateString(recipe.created_date)
        binding.titleTV.text = recipe.title
        binding.ratingRecipeRB.rating = recipe.source_rating.toFloat()
        binding.ratingMedTV.text = recipe.source_rating.toString()

        binding.numberLikeTV.text = recipe.likes.toString()

        // check for user likes
        val user: User = sharedPreference.getUserSession()


        //--------- LIKES ---------
        if (user.checkIfLiked(recipe) != -1) {
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        } else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)


        binding.likeIB.setOnClickListener {
            if (sharedPreference.getUserSession().checkIfLiked(recipe) == -1) {
                recipeViewModel.addLikeOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeLikeOnRecipe(recipe.id)
            }
        }

        //--------- FAVORITES ---------
        if (user.checkIfSaved(recipe) != -1) {
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        } else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

        binding.favoritesIB.setOnClickListener {
            if (sharedPreference.getUserSession().checkIfSaved(recipe) == -1) {
                recipeViewModel.addSaveOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeSaveOnRecipe(recipe.id)
            }
        }


        binding.timeTV.text = recipe.time
        binding.dificultyTV.text = recipe.difficulty

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

        binding.portionTV.text = recipe.portion

        // tabs

        binding.recipeDetailTab.removeAllTabs()

        val fragmentAdapter = FragmentAdapter(requireActivity().supportFragmentManager, lifecycle, recipe)

        binding.recipeDetailTab.addTab(binding.recipeDetailTab.newTab().setText("Recipe"))
        binding.recipeDetailTab.addTab(binding.recipeDetailTab.newTab().setText("Nutrition"))


        binding.recipeInfoViewPager.adapter = fragmentAdapter
        // enables back from comments
        binding.recipeInfoViewPager.isSaveEnabled = false

        binding.recipeDetailTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null)
                    binding.recipeInfoViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        binding.recipeInfoViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.recipeDetailTab.selectTab(binding.recipeDetailTab.getTabAt(position))
            }
        })


        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun updateUI(recipe: Recipe) {

        // check for user likes
        val user: User? = sharedPreference.getUserSession()

        binding.numberLikeTV.text = recipe.likes.toString()

        if (user != null) {
            if (user!!.checkIfLiked(recipe) != -1) {
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            } else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)
        }

        // check for user saves

        if (user != null) {
            if (user!!.checkIfSaved(recipe) != -1) {
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
            } else
                binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)
        }




    }

    private fun bindObservers() {

        // Like function
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        it
                        toast(getString(R.string.recipe_liked))

                        // updates local list
                        if (objRecipe!!.id == it.data) {
                            objRecipe!!.likes++
                            sharedPreference.addLikeToUserSession(objRecipe!!)
                            updateUI(objRecipe!!)
                        }

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        it
                        toast(getString(R.string.recipe_removed_liked))

                        // updates local list
                        if (objRecipe!!.id == it.data) {
                            objRecipe!!.likes--
                            sharedPreference.removeLikeFromUserSession(objRecipe!!)
                            updateUI(objRecipe!!)
                        }

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        it
                        toast(getString(R.string.recipe_saved))

                        // updates local list
                        if (objRecipe!!.id == it.data) {
                            sharedPreference.addSaveToUserSession(objRecipe!!)
                            updateUI(objRecipe!!)
                        }

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        it
                        toast(getString(R.string.recipe_removed_saved))

                        // updates local list
                        if (objRecipe!!.id == it.data) {
                            sharedPreference.removeSaveFromUserSession(objRecipe!!)
                            updateUI(objRecipe!!)
                        }

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })
    }

    private fun showValidationErrors(toString: String) {
        Log.d(TAG, "showValidationErrors: " + toString)
    }


    override fun onResume() {
        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.statusBarColor = requireContext().getColor(R.color.background_1)
        window.navigationBarColor = requireContext().getColor(R.color.background_1)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        super.onResume()
    }

}