package com.example.projectfoodmanager.presentation.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.notifications.Notification
import com.example.projectfoodmanager.databinding.FragmentNotificationBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.UserViewModel


class NotificationFragment : Fragment() {

    // binding
    lateinit var binding: FragmentNotificationBinding

    // viewModel
    private val userViewModel by activityViewModels<UserViewModel>()

    // constants

    private var notificationList: MutableList<Notification> = mutableListOf()
    lateinit var manager: LinearLayoutManager

    // pagination
    private var currentPage:Int = 1
    private var nextPage:Boolean = true
    private var noMoreRecipesMessagePresented = false

    // notification removal
    private var notificationToBeDeleted: MutableList<Int> = mutableListOf()
    // notification seen
    private var notificationToBeSeen: MutableList<Int> = mutableListOf()

    // adapters
    private val notificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onItemClick = { _, item ->
            }

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentNotificationBinding.inflate(layoutInflater)
        }
        binding.notificationListRV.layoutManager = LinearLayoutManager(activity)
        binding.notificationListRV.adapter = notificationListAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()

        bindObservers()
    }

    private fun loadUI() {

        /**
         *  General
         * */

        binding.requestFollowCV.visibility = View.GONE

        /**
         *  Delete Function
         *  (whit swipe)
         * */

        val swipeToDeleteCallback = object :SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notificationToBeDeleted.add(notificationList.removeAt(viewHolder.absoluteAdapterPosition).id)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)

        itemTouchHelper.attachToRecyclerView(binding.notificationListRV)


        /**
         *  Notifications
         * */

        userViewModel.getNotifications()

        /**
         *  Follow Requests
         * */

        userViewModel.getFollowRequests(pageSize = 1)
    }



    private fun setUI() {

        /**
         *  General
         * */
        // muda a cor do status par para fora do tema (branco)
        context?.let { activity?.window!!.navigationBarColor = it.getColor(R.color.white) }


        /**
         * Navigation
         */

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.requestFollowCV.setOnClickListener {

            findNavController().navigate(R.id.action_notificationFragment_to_followRequestFragment)

        }

    }


    private fun bindObservers() {

        /**
         *  Notifications
         * */

        userViewModel.getNotificationsResponseLiveData.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        //binding.progressBar.hide()

                        // sets page data

                        currentPage = it.data!!._metadata.current_page
                        nextPage = it.data._metadata.next != null

                        // check if list empty

                        if(it.data.result.isEmpty()){
                            binding.fragmentNotificationNoNotification.visibility=View.VISIBLE
                            notificationListAdapter.updateList(mutableListOf())
                            return@let
                        }else{
                            binding.fragmentNotificationNoNotification.visibility=View.GONE

                        }

                        // checks if new search

                        if (currentPage == 1){
                            notificationList = it.data.result
                            noMoreRecipesMessagePresented = false
                        }
                        else{
                            notificationList += it.data.result
                        }

                        notificationListAdapter.updateList(notificationList)

                        notificationToBeSeen = notificationList.filter { result ->
                            !result.seen
                        }.map { result->
                            result.id
                        } as MutableList<Int>

                        binding.progressBar.hide()

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        /**
         *  Follow Requests
         * */

        userViewModel.getFollowRequestsLiveData.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // isto é usado para atualizar os likes caso o user vá a detail view


                        if (result.data!!.result.isNotEmpty()){

                            binding.fragmentNotificationFollowRequests.visibility = View.VISIBLE

                            binding.nRequestBadgeTV.text = result.data._metadata.total_items.toString()


                            loadUserImage(binding.imgFirstReqIV, result.data.result[0].imgSource)

                        }

                        binding.progressBar.hide()

                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        // delete notifications
        if (notificationToBeDeleted.isNotEmpty())
            userViewModel.deleteNotifications(IdListRequest(idList=notificationToBeDeleted))

        // update as seem notifications
        if (notificationToBeSeen.isNotEmpty())
            userViewModel.putNotifications(IdListRequest(idList=notificationToBeSeen))


    }

    override fun onDestroy() {
        // muda a cor do status par para dentro do tema (vermelho)
        context?.let { activity?.window!!.navigationBarColor = it.getColor(R.color.main_color)}

        super.onDestroy()
    }

}