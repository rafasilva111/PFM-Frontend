package com.example.projectfoodmanager.presentation.recipe.create.tabs

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.databinding.FragmentStepDetailBinding
import com.example.projectfoodmanager.presentation.recipe.create.tabs.util.StepUtil.Companion.createRecipe
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_RECIPE = "RECIPE"


class StepDetailFragment : Fragment() {

    // binding
    lateinit var binding: FragmentStepDetailBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentStepDetailBinding.inflate(layoutInflater)
        }
        setUI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setUI()

        binding.timeET.setOnClickListener {
            showTimePickerDialog()
        }

        binding.addTagSecBTN.setOnClickListener {

            binding.secTagCG.addView(
                Chip(context, null, com.google.android.material.R.style.Widget_Material3_Chip_Input).apply {
                    text= binding.tagSecET.text
                    isClickable=false
                    isCheckable=false
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        binding.secTagCG.removeView(this)

                        if (binding.secTagCG.childCount == 0) {
                            binding.emptyTagTV.visibility=View.VISIBLE
                        } else {
                            binding.emptyTagTV.visibility=View.INVISIBLE
                        }

                    }
                }
            )

            if (binding.secTagCG.childCount == 0) {
                binding.emptyTagTV.visibility=View.VISIBLE
            } else {
                binding.emptyTagTV.visibility=View.INVISIBLE
            }

            binding.tagSecET.text=null

        }

        binding.nextStepBTN.setOnClickListener {
            // Create new fragment and transaction
            getRecipeRequest()

            findNavController().navigate(R.id.action_stepDetailFragment_to_stepIngredientsFragment)

            // Replace whatever is in the fragment_container view with this fragment,
           /* // and add the transaction to the back stack if needed
            transaction.replace(com.example.projectfoodmanager.R.id.frameRecipeFL,
                StepIngredientsFragment().apply {
                arguments= Bundle().apply { putParcelable("RECIPE",getRecipeRequest()) }
            })

            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()*/
        }

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUI() {

        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item, resources.getStringArray(R.array.recipeDifficulty_array))

        binding.difficultyET.setAdapter(arrayAdapter)

        //Set Chips in ChipGroup
        val tagList = resources.getStringArray(R.array.recipeTags_array).toList()
        //oldTagSelected = tagsList.indexOfFirst { it.equals(objCalEntry.tag, ignoreCase = true) }
        // binding.firstTagCG.s
        //style="@style/Widget.Material3.Chip.Filter"
        for(tag in tagList){
            binding.firstTagCG.addView(
                Chip(context,null, com.google.android.material.R.style.Widget_Material3_Chip_Filter).apply {
                    text= tag
                    isClickable=true
                    isCheckable=true
                }
            )
        }
    }

    private fun showTimePickerDialog() {
        val clockFormat =
            if (DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timeNow = LocalTime.now()

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(timeNow.hour)
                .setMinute(timeNow.minute)
                .setTitleText("Digite a que horário pretende")
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()

        picker.addOnPositiveButtonClickListener {
            binding.timeET.setText(getString(
                R.string.calender_formated_date,
                String.format("%02d", picker.hour),
                String.format("%02d", picker.minute)
            ))
        }

        picker.addOnCancelListener {
            picker.dismiss()
        }



        picker.show(parentFragmentManager, "TimePicker");
        picker.dialog?.setCanceledOnTouchOutside(false)
    }

    private fun getRecipeRequest() {


    /* --------- RECIPE REQUEST ---------
        title                     *
        img_source                -
        description               *
        source_link               -
        source_rating             -
        time                      *
        difficulty                *
        portion                   *
        tags                      *
        ingredients               X
        preparation               X
        nutrition_information     X
        ----------------------------------
    */


        //Build tag
        val mainTags = binding.firstTagCG.children
            .filterIsInstance<Chip>()
            .map { it.text.toString() }

        val secondaryTags = binding.secTagCG.children
            .filterIsInstance<Chip>()
            .map { it.text.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }

        //TODO: Union first tags and secound tags
        val tags = (mainTags + secondaryTags).toList()


        var recipeRequest = RecipeRequest(
            title = binding.titleET.text.toString(),
            description = binding.descriptionET.text.toString(),
            time = binding.timeET.text.toString(),
            difficulty = binding.difficultyET.text.toString(),
            portion = binding.portionET.text.toString(),
            tags = tags,
        )

        if (createRecipe != null){
            //UPDATE
            recipeRequest.ingredients= createRecipe!!.ingredients
        }

        createRecipe = recipeRequest

    }


    override fun onResume() {

        val test = createRecipe
        super.onResume()
    }
}