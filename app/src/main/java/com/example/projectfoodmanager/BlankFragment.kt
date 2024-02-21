package com.example.projectfoodmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentNotificationBinding
import com.example.projectfoodmanager.presentation.follower.FollowerFragment
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BlankFragment : Fragment() {


    /** binding */
    private lateinit var binding: FragmentBlankBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "BlankFragment"


    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */



    override fun onCreate(savedInstanceState: Bundle?) {


        //Para obter os argumentos passados pelo bundle é boa pratica ser feito no onCreate, pois só carrega uma vez
/*        arguments?.let {
            userId = it.getInt("userId")
            userName = it.getString("userName")
            FollowerFragment.followType = it.getInt("followType")
        }*/

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentBlankBinding.inflate(layoutInflater)
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
        Helper.changeMenuVisibility(false, activity)
        Helper.changeStatusBarColor(true, activity, requireContext())

    }

    private fun bindObservers() {

    }

    override fun onPause() {
        // usar para atualizar/destruir listas de elementos
        super.onPause()
    }
}