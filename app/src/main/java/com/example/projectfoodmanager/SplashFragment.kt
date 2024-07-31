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
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
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
    val updateSharedPreferenceTracker: ObservableList<Boolean?> = ObservableList()

    // injects
    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging



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
        changeTheme(true,activity,requireContext())

        /**
         *  OnBoarding
         * */



        Handler().postDelayed({
            // check if onBoarding is already done
            if(sharedPreference.isFirstAppLaunch()){

                // if offline and whit token login anyway
                if (tokenManager.getAccessToken() !=null && isOnline(requireContext()) ){

                        userViewModel.getUserSession()



                }
                else if (tokenManager.getRefreshToken() !=null){
                    // todo obter novo access token
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
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



        /** Calendar Entries */
        val calenderEntries = sharedPreferencesMetadata[SharedPreferencesMetadata.CALENDER_ENTRIES]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (calenderEntries == null || calenderEntries == true){
            updateSharedPreferenceTracker.add(0,null)
            // get calender entrys from -15 days to +15 days to have smt in memory
            LocalDateTime.now().let { dateNow ->
                calendarViewModel.getCalendarDatedEntryList(
                    fromDate = dateNow.minusDays(MAX_CALENDER_DAYS),
                    toDate = dateNow.plusDays(MAX_CALENDER_DAYS),
                    cleanseOldRegistry = true
                )
            }
        }
        else if (calenderEntries == false)
            TODO("update calenderEntrys on db")


        /** Shopping Lists */

        val shoppingLists = sharedPreferencesMetadata[SharedPreferencesMetadata.SHOPPING_LIST]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (shoppingLists == null || shoppingLists == true){
            updateSharedPreferenceTracker.add(1,null)
            shoppingListViewModel.getUserShoppingLists()
        }
        else if (shoppingLists == false)
            TODO("update shoppingLists on db")

        /** Recipes Background */

        val recipesBackground = sharedPreferencesMetadata[SharedPreferencesMetadata.RECIPES_BACKGROUND]

        // se session estiver a null ou se session estiver a true vai buscar a info
        if (recipesBackground == null || recipesBackground == true) {
            updateSharedPreferenceTracker.add(2,null)
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
                            firebaseMessaging.token.addOnSuccessListener { token ->
                                if (result.data.fmcToken != token)
                                    userViewModel.updateUser(UserDTO(fmc_token = token))
                            }
                        updateLocalSharedPreferences()

                    }
                    is NetworkResult.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)

                        toast(result.message.toString(),ToastType.ERROR)

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

                        updateSharedPreferenceTracker[0] = true
                        updateSharedPreferenceTracker.notifyObservers()


                    }
                    is NetworkResult.Error -> {
                        updateSharedPreferenceTracker[0] = false
                        updateSharedPreferenceTracker.notifyObservers()
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

                        updateSharedPreferenceTracker[1] = true
                        updateSharedPreferenceTracker.notifyObservers()

                    }
                    is NetworkResult.Error -> {
                        // todo fix
                        updateSharedPreferenceTracker[1] = false
                        updateSharedPreferenceTracker.notifyObservers()
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

                        updateSharedPreferenceTracker[2] = true
                        updateSharedPreferenceTracker.notifyObservers()

                    }
                    is NetworkResult.Error -> {
                        updateSharedPreferenceTracker[2] = false
                        updateSharedPreferenceTracker.notifyObservers()
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        updateSharedPreferenceTracker.addObserver { list ->

            if (list.isNotEmpty() && list.all { it != null && it }) {
                findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
            }
            else if(list.isNotEmpty() && list.all { it != null}){
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                Log.e(TAG, "Warning -> issue ")
            }

        }
    }


}