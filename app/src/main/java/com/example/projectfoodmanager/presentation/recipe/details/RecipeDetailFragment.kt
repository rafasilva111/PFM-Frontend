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
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToFormatDate
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
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


    // binding
    lateinit var binding: FragmentRecipeDetailBinding

    // viewModels
    private val recipeViewModel: RecipeViewModel by viewModels()

    // constants
    val TAG: String = "ReceitaDetailFragment"
    private var objRecipe: Recipe? = null
    lateinit var manager: LinearLayoutManager

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private lateinit var adapter: FragmentAdapter


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

        binding.calenderIB.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_newCalenderEntryFragment,Bundle().apply {
                putParcelable("Recipe",objRecipe)
            })
        }

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


    private fun setUI(recipe: Recipe) {

        //--------- GENERAL INFO ---------

        //-> Load Recipe img
        loadRecipeImage(binding.IVRecipe,recipe.img_source)

        //info
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


        // check for user likes
        val user: User = sharedPreference.getUserSession()

        //AUTHOR-> NAME
        binding.nameAuthorTV.text = formatNameToNameUpper(recipe.created_by.name)

        //AUTHOR-> IMG
        loadUserImage(binding.imageAuthorIV, recipe.created_by.img_source)

        //AUTHOR-> VERIFIED
        if(recipe.created_by.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }


        //--------- ROUTES ---------

        //Go CommentsFragment

        binding.commentsCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment,Bundle().apply {
                putInt("recipe_id", recipe.id)
                putInt("user_id", recipe.id)
            })
        }

        binding.profileAuthorCV.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_newRecipeFragment)

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



            /*        findNavController().navigate(R.id.action_receitaDetailFragment_to_followerFragment,Bundle().apply {
                          putInt("userID",recipe.created_by.id)
                          putString("userName",recipe.created_by.name)
                          putInt("followType",FollowType.FOLLOWERS)
                      })
          */
        }

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
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

  /*      binding.IVSource.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.source_link))
            startActivity(browserIntent)
        }*/




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



        // tabs

        binding.recipeDetailTab.removeAllTabs()

        binding.recipeDetailTab.addTab(binding.recipeDetailTab.newTab().setText("Recipe"))
        binding.recipeDetailTab.addTab(binding.recipeDetailTab.newTab().setText("Nutrition"))


        binding.recipeInfoViewPager.adapter = FragmentAdapter(requireActivity().supportFragmentManager, lifecycle, recipe)
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





    }

    private fun updateUI(recipe: Recipe) {

        // check for user likes
        val user: User = sharedPreference.getUserSession()

        binding.numberLikeTV.text = recipe.likes.toString()

        if (user.checkIfLiked(recipe) != -1) {
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        } else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)

        // check for user saves

        if (user.checkIfSaved(recipe) != -1) {
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        } else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

    }

    private fun bindObservers() {

        // Like function
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
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
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
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
        }

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
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
        }

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
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
        }

        // comments function

        recipeViewModel.functionGetSizedCommentsOnRecipePaginated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // selecionar as 2/3 primeiras imagens
                        if (result.data != null){
                            try {
                                result.data.result[0].user?.img_source?.let { img ->
                                    loadUserImage(binding.userComent1IV,img)
                                }
                                result.data.result[1].user?.img_source?.let { img ->
                                    loadUserImage(binding.userComent2IV,img)
                                }
                            } catch (_: IndexOutOfBoundsException) {

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

    private fun showValidationErrors(toString: String) {
        Log.d(TAG, "showValidationErrors: " + toString)
    }

    override fun onStart() {

        Helper.changeStatusBarColor(true, activity, context)
        Helper.changeMenuVisibility(false, activity)

        super.onStart()
    }




}