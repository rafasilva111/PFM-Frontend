package com.example.projectfoodmanager.presentation.calendar.insertCalenderEntry

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentNewCalenderEntryBinding
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
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
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCalenderEntryFragment : Fragment() {

    // binding
    lateinit var binding: FragmentNewCalenderEntryBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()
    private val calendarViewModel by activityViewModels<CalendarViewModel>()

    // constants
    private val TAG: String = "NewCalenderEntryFragment"
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private var checkedTag: Int= 0

    lateinit var user: User
    private var currentTabSelected :Int = 0

    private var objRecipe: Recipe? = null
    private var recipeRecyclerViewList: MutableList<Recipe> = mutableListOf()

    private var chosenDate: Long?=null
    // injects

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager


    //adapters
    private val adapter by lazy {
        NewCalenderEntryFragmentListingAdapter(
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


            if (!this::binding.isInitialized) {
                binding = FragmentNewCalenderEntryBinding.inflate(layoutInflater)
            }

            // when objRecipe is supplied from recipeDetail
            objRecipe = if (Build.VERSION.SDK_INT >= 33) {
                // TIRAMISU
                arguments?.getParcelable("Recipe", Recipe::class.java)
            } else {
                arguments?.getParcelable("Recipe")
            }

            return binding.root
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUI() {


        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.navigationBarColor = Color.TRANSPARENT
        requireActivity().window.statusBarColor = Color.TRANSPARENT

        manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        manager.reverseLayout = false
        binding.favoritesRV.layoutManager = manager
        snapHelper.attachToRecyclerView(binding.favoritesRV)


        binding.favoritesRV.adapter = adapter

        binding.CloseRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        // default list

        user = sharedPreference.getUserSession()


        // se viewer recipe
        if (objRecipe == null){
            updateView(currentTabSelected)
        }
        else{
            updateView(3)
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
            showDatePickerDialog()



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

            showTimePickerDialog()

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
                calendarViewModel.createEntryOnCalendar(recipe_id,calenderEntryRequest)
            }
            else{
                Toast(context).showCustomToast (message, requireActivity(),ToastType.ALERT)
            }


        }
    }

    private fun showTimePickerDialog() {
        val clockFormat =
            if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timeNow = LocalTime.now()

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(timeNow.hour)
                .setMinute(timeNow.minute)
                .setTitleText("Digite a que horário pretende")
                .build()

        picker.addOnPositiveButtonClickListener {
            binding.timeValTV.text = getString(
                R.string.calender_formated_date,
                String.format("%02d", picker.hour),
                String.format("%02d", picker.minute)
            )
        }

        picker.addOnCancelListener {
            picker.dismiss()
        }



        MaterialTimePicker.Builder().setInputMode(INPUT_MODE_KEYBOARD)

        picker.show(parentFragmentManager, "TimePicker");
        picker.dialog?.setCanceledOnTouchOutside(false)
    }

    private fun showTagDialog() {
        val tags = resources.getStringArray(R.array.tagEntryCalender_array).toList()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_checked_text_view, tags)

       // val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog)
        val builder = MaterialAlertDialogBuilder(requireActivity())
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

    private fun showDatePickerDialog() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(if (chosenDate == null )
                    selectedDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                else
                    chosenDate)
                .build()
        datePicker.dialog?.setCanceledOnTouchOutside(false)

        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.addOnPositiveButtonClickListener {selection->
            chosenDate = selection
            val selectedDate = Date(selection)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(selectedDate)

            binding.dateValTV.setText(formattedDate)

        }

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
                // todo rui falta aqui um icon para os criados
                binding.listingIV.setImageResource(R.drawable.ic_favorito_active)


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
            3 ->{
                // todas as receitas
                // tab title
                binding.listingTV.text = "Todas as receitas"
                // todo rui falta aqui um icon para todas as receitas
                binding.listingIV.setImageResource(R.drawable.ic_baseline_menu_book_24)
                binding.nextBtn.visibility = View.GONE

                //list
                // todo rafa get all recipes
                if(objRecipe == null){

                }
                else{
                    recipeRecyclerViewList = mutableListOf()
                    recipeRecyclerViewList.add(objRecipe!!)
                    adapter.updateList(recipeRecyclerViewList, user)
                }
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

    private fun getCalenderEntryRequest(): Pair<Int, CalenderEntryRequest> {
        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))
        return recipeRecyclerViewList[manager.findFirstCompletelyVisibleItemPosition()].id to CalenderEntryRequest(tag = binding.tagValTV.text.toString().uppercase(),formatLocalTimeToServerTime(localDateTime))
    }

    private fun bindObservers() {

        calendarViewModel.createEntryOnCalendarLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.detailsPanel.show()
                        binding.progressBar.hide()
                        toast ("Nova entrada no calendario adicionada.")
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${it.message}.")
                    }
                    is NetworkResult.Loading -> {
                        binding.detailsPanel.hide()
                        binding.progressBar.show()

                    }
                }
            }
        }

    }

    override fun onDestroy() {

        /** Reset navigation back color */
        requireActivity().window.navigationBarColor = requireContext().getColor(R.color.main_color)

        super.onDestroy()
    }
}