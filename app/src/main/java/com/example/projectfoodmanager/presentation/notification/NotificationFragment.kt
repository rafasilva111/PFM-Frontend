package com.example.projectfoodmanager.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.databinding.FragmentNotificationBinding
import com.example.projectfoodmanager.di.notification.MyFirebaseMessagingService
import com.example.projectfoodmanager.di.notification.MyFirebaseMessagingService.Companion.EXTRA_NOTIFICATION_DATA
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.UserViewModel
import java.time.LocalDateTime


class NotificationFragment : Fragment() {

    // binding
    lateinit var binding: FragmentNotificationBinding

    // viewModel
    private val userViewModel by activityViewModels<UserViewModel>()

    // constants
    lateinit var manager: LinearLayoutManager

    // pagination
    private var lastId:Int = -1
    private var pageSize:Int = 12

    // notification removal
    private var notificationToBeDeleted: MutableList<Int> = mutableListOf()
    // notification seen
    private var notificationToBeSeen: MutableList<Int> = mutableListOf()

    // Create a single instance of SwipeToDeleteCallback
    private val swipeToDeleteCallback = object :SwipeToDeleteCallback(){
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // remove item from adapter list and add id to notificationToBeDeleted list for batch delete
            notificationToBeDeleted.add(((viewHolder.itemView.parent as RecyclerView).adapter as NotificationListingAdapter).removeItem(viewHolder.absoluteAdapterPosition).id)
        }
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.hasExtra(EXTRA_NOTIFICATION_DATA)) {
                // Retrieve the extra data from the intent
                val notificationData: Notification? = intent.getParcelableExtra(EXTRA_NOTIFICATION_DATA)

                // Do something with the notification data
                notificationData?.let {
                    binding.todayCV.show()
                    todayNotificationListAdapter.addItem(it,0)
                    // Process the notification data
                }
            }
        }

    }

    // adapters
    private val todayNotificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onAuthorClick = { author ->

                findNavController().navigate(R.id.action_notificationFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",author.id)
                })

            },
            onItemClick = { notification ->
                navigateToNotificationObject(notification)
            }
        )
    }


    private val yesterdayNotificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onAuthorClick = { author ->

                findNavController().navigate(R.id.action_notificationFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",author.id)
                })

            },
            onItemClick = { notification ->
                navigateToNotificationObject(notification)
            }
        )
    }
    private val sevenDayNotificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onAuthorClick = { author ->

                findNavController().navigate(R.id.action_notificationFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",author.id)
                })

            },
            onItemClick = { notification ->
                navigateToNotificationObject(notification)
            }
        )
    }
    private val thirtyDayNotificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onAuthorClick = { author ->

                findNavController().navigate(R.id.action_notificationFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",author.id)
                })

            },
            onItemClick = { notification ->
                navigateToNotificationObject(notification)
            }
        )
    }
    private val olderNotificationListAdapter by lazy {
        NotificationListingAdapter(
            context,
            onAuthorClick = { author ->

                findNavController().navigate(R.id.action_notificationFragment_to_profileFragment,Bundle().apply {
                    putInt("user_id",author.id)
                })

            },
            onItemClick = { notification ->
                navigateToNotificationObject(notification)
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


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()

        bindObservers()
    }

    override fun onResume() {
        super.onResume()
        // Register the broadcast receiver
        context?.registerReceiver(notificationReceiver, IntentFilter(MyFirebaseMessagingService.ACTION_NOTIFICATION_RECEIVED))
    }

    private fun loadUI() {

        /**
         *  General
         * */


        val activity = requireActivity()
        Helper.changeMenuVisibility(false, activity)
        Helper.changeTheme(false, activity, requireContext())


        /**
         *  Notifications
         * */

        if (lastId == -1)
            userViewModel.getNotifications(pageSize = pageSize)

        /**
         *  Follow Requests
         * */

        userViewModel.getFollowRequests(pageSize = 1)
    }



    private fun setUI() {

        /**
         *  General
         * */


        binding.header.titleTV.text = getString(R.string.FRAGMENT_NOTIFICATION_TITLE)

        /**
         *  Recycler Views
         * */

        binding.todayNotificationListRV.layoutManager = LinearLayoutManager(activity)
        binding.yesterdayNotificationListRV.layoutManager = LinearLayoutManager(activity)
        binding.sevenDaysNotificationListRV.layoutManager = LinearLayoutManager(activity)
        binding.thirtyDaysNotificationListRV.layoutManager = LinearLayoutManager(activity)
        binding.olderNotificationListRV.layoutManager = LinearLayoutManager(activity)

        binding.todayNotificationListRV.adapter = todayNotificationListAdapter
        binding.yesterdayNotificationListRV.adapter = yesterdayNotificationListAdapter
        binding.sevenDaysNotificationListRV.adapter = sevenDayNotificationListAdapter
        binding.thirtyDaysNotificationListRV.adapter = thirtyDayNotificationListAdapter
        binding.olderNotificationListRV.adapter = olderNotificationListAdapter

        /**
         *  Delete Function
         *  (whit swipe)
         * */


        // Attach ItemTouchHelper to each RecyclerView
        attachSwipeToDelete(binding.todayNotificationListRV)
        attachSwipeToDelete(binding.yesterdayNotificationListRV)
        attachSwipeToDelete(binding.sevenDaysNotificationListRV)
        attachSwipeToDelete(binding.thirtyDaysNotificationListRV)
        attachSwipeToDelete(binding.olderNotificationListRV)


        /**
         * Navigation
         */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.seeMoreTv.setOnClickListener {
            binding.seeMoreProgressBar.show()
            binding.seeMoreTv.hide()
            userViewModel.getNotifications(lastId=lastId,pageSize = pageSize)
        }

        binding.requestFollowCV.setOnClickListener {

            findNavController().navigate(R.id.action_notificationFragment_to_followRequestFragment)

        }

    }
    private  fun attachSwipeToDelete(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
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

                        binding.progressBar.hide()
                        binding.seeMoreProgressBar.hide()

                        // check if list empty on first time
                        if(it.data!!.result.isEmpty() && lastId == -1){
                            binding.fragmentNotificationNoNotificationTv.visibility=View.VISIBLE
                            return@let
                        }

                        if (it.data._metadata.next != null)
                            binding.seeMoreTv.show()
                        else
                            binding.seeMoreTv.hide()

                        // sets pagination data
                        lastId = it.data.result.last().id

                        binding.fragmentNotificationNoNotificationTv.visibility=View.GONE

                        updateRecyclerViews(it.data.result)

                        notificationToBeSeen += it.data.result.filter { result ->
                            !result.seen
                        }.map { result->
                            result.id
                        } as MutableList<Int>


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


                    }
                    is NetworkResult.Error -> {
                        toast(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }
    }

    private fun updateRecyclerViews(result: MutableList<Notification>) {
        val todayDateTime = LocalDateTime.now()
        val today = todayDateTime.toLocalDate()
        val yesterday = todayDateTime.minusDays(1).toLocalDate()
        val sevenDaysFromYesterday = todayDateTime.minusDays(6).toLocalDate()
        val thirtyDaysFromSevenDaysAgo = todayDateTime.minusDays(23).toLocalDate()

        for (notification in result)
            when(notification.getDate().toLocalDate()){
                today -> todayNotificationListAdapter.addItem(notification)
                yesterday -> yesterdayNotificationListAdapter.addItem(notification)
                sevenDaysFromYesterday -> sevenDayNotificationListAdapter.addItem(notification)
                thirtyDaysFromSevenDaysAgo -> thirtyDayNotificationListAdapter.addItem(notification)
                else-> olderNotificationListAdapter.addItem(notification)

            }


        if (todayNotificationListAdapter.itemCount == 0){
            binding.todayCV.hide()
        }
        else{
            binding.todayCV.show()
        }

        if (yesterdayNotificationListAdapter.itemCount == 0){
            binding.yesterdayCV.hide()
        }
        else{
            binding.yesterdayCV.show()
        }

        if (sevenDayNotificationListAdapter.itemCount == 0){
            binding.sevenDaysCV.hide()
        }
        else{
            binding.sevenDaysCV.show()
        }

        if (thirtyDayNotificationListAdapter.itemCount == 0){
            binding.thirtyDaysCV.hide()
        }
        else{
            binding.thirtyDaysCV.show()
        }

        if (olderNotificationListAdapter.itemCount == 0){
            binding.olderCV.hide()
        }
        else{
            binding.olderCV.show()
        }

    }

    private fun navigateToNotificationObject(item: Notification) {
        when (item.type) {
            FirebaseNotificationCode.LIKE,
            FirebaseNotificationCode.RECIPE_CREATED -> {
                // Handle notification for recipe created
                findNavController().navigate(R.id.action_notificationFragment_to_receitaDetailFragment,Bundle().apply {
                    putInt("recipe_id",item.recipe!!.id)
                })
            }
            FirebaseNotificationCode.COMMENT_LIKED,
            FirebaseNotificationCode.COMMENT -> {
                // Handle notification for recipe created
                findNavController().navigate(R.id.action_notificationFragment_to_receitaCommentsFragment,Bundle().apply {
                    putInt("recipe_id",item.recipe!!.id)
                    putInt("comment_id",item.comment!!.id)
                })

            }
            FirebaseNotificationCode.HEALTH -> {
                // Handle notification for health
                // TODO
            }
            FirebaseNotificationCode.SECURITY -> {
                // Handle notification for security
                // TODO
            }
            FirebaseNotificationCode.SYSTEM -> {
                // Handle notification for system
                // TODO
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

        // Unregister the broadcast receiver to avoid memory leaks
        context?.unregisterReceiver(notificationReceiver)

    }
}