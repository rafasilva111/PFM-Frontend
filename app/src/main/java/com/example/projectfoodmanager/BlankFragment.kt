package com.example.projectfoodmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
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
        // Notas : setUi deverá ocorrer aqui pois não precisamos de estar sempre a inicializar todas as componentes do fragment ( buttons,
        // recyclerviews, adapters, etc...), apenas o deveremos fazer quando o fragment é inicializado
        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {


    }

    private fun bindObservers() {

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
        changeStatusBarColor(false, activity, requireContext())

    }
}