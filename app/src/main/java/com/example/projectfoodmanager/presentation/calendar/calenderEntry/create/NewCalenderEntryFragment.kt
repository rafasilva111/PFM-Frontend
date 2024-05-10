package com.example.projectfoodmanager.presentation.calendar.calenderEntry.create

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.calender.CalenderEntryDTO
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentNewCalenderEntryBinding
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
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
    private val calendarViewModel by activityViewModels<CalendarViewModel>()

    // constants
    private val TAG: String = "NewCalenderEntryFragment"
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager


    lateinit var user: User
    private var currentTabSelected :Int = 0

    private var objRecipe: Recipe? = null
    private var recipeRecyclerViewList: MutableList<Recipe> = mutableListOf()

    private var chosenDate: Long?=null

    private lateinit var tags : List<String>
    private lateinit var portions : List<String>

    private var checkedTag: Int= -1
    private var checkedPortion: Int= -1

    private lateinit var tagDialog: AlertDialog
    private lateinit var dateDialog: MaterialDatePicker<Long>
    private lateinit var timeDialog: MaterialTimePicker
    private lateinit var portionDialog: AlertDialog
    private lateinit var costumPortionNumberDialog: AlertDialog
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

            return binding.root
        }
    override fun onCreate(savedInstanceState: Bundle?) {


        tags =  resources.getStringArray(R.array.tagEntryCalender_array).toList()
        portions = resources.getStringArray(R.array.portionEntryCalender_array).toList()

        objRecipe = if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            arguments?.getParcelable("Recipe", Recipe::class.java)
        } else {
            arguments?.getParcelable("Recipe")
        }

        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUI() {

        /**
         * General
         */

        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.navigationBarColor = Color.TRANSPARENT
        requireActivity().window.statusBarColor = Color.TRANSPARENT

        manager = LinearLayoutManager(activity)
        manager.orientation = LinearLayoutManager.HORIZONTAL
        manager.reverseLayout = false
        binding.favoritesRV.layoutManager = manager
        snapHelper.attachToRecyclerView(binding.favoritesRV)


        binding.favoritesRV.adapter = adapter

        user = sharedPreference.getUserSession()


        binding.dateValTV.text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.timeValTV.text = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        /** If we receive recipe as argument*/

        if (objRecipe == null){
            updateView(currentTabSelected)
        }
        else{
            updateView(3)
        }

        /** Create Dialogs*/

        /** Tag Dialog */
        tagDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.COMMON_DIALOG_CATEGORY_TITLE))
            .setSingleChoiceItems(ArrayAdapter(requireContext(), R.layout.item_checked_text_view, tags), checkedTag) { dialog, which ->
                // Handle the item selection here
                checkedTag = which
                binding.tagValTV.text = tags[checkedTag]
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.COMMON_DIALOG_CANCEL)) { dialog, _ ->
                dialog.dismiss()
            }.create()

        /** Date Dialog */
        dateDialog =MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.COMMON_DIALOG_DATE_TITLE))
                .setSelection(
                    if (chosenDate == null )
                        selectedDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                    else
                        chosenDate
                )
                .build()


        dateDialog.addOnCancelListener {
            dateDialog.dismiss()
        }

        dateDialog.addOnPositiveButtonClickListener {selection->
            chosenDate = selection
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            binding.dateValTV.text = formatter.format(Date(selection)).toString()

        }


        /** Time Dialog */
        val timeNow = LocalTime.now()

        timeDialog =MaterialTimePicker.Builder()
                    .setTimeFormat(if (is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
                    .setHour(timeNow.hour)
                    .setMinute(timeNow.minute)
                    .setTitleText(getString(R.string.COMMON_DIALOG_TIME_TITLE))

                    .build()

        timeDialog.addOnPositiveButtonClickListener {
            binding.timeValTV.text = getString(
                R.string.calender_formated_date,
                String.format("%02d", timeDialog.hour),
                String.format("%02d", timeDialog.minute)
            )
        }

        timeDialog.addOnCancelListener {
            timeDialog.dismiss()
        }

        /** Portion Dialog */

        portionDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.COMMON_DIALOG_PORTION_TITLE))
            .setSingleChoiceItems(ArrayAdapter(requireContext(), R.layout.item_checked_text_view, portions), checkedTag) { dialog, which ->

                when (which){
                    0 -> {
                        checkedPortion = user.userPortion
                        binding.portionValTv.text = getString(R.string.FRAGMENT_NEW_CALENDER_ENTRY,checkedPortion)
                    }
                    1 -> {
                        checkedPortion = 1
                        binding.portionValTv.text = getString(R.string.FRAGMENT_NEW_CALENDER_ENTRY,checkedPortion)
                    }
                    2 ->{
                        costumPortionNumberDialog.show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.COMMON_DIALOG_CANCEL)) { dialog, _ ->
                dialog.dismiss()
            }.create()


        val numberPicker = NumberPicker(requireContext())
        numberPicker.minValue = 1
        numberPicker.maxValue = 16

        costumPortionNumberDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.CALENDER_PORTION_DIALOG_NUMBER_TITLE))
            .setView(numberPicker)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.COMMON_DIALOG_SELECT)) { dialog, _ ->
                checkedPortion = numberPicker.value
                binding.portionValTv.text = getString(R.string.FRAGMENT_NEW_CALENDER_ENTRY,checkedPortion)

                dialog.dismiss()
            }
            .create()

        /**
         * Functions
         */

        binding.tagCV.setOnClickListener {
            tagDialog.show()
        }

        binding.dateCV.setOnClickListener {
            dateDialog.show(parentFragmentManager, "DatePicker")

        }

        binding.timeCV.setOnClickListener {
            timeDialog.show(parentFragmentManager, "TimePicker")
        }

        binding.portionCV.setOnClickListener {
            portionDialog.show()
        }


        /**
         * Navigation
         */

        binding.backBT.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nextBtn.setOnClickListener {
            updateView(++currentTabSelected)
        }

        binding.previousBtn.setOnClickListener {
            updateView(--currentTabSelected)
        }

        binding.completeRegIB.setOnClickListener {

            val (valid,message) = validation()
            if (valid) {
                val (recipe_id,calenderEntryRequest) = getCalenderEntryRequest()
                calendarViewModel.createEntryOnCalendar(recipe_id,calenderEntryRequest)
            }
            else{
                toast(message, ToastType.ALERT)
            }
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
                val recipes = user.savedRecipes
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
                val recipes = user.likedRecipes
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
                val recipes = user.createdRecipes
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

    private fun getCalenderEntryRequest(): Pair<Int, CalenderEntryDTO> {
        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))
        return recipeRecyclerViewList[manager.findFirstCompletelyVisibleItemPosition()].id to CalenderEntryDTO(
            tag = binding.tagValTV.text.toString().uppercase(),
            portion=checkedPortion,
            realizationDate = formatLocalTimeToServerTime(localDateTime)
        )
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