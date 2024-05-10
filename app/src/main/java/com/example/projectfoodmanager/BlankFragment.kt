package com.example.projectfoodmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BlankFragment : Fragment() {


    /** binding */
    private lateinit var binding: FragmentBlankBinding

    /** viewModels */
    private val goalsViewModel: GoalsViewModel by viewModels()

    /** variables */
    private val TAG: String = "BlankFragment"

        // Example

    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */

    /** observers */
    private val fitnessObserver = Observer<Event<NetworkResult<FitnessReport>>> { networkResultEvent ->
        networkResultEvent.getContentIfNotHandled()?.let {
            when (it) {
                is NetworkResult.Success -> {
                }
                is NetworkResult.Error -> {
                    // Handle error if needed
                }
                is NetworkResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         *  Arguments
         * */
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
        // Notas : setUi deverá ocorrer aqui pois não precisamos de estar sempre a inicializar todas as componentes do fragment ( buttons,
        // recyclerviews, adapters, etc...), apenas o deveremos fazer quando o fragment é inicializado
        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {

    }

    override fun onStart() {
        // Notas : loadUI tem que ser carregada sempre que o fragment começa, porque temos de ter sempre a copia dos dados mais frescos sempre
        // que entramos na view, esta depois poderá ser um load offline ou um load online
        loadUI()
        super.onStart()
    }

    private fun loadUI() {
        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeTheme(false, activity, requireContext())

    }

    private fun bindObservers() {

        // bind the observer
        goalsViewModel.getFitnessModelLiveData.observe(viewLifecycleOwner, fitnessObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // unbind the observer
        goalsViewModel.getFitnessModelLiveData.removeObserver(fitnessObserver)
    }

}