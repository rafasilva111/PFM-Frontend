package com.example.projectfoodmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationData
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentShoppingListDetailBinding
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.NotificationViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BlankFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentBlankBinding

    // viewModels

    // constants
    private val TAG: String = "BlankFragment"

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentBlankBinding.inflate(layoutInflater)





        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()
    }

    private fun setUI() {

    }

    private fun bindObservers() {

    }


}