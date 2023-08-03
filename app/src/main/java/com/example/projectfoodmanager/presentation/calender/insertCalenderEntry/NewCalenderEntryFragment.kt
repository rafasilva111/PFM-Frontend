package com.example.projectfoodmanager.presentation.calender.insertCalenderEntry

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentNewCalenderEntryBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.CalenderViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCalenderEntryFragment : Fragment() {

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel: AuthViewModel by viewModels()
    private val calenderViewModel: CalenderViewModel by viewModels()

    lateinit var binding: FragmentNewCalenderEntryBinding

    var objCalEntry: CalenderEntry? = null

    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private var checkedTag: Int= 0
    private var recipeRecyclerViewList: MutableList<Recipe> = mutableListOf()

    val TAG: String = "NewCalenderEntryFragment"


    lateinit var user: User
    private var currentTabSelected :Int = 0


    private val adapter by lazy {
        RecipeCalenderEntryListingAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_newCalenderEntryFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })
            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bindObservers()


        //todo check for internet connection
        if (this::binding.isInitialized) {
            return binding.root
        } else {
            binding = FragmentNewCalenderEntryBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            manager.reverseLayout = false
            binding.favoritesRV.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.favoritesRV)




            //setRecyclerViewScrollListener()

            return binding.root
        }
    }

    private fun fillTheViewToCalEntry() {
        //TODO: Mostrar apenas a receita associada
        //binding.favoritesRV.adapter = adapter.updateList(objCalEntry.recipe)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bindObservers()

        if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            objCalEntry = arguments?.getParcelable("CalenderEntry", CalenderEntry::class.java)
        } else {
            objCalEntry = arguments?.getParcelable("CalenderEntry")
        }


        if (objCalEntry!=null){
            fillTheViewToCalEntry()
        }



        binding.favoritesRV.adapter = adapter

        binding.CloseRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        // default list
        try {
            user = sharedPreference.getUserSession()
            updateView(currentTabSelected)
        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: User had no shared prefences...")
            // se não tiver shared preferences o user não tem sessão válida
            //tera um comportamento diferente offilne
            authViewModel.logoutUser()
        }

        // change listing items

        binding.nextBtn.setOnClickListener {
            updateView(++currentTabSelected)
        }

        binding.previousBtn.setOnClickListener {

            updateView(--currentTabSelected)
        }



        // form body
        binding.tagCV.setOnClickListener {
            showTagDialog()
        }

        binding.tagCV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.tagCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.tagCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }

        binding.dateCV.setOnClickListener {
           val datePicker = showDatePickerDialog()

            datePicker.addOnCancelListener {
                datePicker.dismiss()
            }

            datePicker.addOnPositiveButtonClickListener {

                if(datePicker.headerText.length == 9)
                    binding.dateValTV.text= "0"+datePicker.headerText

            }

        }

        binding.dateValTV.text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))



        binding.dateCV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.dateCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.dateCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }

        binding.timeValTV.text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        binding.timeCV.setOnClickListener {

            val clockFormat = if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val timeNow = LocalTime.now()

            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(timeNow.hour)
                    .setMinute(timeNow.minute)
                    .setTitleText("Digite a que horário pretende")
                    .build()

            picker.addOnPositiveButtonClickListener {
                binding.timeValTV.text= getString(R.string.calender_formated_date,String.format("%02d", picker.hour),String.format("%02d", picker.minute))
            }

            picker.addOnCancelListener {
                picker.dismiss()
            }



            MaterialTimePicker.Builder().setInputMode(INPUT_MODE_KEYBOARD)

            picker.show(parentFragmentManager, "TimePicker");
            picker.dialog?.setCanceledOnTouchOutside(false)



        }

        binding.timeCV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.timeCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.timeCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }

        binding.completeRegIB.setOnClickListener {

            val (valid,message) = validation()
            if (valid) {
                val (recipe_id,calenderEntryRequest) = getCalenderEntryRequest()
                if (objCalEntry != null){
                    //UPDATE calenderEntry
                    //TODO:Open dialog to confirmation ACTION
                    //TODO:Update calEntry
                }else{
                    //CREATE calenderEntry
                    calenderViewModel.createEntryOnCalender(recipe_id,calenderEntryRequest)
                }
            }
            else{
                Toast(context).showCustomToast (message, requireActivity(),ToastType.ALERT)
            }


        }
    }

    private fun showTagDialog() {
        val tags = resources.getStringArray(R.array.tagEntryCalender_array).toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_checked_text_view, tags)

        if (objCalEntry!=null){
            for ((index, tag) in tags.withIndex()) {
                if(objCalEntry!!.tag.lowercase() == tag.lowercase())
                    checkedTag=index
            }
        }

        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog)
            .setTitle("Selecione a categoria")
            .setPositiveButton("ok") { _, _ ->
                binding.tagValTV.text = tags[checkedTag]
            }
            .setSingleChoiceItems(adapter, checkedTag) { _, which ->
                // Handle the item selection here
                checkedTag = which
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create()
        builder.show()
    }

    private fun showDatePickerDialog(): MaterialDatePicker<Long> {
        var date: Long? = null
        if(objCalEntry!=null){
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            date = dateFormat.parse(objCalEntry!!.realization_date) as Nothing?
        }else{
            date = MaterialDatePicker.todayInUtcMilliseconds()
        }


        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setSelection(date)
                .build()
        datePicker.dialog?.setCanceledOnTouchOutside(false)

        datePicker.show(parentFragmentManager, "DatePicker")

        return datePicker
    }

    private fun updateView(currentTabSelected: Int) {

        when(currentTabSelected){
            0 -> {
                // salvos
                // tab title
                binding.listingTV.text = "Guardados"
                binding.listingIV.setImageResource(R.drawable.ic_favorito_active)
                binding.previousBtn.visibility = View.GONE

                //list
                val recipes = user.getSavedRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_saved)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                recipeRecyclerViewList = recipes
                adapter.updateList(recipeRecyclerViewList, user)

            }
            1 ->{
                // favoritos
                // tab title
                binding.listingTV.text = "Favoritos"
                binding.listingIV.setImageResource(R.drawable.ic_like_active)
                binding.previousBtn.visibility = View.VISIBLE
                binding.nextBtn.visibility = View.VISIBLE

                //list
                val recipes = user.getLikedRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_liked)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                recipeRecyclerViewList = recipes
                adapter.updateList(recipeRecyclerViewList, user)
            }
            2 ->{
                // criados
                // tab title
                binding.listingTV.text = "Criados"
                // todo rui falta aqui um icon para os creados
                binding.listingIV.setImageResource(R.drawable.ic_favorito_active)
                binding.nextBtn.visibility = View.GONE

                //list
                val recipes = user.getCreateRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_created)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE


                recipeRecyclerViewList = recipes
                adapter.updateList(recipeRecyclerViewList, user)

            }
            else->{

            }
        }
    }


    private fun validation():Pair<Boolean,String> {

        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))

        if (recipeRecyclerViewList.isEmpty())
            return false to "No recipe selected."

        if (localDateTime < LocalDateTime.now())
            return false to "Date should be before today."

        if (binding.tagValTV.text == getString(R.string.none))
            return false to "Tag cant be none."

        return true to ""

    }

    private fun getCalenderEntryRequest(): Pair<Int,CalenderEntryRequest> {
        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))
        return recipeRecyclerViewList[manager.findFirstCompletelyVisibleItemPosition()].id to CalenderEntryRequest(tag = binding.tagValTV.text.toString().uppercase(),formatLocalTimeToServerTime(localDateTime))
    }


    private fun bindObservers() {

        calenderViewModel.createEntryOnCalenderLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast ("Nova entrada no calendario adicionada.")
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${it.message}.")
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }


        authViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast(getString(R.string.user_had_no_shared_preferences))
                        findNavController().navigate(R.id.action_profile_to_login)
                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${it.message}.")
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

    }



}