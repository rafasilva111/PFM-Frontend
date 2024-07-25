package com.example.projectfoodmanager.presentation.recipe.comments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentCommentsBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.closeKeyboard
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : Fragment() {



    /** binding */
    private lateinit var binding: FragmentCommentsBinding

    /** viewModels */
    private val recipeViewModel: RecipeViewModel by viewModels()

    /** variables */
    private val TAG: String = "CommentsFragment"
    private lateinit var manager: LinearLayoutManager
    private var snapHelper : SnapHelper = PagerSnapHelper()
    private var recipeId: Int = -1
    private var commentId: Int = -1
    private var pos: Int = 0

    private var alreadyLoaded: Boolean = false

        // Pagination
    private var commentsList: MutableList<Comment> = mutableListOf()
    private var nextPage:Boolean = true
    private var currentPage: Int = 0

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private lateinit var dialogDeleteComment: MaterialAlertDialogBuilder
    private var noMoreRecipesMessagePresented = false

    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    /** adapters */
    private val adapter by lazy {
        CommentsListingAdapter(
            sharedPreference,
            onLikePressed = { commentId, liked ->
                if (liked)
                    recipeViewModel.deleteLikeOnComment(commentId)
                else
                    recipeViewModel.postLikeOnComment(commentId)

            },
            onProfilePressed = {user->
                findNavController().navigate(R.id.action_receitaCommentsFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",user.id)
                })

            },
            onDeletePressed = {commentId->

                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_logout)
                    .setTitle(getString(R.string.COMMENT_ITEM_LAYOUT_DELETE_DIALOG_TITLE))
                    .setMessage(resources.getString(R.string.COMMENT_ITEM_LAYOUT_DELETE_DIALOG_MESSAGE))
                    .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { _, _ ->
                        recipeViewModel.deleteComment(commentId)
                    }
                    .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
                        dialog.dismiss()
                    }.show()


            },
            onEditPressed = {commentId->
                toast("Not implemented yet like")
                //recipeViewModel.patchComment(commentId)

            },
            onCommentPressed = {commentId->
                toast("Not implemented yet like")
                //recipeViewModel.patchComment(commentId)

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

    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         *  Arguments
         * */

       arguments?.let {
            recipeId = it.getInt("recipe_id")
            commentId = it.getInt("comment_id")
            if(commentId != -1){
                pos = 1
           }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        loadUi()
        super.onStart()
    }

    private fun setUI(){

        /**
         *  General
         * */

        binding.header.titleTV.text = getString(R.string.FRAGMENT_COMMENTS_TITLE)

        setRecyclerViewScrollListener()

        /**
         *  Navigation
         * */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }




    }

    private fun loadUi(){
        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        activity.window.navigationBarColor = requireContext().getColor(R.color.main_color)

        val userSession: User = sharedPreference.getUserSession()

        if (isOnline(requireView().context)) {


            binding.recyclerView.adapter = adapter

            if (!alreadyLoaded){
                if (commentId!= -1)
                    recipeViewModel.getComment(commentId)

                recipeViewModel.getCommentsByRecipe(recipeId,currentPage)
                alreadyLoaded = true
            }




            // create a comment
            binding.publishIB.setOnClickListener {

                recipeViewModel.postCommentOnRecipe(
                    recipeId,
                    CommentDTO(text = binding.ETcomment.text.toString())
                )
            }

            //-> Load Author img
            loadUserImage(binding.IVcommentBottonImage,userSession.imgSource)

        }
        else{
            toast("User não tem internet")
        }
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // prevent missed calls to api // needs to be reseted on search so it could be a next page

                    if (nextPage){
                        //val visibleItemCount: Int = manager.childCount
                        val pastVisibleItem: Int =
                            manager.findLastCompletelyVisibleItemPosition()
                        //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                        if ((pastVisibleItem + 1) >= adapter.itemCount){
                            // prevent more scroll
                            binding.recyclerView.removeOnScrollListener(scrollListener)

                            recipeViewModel.getCommentsByRecipe(recipeId = recipeId, page = ++currentPage)
                        }


                    }
                    else if (!noMoreRecipesMessagePresented){
                        noMoreRecipesMessagePresented = true
                        toast("Sorry can't find more comments.",ToastType.ALERT)
                    }


                }

                super.onScrollStateChanged(recyclerView, newState)

            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

    }

    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {

        recipeViewModel.functionGetComment.observe(viewLifecycleOwner){ event ->
            event.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        /** Update adapter list */
                        result.data?.let { it ->
                            adapter.addFocusedItem(it)
                        }


                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionGetComments.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {


                        if (currentPage >1){
                            // we previously removed the scroll listener to prevent double requests
                            // and here we add it
                            binding.recyclerView.addOnScrollListener(scrollListener)
                            binding.progressBar.hide()
                        }


                        currentPage = it.data!!._metadata.page
                        nextPage = it.data._metadata.nextPage != null



                        for (comment in it.data.result) {
                            if (commentId !=comment.id)
                                adapter.addItemAtBottom( item = comment)
                        }

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {

                        if (currentPage >1)
                            binding.progressBar.show()

                    }
                }
            }
        }

        /**
         *  Create Comment
         * */

        recipeViewModel.functionPostCommentOnRecipe.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        /** Update adapter list */
                        result.data?.let { comment ->
                            adapter.addItemAtTop(item = comment)
                        }

                        /** Update clean textBox */
                        binding.ETcomment.text.clear()
                        closeKeyboard(requireActivity(),requireView())


                        binding.recyclerView.scrollToPosition(0)

                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /**
         *  Delete Comment
         * */

        recipeViewModel.functionDeleteComment.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        /** Update adapter list */
                        result.data?.let { it ->
                            adapter.removeItemById(it.id)
                        }

                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /**
         *  Like Comment Function
         * */

        recipeViewModel.functionPostLikeOnComment.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        /** Update adapter list */
                        result.data?.let { it ->
                            adapter.updateItem(it)
                        }


                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        recipeViewModel.functionDeleteLikeOnComment.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        /** Update adapter list */
                        result.data?.let { it ->
                            adapter.updateItem(it)
                        }


                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "observer: " + result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

    }


}