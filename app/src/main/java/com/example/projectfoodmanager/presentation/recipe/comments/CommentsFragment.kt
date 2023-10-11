@file:Suppress("DEPRECATION")

package com.example.projectfoodmanager.presentation.recipe.comments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentCommentsBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class CommentsFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentCommentsBinding

    // viewModels
    private val recipeViewModel: RecipeViewModel by viewModels()

    // constants
    private val TAG: String = "CommentsFragment"
    private lateinit var manager: LinearLayoutManager
    private var snapHelper : SnapHelper = PagerSnapHelper()
    private var recipeId: Int = -1

        //pagination
    private var commentsList: MutableList<Comment> = mutableListOf()
    private var nextPage:Boolean = true
    private var refreshPage: Int = 0
    private var currentPage: Int = 0

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters

    private val adapter by lazy {
        CommentsListingAdapter(
            sharedPreference,
            onLikePressed = {view,commentId ->
                toast("Not implemented yet like")
                view.isClickable = true
            },
            onProfilePressed = {user->
                toast("Not implemented yet profile")

            },
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindObservers()
        if (!this::binding.isInitialized){

            // Inflate the layout for this fragment

            binding = FragmentCommentsBinding.inflate(layoutInflater)


            manager = LinearLayoutManager(activity)
            manager.reverseLayout=false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)
            binding.root
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI(view)
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setUI(view:View){
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        val userSession: User = sharedPreference.getUserSession()

        if (isOnline(view.context)) {

            // list comments
            this.recipeId = arguments?.getInt("recipe_id")!!
            binding.recyclerView.adapter = adapter


            // create a comment
            binding.publishIB.setOnClickListener {

                recipeViewModel.postCommentOnRecipe(
                    recipeId,
                    CreateCommentRequest(text = binding.ETcomment.text.toString())
                )
            }

            //-> Load Author img
            loadUserImage(binding.IVcommentBottonImage,userSession.img_source)

        }
        else{
            toast("User não tem internet")
        }
    }

    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {

        recipeViewModel.functionGetCommentsOnRecipePaginated.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view

                        if (refreshPage != 0) {

                            // todo rui
                            //binding.progressBar.hide()
                            val lastIndex =
                                if (commentsList.size >= PaginationNumber.COMMENTS) (refreshPage * PaginationNumber.COMMENTS) - 1 else commentsList.size - 1
                            var firstIndex = if (commentsList.size >= PaginationNumber.COMMENTS) lastIndex - 4 else 0

                            commentsList.subList(firstIndex, lastIndex + 1).clear()


                            for (recipe in it.data!!.result) {
                                commentsList.add(firstIndex, recipe)
                                firstIndex++
                            }
                            adapter.updateList(commentsList)

                            //reset control variables
                            refreshPage = 0
                        } else {
                            //binding.progressBar.hide()

                            currentPage = it.data!!._metadata.current_page


                            // check next page to failed missed calls to api
                            nextPage = it.data._metadata.next != null


                            for (recipe in it.data.result) {
                                commentsList.add(recipe)
                            }
                            adapter.updateList(commentsList)
                            binding.recyclerView.adapter = adapter
                        }


                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.show()

                    }
                }
            }
        })


        recipeViewModel.functionPostCommentOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.ETcomment.text.clear()
                        commentsList = mutableListOf()
                        adapter.cleanList()
                        recipeViewModel.getCommentsOnRecipePaginated(recipeId,0)

                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + it.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })

       /* // Like function


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
        })*/


    }

    override fun onResume() {

        // vai buscar os commentarios

        if (refreshPage == 0)
            recipeViewModel.getCommentsOnRecipePaginated(recipeId,currentPage)
        else
            recipeViewModel.getCommentsOnRecipePaginated(recipeId,refreshPage)

        super.onResume()
    }
}