package com.example.projectfoodmanager.presentation.goals.createGoal.tabs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.databinding.FragmentGoalCarboHydratesFramgentBinding
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.goalGenericReport
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.userGoal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GoalCarbohydratesFragment(private var parentBinding: FragmentCreateGoalBinding) : Fragment() {




    /** binding */
    private lateinit var binding: FragmentGoalCarboHydratesFramgentBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "GoalWeightFragment"
    private var carbohydrates: Float = -1.0F

    /** injects */

    /** adapters */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentGoalCarboHydratesFramgentBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {

        loadUI()
        super.onResume()
    }

    private fun setUI() {

        /**
         *  General
         * */



        /**
         *  Navigation
         * */

        binding.backBtn.setOnClickListener {

            // Slide to previous page
            parentBinding.fragmentRegisterViewPager.currentItem= parentBinding.fragmentRegisterTabLayout.selectedTabPosition- 1

        }

        binding.continueBtn.setOnClickListener {
            if (validation()){
                // Slide to next page
                parentBinding.fragmentRegisterViewPager.currentItem= parentBinding.fragmentRegisterTabLayout.selectedTabPosition +1
            }
        }

        /**
         *  Validations
         * */


        binding.goalRg.setOnCheckedChangeListener { _, checkedId ->
            validateCarbohydrates()
            when(checkedId){
                R.id.only_option-> userGoal.carbohydrates = goalGenericReport.carbohydrates.onlyOption
                R.id.forty_perc-> userGoal.carbohydrates = goalGenericReport.carbohydrates.fortyPerc
                R.id.fifty_perc-> userGoal.carbohydrates = goalGenericReport.carbohydrates.fiftyPerc
                R.id.sixty_five_perc-> userGoal.carbohydrates = goalGenericReport.carbohydrates.sixtyFivePerc
                R.id.seventy_five_perc-> userGoal.carbohydrates = goalGenericReport.carbohydrates.seventyFivePerc
            }
        }


    }

    private fun loadUI() {

        /**
         *  General
         * */

        if (goalGenericReport.carbohydrates.onlyOption != null) {
            onlyOptionVisible(true)

            binding.onlyOptionValue.text = "${goalGenericReport.carbohydrates.onlyOption.toString()} g"

        }
        else{
            onlyOptionVisible(false)

            binding.fortyPercValue.text = "${goalGenericReport.carbohydrates.fortyPerc} g"
            binding.fiftyPercValue.text = "${goalGenericReport.carbohydrates.fiftyPerc} g"
            binding.sixtyFivePercValue.text = "${goalGenericReport.carbohydrates.sixtyFivePerc} g"
            binding.seventyFivePercValue.text = "${goalGenericReport.carbohydrates.seventyFivePerc} g"
        }


    }

    private fun validation(): Boolean {

        var isValid = true

        /** Height  */

        isValid = validateCarbohydrates() and isValid

        return isValid
    }

    private fun validateCarbohydrates():Boolean{

        var isValid = true
        if (binding.goalRg.checkedRadioButtonId == -1) {
            isValid = false

            binding.onlyOption.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.onlyOption.setTextColor(resources.getColor(R.color.red,null))
            binding.fortyPerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.fortyPerc.setTextColor(resources.getColor(R.color.red,null))
            binding.fiftyPerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.fiftyPerc.setTextColor(resources.getColor(R.color.red,null))
            binding.sixtyFivePerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.sixtyFivePerc.setTextColor(resources.getColor(R.color.red,null))
            binding.seventyFivePerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.seventyFivePerc.setTextColor(resources.getColor(R.color.red,null))

            binding.errorCarbohydratesTV.visibility=View.VISIBLE
            binding.errorCarbohydratesTV.text="You need to chose an option"
        }else{
            binding.onlyOption.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.onlyOption.setTextColor(resources.getColor(R.color.black,null))
            binding.fortyPerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.fortyPerc.setTextColor(resources.getColor(R.color.black,null))
            binding.fiftyPerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.fiftyPerc.setTextColor(resources.getColor(R.color.black,null))
            binding.sixtyFivePerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.sixtyFivePerc.setTextColor(resources.getColor(R.color.black,null))
            binding.seventyFivePerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.seventyFivePerc.setTextColor(resources.getColor(R.color.black,null))
            binding.errorCarbohydratesTV.visibility=View.INVISIBLE
        }
        return isValid
    }


    private fun onlyOptionVisible(visible: Boolean){
        if (visible){
            binding.onlyOption.visibility = View.VISIBLE
            binding.onlyOptionValue.visibility = View.VISIBLE

            binding.fortyPerc.visibility = View.GONE
            binding.fortyPercValue.visibility = View.GONE
            binding.fiftyPerc.visibility = View.GONE
            binding.fiftyPercValue.visibility = View.GONE
            binding.sixtyFivePerc.visibility = View.GONE
            binding.sixtyFivePercValue.visibility = View.GONE
            binding.seventyFivePerc.visibility = View.GONE
            binding.seventyFivePercValue.visibility = View.GONE
        }
        else{
            binding.onlyOption.visibility = View.GONE
            binding.onlyOptionValue.visibility = View.GONE

            binding.fortyPerc.visibility = View.VISIBLE
            binding.fortyPercValue.visibility = View.VISIBLE
            binding.fiftyPerc.visibility = View.VISIBLE
            binding.fiftyPercValue.visibility = View.VISIBLE
            binding.sixtyFivePerc.visibility = View.VISIBLE
            binding.sixtyFivePercValue.visibility = View.VISIBLE
            binding.seventyFivePerc.visibility = View.VISIBLE
            binding.seventyFivePercValue.visibility = View.VISIBLE
        }

    }

}