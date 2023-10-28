package com.example.projectfoodmanager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Constants.MAX_CALENDER_DAYS
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

    // viewModels
    private val authViewModel by activityViewModels<AuthViewModel>()
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
    ): View? {
        // Inflate the layout for this fragment
        Handler().postDelayed({
            if(onBoardingFinished()){

                // if offline and whit token login anyway
                if (tokenManager.getToken()!=null && isOnline(inflater.context)){
                    updateLocalSharedPreferences()
                }
                else{
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        },SPLASH_TIME)

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()

    }

    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if ( result.data!!.fmc_token != "-1")
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                if (result.data.fmc_token != token)
                                    authViewModel.updateUser(UserRequest(fmc_token = token))
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

        authViewModel.getUserRecipesBackground.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if (updateSharedPreferenceTracker.isNotEmpty()) {
                        updateSharedPreferenceTracker[2] = true
                        updateSharedPreferenceTracker.notifyObservers()}

                    }
                    is NetworkResult.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
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

    private fun updateLocalSharedPreferences() {
        val sharedPreferencesMetadata = sharedPreference.getSharedPreferencesMetadata()
        updateSharedPreferenceTracker.clear()

        authViewModel.getUserSession()

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
            authViewModel.getUserRecipesBackground()
        }
        else if (recipesBackground == false)
            TODO("update recipesBackground on db")


    }

    override fun onResume() {
        super.onResume()

        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.statusBarColor = requireContext().getColor(R.color.main_color)
        window.navigationBarColor = requireContext().getColor(R.color.main_color)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance( 0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( 0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

}