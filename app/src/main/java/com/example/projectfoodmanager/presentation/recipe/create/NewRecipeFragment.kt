package com.example.projectfoodmanager.presentation.recipe.create

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentNewRecipeBinding



class NewRecipeFragment : Fragment() {

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }*/

    // binding
    lateinit var binding: FragmentNewRecipeBinding

    private var currentStep:Int = 1
    private var stepMAX:Int = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentNewRecipeBinding.inflate(layoutInflater)

/*            binding.followerRV.layoutManager = LinearLayoutManager(activity)
            binding.followerRV.adapter = adapter*/

/*            bindObservers()*/
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUI()


        binding.nextBTN.setOnClickListener {
            nextStep()
        }

        binding.previousBTN.setOnClickListener {
            previousStep()
        }

    }

    private fun setUI() {
        // Create new fragment and transaction
       // val transaction = parentFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        //transaction.replace(com.example.projectfoodmanager.R.id.frameRecipeFL, StepDetailFragment())
        //transaction.addToBackStack(null)

        // Commit the transaction
        //transaction.commit()

    }

    fun nextStep(){

        //validar os campos CREATE FUNC
        //if(!valide())
        //    return false //message

        if(currentStep+1 > stepMAX)
            return

        //-------------------
        //DISABLE OLD STEP
        //-------------------

        //---> [OFF] TEXT VIEW
        //binding.nStep1TV
        binding.root.findViewById<TextView>(resources.getIdentifier("nStep${currentStep}TV", "id", requireActivity().packageName)).visibility= View.INVISIBLE

        //---> [OFF] TEXT VIEW DESC VISIBILITY (GONE)
        //binding.descStep1TV
        binding.root.findViewById<TextView>(resources.getIdentifier("descStep${currentStep}TV", "id",  requireActivity().packageName)).visibility= View.GONE

        //--->  [ON] IMAGE VIEW
        //binding.iconStep1IV
        binding.root.findViewById<ImageView>(resources.getIdentifier("iconStep${currentStep}IV", "id",  requireActivity().packageName)).visibility= View.VISIBLE

        //CHANGE CURRENT STEP TO NEW STEP

        currentStep+=1

        //-------------------
        //ENABLE OLD STEP
        //-------------------

        //--->  [ON] CONSTRAINT COLOR MAIN-COLOR
        //binding.step1CL
        binding.root.findViewById<ConstraintLayout>(resources.getIdentifier("step${currentStep}CL", "id", requireActivity().packageName)).backgroundTintList=
            ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))

        //CHANGE COLOR TEXT IN TEXT VIEW
        //binding.nStep1TV
        binding.root.findViewById<TextView>(resources.getIdentifier("nStep${currentStep}TV", "id", requireActivity().packageName)).setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))
            ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))

        //--->  [ON] TEXT VIEW DESC VISIBILITY (VISIBLE)
        //binding.descStep1TV
        val descLabelStepTV= binding.root.findViewById<TextView>(resources.getIdentifier("descStep${currentStep}TV", "id",  requireActivity().packageName))
        descLabelStepTV.visibility= View.VISIBLE

        //update title
        binding.descStepTV.text= descLabelStepTV.text
    }

    fun previousStep(){

        if(currentStep-1 < 1)
            return

        //-------------------
        //DISABLE CURRENT STEP
        //-------------------

        //---> [OFF] TEXT VIEW desc VISIBILITY (VISIBLE)
        //binding.nStep1TV
        binding.root.findViewById<TextView>(resources.getIdentifier("descStep${currentStep}TV", "id", requireActivity().packageName)).visibility= View.GONE


        currentStep-=1

        //-------------------
        //ENABLE CURRENT STEP
        //-------------------

        //--->  [OFF] IMAGE VIEW
        //binding.iconStep1IV
        binding.root.findViewById<ImageView>(resources.getIdentifier("iconStep${currentStep}IV", "id",  requireActivity().packageName)).visibility= View.GONE

        //---> [ON] TEXT VIEW nStep VISIBILITY (VISIBLE)
        //binding.nStep1TV
        binding.root.findViewById<TextView>(resources.getIdentifier("nStep${currentStep}TV", "id", requireActivity().packageName)).visibility= View.VISIBLE

        //---> [ON] TEXT VIEW desc VISIBILITY (VISIBLE)
        //binding.descStep1TV
        val descLabelStepTV= binding.root.findViewById<TextView>(resources.getIdentifier("descStep${currentStep}TV", "id",  requireActivity().packageName))
        descLabelStepTV.visibility= View.VISIBLE

        //update title
        binding.descStepTV.text= descLabelStepTV.text

    }

    override fun onResume() {
        super.onResume()

        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.statusBarColor = requireContext().getColor(R.color.background_1)
        window.navigationBarColor = requireContext().getColor(R.color.background_1)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}