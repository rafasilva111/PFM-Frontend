package com.example.projectfoodmanager.presentation.recipe.details


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
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
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailNewBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {
    val TAG: String = "ReceitaDetailFragment"
    lateinit var binding: FragmentRecipeDetailNewBinding
    var objRecipe: Recipe? = null
    val recipeViewModel: RecipeViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    lateinit var manager: LinearLayoutManager

    private lateinit var adapter: FragmentAdapter

    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindObservers()

        if (this::binding.isInitialized) {
            return binding.root
        } else {
            // Inflate the layout for this fragment
            binding = FragmentRecipeDetailNewBinding.inflate(layoutInflater)

            return binding.root
        }
    }
    @ExperimentalBadgeUtils
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        objRecipe = arguments?.getParcelable("Recipe")
        //requireActivity().window.statusBarColor =  requireContext().getColor(R.color.transparent)


        super.onViewCreated(view, savedInstanceState)

        if (objRecipe != null) {
            if (isOnline(view.context)) {

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
                })
            }

            setUI(objRecipe!!)
        }


    }


    private fun setUI(recipe: Recipe) {


        // comments

        val bundle = Bundle()
        bundle.putInt("recipe_id", recipe.id)
        bundle.putInt("user_id", recipe.id)

        binding.commentsFB.setOnClickListener {
            findNavController().navigate(R.id.action_receitaDetailFragment_to_receitaCommentsFragment,bundle)
        }





        // TODO: Inserir imagem do autor da receita
        binding.AutorTV.text = recipe.company
        binding.IVSource.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.source_link))
            startActivity(browserIntent)
        }
        binding.TVRef.text = recipe.id.toString()

        val imgRef = Firebase.storage.reference.child(recipe.img_source)
        imgRef.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            Glide.with(binding.IVRecipe.context).load(imageURL).into(binding.IVRecipe)
        }

        //info
        binding.TVTitle.text = recipe.title
        binding.TVDate.text = recipe.created_date
        binding.radtingRecipe.rating = recipe.source_rating.toFloat()
        binding.ratingMedTV.text = recipe.source_rating.toString()

        binding.numberLikeTV.text = recipe.likes.toString()

        // check for user likes
        val user: User? = sharedPreference.getUserSession()

        if (user != null) {
            if (user!!.checkIfLiked(recipe) != -1) {
                binding.likeIB.setImageResource(R.drawable.ic_like_active)
            } else
                binding.likeIB.setImageResource(R.drawable.ic_like_black)
        }

        binding.likeIB.setOnClickListener {
            if (sharedPreference.getUserSession()!!.checkIfLiked(recipe) == -1) {
                recipeViewModel.addLikeOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeLikeOnRecipe(recipe.id)
            }
        }


        // check for user saves

        if (user != null) {
            if (user!!.checkIfSaved(recipe) != -1) {
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
            } else
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_black)
        }

        binding.favoritesIB.setOnClickListener {
            if (sharedPreference.getUserSession()!!.checkIfSaved(recipe) == -1) {
                recipeViewModel.addSaveOnRecipe(recipe.id)
            } else {
                recipeViewModel.removeSaveOnRecipe(recipe.id)
            }
        }


        binding.TVTime.text = recipe.time
        binding.TVDifficulty.text = recipe.difficulty

        when(recipe.difficulty){
            RecipeDifficultyConstants.LOW->{
                binding.IV3.setImageResource(R.drawable.low_difficulty)
            }
            RecipeDifficultyConstants.MEDIUM->{
                binding.IV3.setImageResource(R.drawable.medium_difficulty)
            }
            RecipeDifficultyConstants.HIGH->{
                binding.IV3.setImageResource(R.drawable.high_difficulty)
            }
        }

        binding.TVPortion.text = recipe.portion

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
                binding.favoritesIB.setImageResource(R.drawable.ic_favorito_black)
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
        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.main_color)
        super.onResume()
    }

    override fun onPause() {
        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.background_1)
        super.onPause()
    }
}