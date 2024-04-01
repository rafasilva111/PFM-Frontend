package com.example.projectfoodmanager

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.databinding.FragmentSplashBinding
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Constants.MAX_CALENDER_DAYS
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.example.projectfoodmanager.viewmodels.ShoppingListViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject



@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashFragment : Fragment() {

    // binding
    lateinit var binding: FragmentSplashBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()
    private val calendarViewModel by activityViewModels<CalendarViewModel>()
    private val shoppingListViewModel by activityViewModels<ShoppingListViewModel>()

    // constants
    val TAG: String = "SplashFragment"
    val updateSharedPreferenceTracker: ObservableList<Boolean> = ObservableList()

    // injects
    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        if (!this::binding.isInitialized) {
            binding = FragmentSplashBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()

    }

    private fun setUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeStatusBarColor(true,activity,requireContext())

        /**
         *  OnBoarding
         * */



        Handler().postDelayed({
            // check if onBoarding is already done
            if(sharedPreference.isFirstAppLaunch()){

                // if offline and whit token login anyway
                if (tokenManager.getToken()!=null && isOnline(requireContext())){
                    updateLocalSharedPreferences()
                }
                else{
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        },SPLASH_TIME)

    }



    private fun updateLocalSharedPreferences() {
        val sharedPreferencesMetadata = sharedPreference.getSharedPreferencesMetadata()
        updateSharedPreferenceTracker.clear()

        userViewModel.getUserSession()

        // Calender entrys
        val calenderEntrys = sharedPreferencesMetadata[SharedPreferencesMetadata.CALENDER_ENTRYS]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (calenderEntrys == null || calenderEntrys == true){
            updateSharedPreferenceTracker.add(0,false)
            // get calender entrys from -15 days to +15 days to have smt in memory
            LocalDateTime.now().let { dateNow ->
                calendarViewModel.getCalendarDatedEntryList(
                    fromDate = dateNow.minusDays(MAX_CALENDER_DAYS),
                    toDate = dateNow.plusDays(MAX_CALENDER_DAYS),
                    cleanseOldRegistry = true
                )
            }
        }
        else if (calenderEntrys == false)
            TODO("update calenderEntrys on db")

        // Shopping Lists
        val shoppingLists = sharedPreferencesMetadata[SharedPreferencesMetadata.SHOPPING_LIST]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (shoppingLists == null || shoppingLists == true){
            updateSharedPreferenceTracker.add(1,false)
            shoppingListViewModel.getUserShoppingLists()
        }
        else if (shoppingLists == false)
            TODO("update shoppingLists on db")

        // Recipes Background
        val recipesBackground = sharedPreferencesMetadata[SharedPreferencesMetadata.RECIPES_BACKGROUND]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (recipesBackground == null || recipesBackground == true) {
            updateSharedPreferenceTracker.add(2,false)
            userViewModel.getUserRecipesBackground()
        }
        else if (recipesBackground == false)
            TODO("update recipesBackground on db")


    }

    private fun bindObservers() {
        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if ( result.data!!.fmcToken != "-1")
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                if (result.data.fmcToken != token)
                                    userViewModel.updateUser(UserDTO(fmc_token = token))
                            }
                    }
                    is NetworkResult.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                        tokenManager.deleteToken()

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        calendarViewModel.getCalendarDatedEntryListLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (updateSharedPreferenceTracker.isNotEmpty()){
                            updateSharedPreferenceTracker[0] = true
                            updateSharedPreferenceTracker.notifyObservers()
                        }

                    }
                    is NetworkResult.Error -> {
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        shoppingListViewModel.getUserShoppingLists.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (updateSharedPreferenceTracker.isNotEmpty()) {
                            updateSharedPreferenceTracker[1] = true
                            updateSharedPreferenceTracker.notifyObservers()
                        }
                    }
                    is NetworkResult.Error -> {
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        userViewModel.getUserRecipesBackgroundLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (updateSharedPreferenceTracker.isNotEmpty()) {
                            updateSharedPreferenceTracker[2] = true
                            updateSharedPreferenceTracker.notifyObservers()}

                    }
                    is NetworkResult.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                        Log.e(TAG, "bindObservers: getUserRecipesBackground error "+result.message)
                        toast(getString(R.string.welcome))
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        updateSharedPreferenceTracker.addObserver { list ->
            if (list.isNotEmpty() && list.all { it }) {
                findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
                toast(getString(R.string.welcome))
            }
        }
    }


}