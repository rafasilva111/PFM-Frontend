package com.example.projectfoodmanager.presentation.profile

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentProfileBottomSheetDialogBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileBottomSheetDialog : BottomSheetDialogFragment() {

    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var objUser: User
    lateinit var binding: FragmentProfileBottomSheetDialogBinding

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.peekHeight=700

        }
        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.navigationBarColor = requireContext().getColor(R.color.background_1)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            //@Suppress("DEPRECATION")
            //window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        return dialog

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        return if (this::binding.isInitialized) {
            binding.root
        } else {
            // Inflate the layout for this fragment
            binding = FragmentProfileBottomSheetDialogBinding.inflate(layoutInflater)

            bindObservers()


            binding.root
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            objUser = arguments?.getParcelable("User", User::class.java)!!
        } else {
            objUser = arguments?.getParcelable("User")!!
        }



        setUI()

    }

    private fun setUI() {

        Helper.loadUserImage(binding.imageAuthorIV,objUser.img_source)


        binding.nameAuthorTV.text= Helper.formatNameToNameUpper(objUser.name)

        //VERIFIED
        if(objUser.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }


        if (objUser.description.isNotEmpty()){
            binding.descriptionAuthorTV.text=objUser.description
        }else{
            binding.descriptionAuthorTV.text="No description"
        }



        //TODO: Caso sejam valores grandes encortar Ex: 1000 = 1k
        binding.nFollowersTV.text= objUser.followers.toString()
        binding.nFollowedsTV.text= objUser.followeds.toString()

        //TODO: O numero de receitas
        //binding.nRecipesTV.text=

        if (objUser.profile_type=="PRIVATE"){
            binding.privateAccountLL.visibility=View.VISIBLE

        }else{
            binding.privateAccountLL.visibility=View.INVISIBLE

            //TODO: Verificar se existe receitas ou não e mostrar a mensagem a dizer No recipes
            //if ()
            binding.emptyRecipeTV.visibility = View.VISIBLE

        }


        binding.nFollowersLL.setOnClickListener {
           /* findNavController().navigate(R.id.action_profileBottomSheetDialog_to_followerFragment,Bundle().apply {
              putInt("userID",objUser.id)
              putString("userName",objUser.name)
              putInt("followType", FollowType.FOLLOWERS)
            })*/
        }

        binding.nFollowedsLL.setOnClickListener {
            /*findNavController().navigate(R.id.action_profileBottomSheetDialog_to_followerFragment,Bundle().apply {
                putInt("userID",objUser.id)
                putString("userName",objUser.name)
                putInt("followType", FollowType.FOLLOWEDS)
            })*/
        }

        //Get User in SharedPreferences
        val user = sharedPreference.getUserSession()

        if(user.id==objUser.id){
            binding.followBTN.visibility=View.INVISIBLE
        }else{
            binding.followBTN.visibility=View.VISIBLE
        }


        binding.followBTN.setOnClickListener {

            //TODO: Verificar se o utilizador é ou não seguidor
            userViewModel.postFollowRequest(objUser.id)

            if (objUser.profile_type=="PRIVATE"){
                binding.followBTN.text = "Aguardar confirmação"
            }else{
                binding.followBTN.text = "A seguir"
            }

            binding.followBTN.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grayLightBTN))
            binding.followBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

    }

    private fun bindObservers() {
        userViewModel.postUserFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Pedido enviado com sucesso")
                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }
    }


    override fun onResume() {
        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.navigationBarColor = requireContext().getColor(R.color.background_1)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            //@Suppress("DEPRECATION")
            //window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        super.onResume()
    }
}