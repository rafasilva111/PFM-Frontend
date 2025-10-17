package com.example.projectfoodmanager.presentation.calendar

import CalendarEntryAdapter
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.databinding.FragmentCalendarBinding
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.currentDate
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInWeekArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToDateString
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfMonth
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfWeek
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.PreloadCalenderEntriesModelProvider
import com.example.projectfoodmanager.util.ToastType
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.util.show
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment(), ImageLoadingListener {

    /** Binding */
    private lateinit var binding: FragmentCalendarBinding

    /** ViewModels */
    private val userViewModel by activityViewModels<UserViewModel>()
    private val calenderViewModel by activityViewModels<CalendarViewModel>()

    /** Constants */
    val TAG: String = "CalenderFragment"

    /** Layout Manager & Preloader */
    private lateinit var manager: LinearLayoutManager
    private lateinit var preloadModelProvider: PreloadCalenderEntriesModelProvider

    /** Data helpers */
    private val calenderEntriesToBeChecked: MutableList<Int> = mutableListOf()
    private val calenderEntriesToBeUnchecked: MutableList<Int> = mutableListOf()

    /** Injections */
    @Inject lateinit var sharedPreference: SharedPreference
    @Inject lateinit var tokenManager: TokenManager

    /** Adapters */
    private val adapterCalMonth by lazy {
        CalendarAdapter(
            daysInMonthArray(selectedDate),
            onItemClicked = { selectedDate -> getCalendarEntriesForDate(selectedDate) }
        )
    }

    private val adapterCalWeekly by lazy {
        CalendarAdapter(
            daysInWeekArray(selectedDate),
            onItemClicked = { selectedDate -> getCalendarEntriesForDate(selectedDate) }
        )
    }

    private val adapterCalenderEntries by lazy {
        CalendarEntryAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(
                    R.id.action_calendarFragment_to_calendarEntryDetailFragment,
                    Bundle().apply { putParcelable("calendar_entry", item) }
                )
                changeMenuVisibility(false, activity)
            },
            onDoneClicked = { checkDone, item ->
                if (checkDone) {
                    calenderEntriesToBeUnchecked.remove(item.id)
                    calenderEntriesToBeChecked.add(item.id)
                } else {
                    calenderEntriesToBeChecked.remove(item.id)
                    calenderEntriesToBeUnchecked.add(item.id)
                }
            },
            this
        )
    }

    /** Interfaces */
    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            if (binding.calendarEntriesRecyclerView.visibility != View.VISIBLE &&
                binding.calendarEntriesRecyclerView.visibility != View.GONE
            ) {
                adapterCalenderEntries.imagesLoaded++

                val firstVisible = manager.findFirstVisibleItemPosition()
                val lastVisible = manager.findLastVisibleItemPosition()
                val visibleCount = lastVisible - firstVisible + 1

                if (adapterCalenderEntries.imagesLoaded >= visibleCount) {
                    showCalendarEntriesRecyclerView()
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Android Lifecycle
    // -----------------------------------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /** Inflate binding */
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        /** Set up RecyclerView LayoutManager */
        manager = LinearLayoutManager(requireContext()).apply {
            orientation = LinearLayoutManager.VERTICAL
            reverseLayout = false
        }
        binding.calendarEntriesRecyclerView.layoutManager = manager
        binding.calendarEntriesRecyclerView.itemAnimator = null

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /** Setup UI and bind observers */
        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        /** Load data into UI */
        loadUI()
        super.onStart()
    }

    override fun onStop() {

        /** Update checked/unchecked entries */
        if (calenderEntriesToBeChecked.isNotEmpty() or calenderEntriesToBeUnchecked.isNotEmpty())
            calenderViewModel.checkCalenderEntries(
                CalenderEntryCheckListRequest(
                    checked = calenderEntriesToBeChecked,
                    unchecked = calenderEntriesToBeUnchecked
                )
            )

        super.onStop()
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 UI Setup
    // -----------------------------------------------------------------------------------------

    private fun setUI() {

        /** General */
        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false, activity, requireContext())

        /** Set Adapters */
        binding.calendarEntriesRecyclerView.adapter = adapterCalenderEntries

        /** Default UI State */
        binding.registersDateTV.text = formatLocalDateToDateString(selectedDate)
        hideCalendarEntriesRecyclerView()

        /** Portion Dialog (only first time) */
        if (sharedPreference.isFirstPortionAsk()) {
            sharedPreference.saveFirstPortionAsk()
            askUserPortionPreference()
        }

        /** Navigation Buttons */
        binding.addRegisterIB.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_newCalenderEntryFragment)
            changeMenuVisibility(false, activity)
        }

        binding.addBasketIB.setOnClickListener { navigateToCalenderShoppingList() }

        /** Calendar View Controls */
        binding.monthViewBtn.setOnClickListener { setMonthView() }
        binding.weeklyViewBtn.setOnClickListener { setWeeklyView() }

        binding.previousBtn.setOnClickListener { changeCalendarPeriod(-1) }
        binding.nextBtn.setOnClickListener { changeCalendarPeriod(1) }
    }

    private fun loadUI() {
        /** Called when Fragment becomes visible */
        val activity = requireActivity()
        changeTheme(false, activity, requireContext())
        changeMenuVisibility(true, activity)

        if (weeklyViewSelected)
            setWeeklyView()
        else
            setMonthView()

        /** Load data for selected date */
        getCalendarEntriesForDate(selectedDate)
    }

    private fun bindObservers() {
        /** Observe calendar data */
        calenderViewModel.getCalendarDatedEntryListLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val calenderData = result.data!!.result.values.first()
                        binding.nRegistersTV.text = calenderData.size.toString()

                        if (calenderData.isEmpty()) {
                            binding.emptyRegTV.show()
                            binding.progressBar.hide()
                            return@let
                        }

                        adapterCalenderEntries.setItems(calenderData)
                        setItemsToImagePreload(calenderData)
                    }

                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }

                    is NetworkResult.Loading -> binding.emptyRegTV.hide()
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Core Logic
    // -----------------------------------------------------------------------------------------

    private fun getCalendarEntriesForDate(selectedDate: LocalDate) {
        binding.registersDateTV.text = formatLocalDateToDateString(selectedDate)
        binding.calendarEntriesRecyclerView.hide()
        binding.progressBar.show()

        if (isOnline(requireContext())) {
            calenderViewModel.getCalendarDatedEntryList(
                fromDate = selectedDate.atStartOfDay(),
                toDate = selectedDate.atStartOfDay().plusDays(1).minusSeconds(1),
                cleanseOldRegistry = true
            )
        } else {
            val calendarEntry = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())
            calendarEntry?.let {
                adapterCalenderEntries.setItems(it)
                binding.nRegistersTV.text = adapterCalenderEntries.itemCount.toString()

                if (it.isEmpty()) binding.emptyRegTV.show()
                else binding.emptyRegTV.hide()
            }
        }

        hideCalendarEntriesRecyclerView()
    }


    private fun showCalendarEntriesRecyclerView() {
        binding.progressBar.hide()
        binding.calendarEntriesRecyclerView.show()
        adapterCalenderEntries.imagesLoaded = 0
    }

    private fun hideCalendarEntriesRecyclerView() {
        binding.progressBar.show()
        binding.calendarEntriesRecyclerView.visibility = View.INVISIBLE
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Preloading Helpers
    // -----------------------------------------------------------------------------------------

    private fun setItemsToImagePreload(list: MutableList<CalenderEntry>) {
        if (!::preloadModelProvider.isInitialized) {
            initImagePreload(list)
        }
        preloadModelProvider.setItems(list)
    }

    private fun initImagePreload(list: MutableList<CalenderEntry>) {
        preloadModelProvider = PreloadCalenderEntriesModelProvider(list, requireContext())
        val preloader = RecyclerViewPreloader(
            Glide.with(this),
            preloadModelProvider,
            ViewPreloadSizeProvider(),
            10
        )
        binding.calendarEntriesRecyclerView.addOnScrollListener(preloader)
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Dialogs & Navigation
    // -----------------------------------------------------------------------------------------

    private fun askUserPortionPreference() {
        val myDialog = Dialog(requireContext())
        myDialog.setContentView(layoutInflater.inflate(R.layout.dialog_portion_preferenced_by_user, null))
        myDialog.setCancelable(false)
        myDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val numberPicker = myDialog.findViewById<NumberPicker>(R.id.number_picker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 20

        /** Select button */
        myDialog.findViewById<Button>(R.id.use_first_recipes_portion).setOnClickListener {
            userViewModel.updateUser(UserRequest(user_portion = numberPicker.value))
            myDialog.dismiss()
        }

        /** Ignore button */
        myDialog.findViewById<Button>(R.id.use_seconds_recipes_portion).setOnClickListener {
            userViewModel.updateUser(UserRequest(user_portion = 0))
            myDialog.dismiss()
        }

        myDialog.show()
    }

    private fun navigateToCalenderShoppingList() {
        val (from, to) = if (binding.calMonthRV.isVisible)
            getStartAndEndOfMonth(currentDate)
        else
            getStartAndEndOfWeek(currentDate)

        findNavController().navigate(
            R.id.action_calendarFragment_to_calenderIngredientsFragment,
            Bundle().apply {
                putBoolean("calender_view", binding.calMonthRV.isVisible)
                putString("from_date", from.atStartOfDay().toString())
                putString("to_date", to.atStartOfDay().toString())
            }
        )
        changeMenuVisibility(false, activity)
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Calendar View Controls
    // -----------------------------------------------------------------------------------------

    private fun setMonthView() {
        weeklyViewSelected = false

        binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
        binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))

        binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grayLightBTN))
        binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        binding.calMonthRV.visibility = View.VISIBLE
        binding.calWeeklyRV.visibility = View.GONE

        currentDate = selectedDate

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        binding.calMonthRV.layoutManager = layoutManager
        binding.calMonthRV.adapter = adapterCalMonth

        updateCalenderView()
    }

    private fun setWeeklyView() {
        weeklyViewSelected = true

        binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
        binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))

        binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grayLightBTN))
        binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        binding.calMonthRV.visibility = View.GONE
        binding.calWeeklyRV.visibility = View.VISIBLE

        currentDate = selectedDate

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireContext(), 7)
        binding.calWeeklyRV.layoutManager = layoutManager
        binding.calWeeklyRV.adapter = adapterCalWeekly

        updateCalenderView()
    }

    private fun changeCalendarPeriod(direction: Int) {
        currentDate = if (weeklyViewSelected)
            currentDate.plusDays((7 * direction).toLong())
        else
            currentDate.plusMonths((1 * direction).toLong())
        updateCalenderView()
    }

    private fun updateCalenderView() {
        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.replaceFirstChar { it.uppercase() }

        if (weeklyViewSelected)
            adapterCalWeekly.updateList(daysInWeekArray(currentDate))
        else
            adapterCalMonth.updateList(daysInMonthArray(currentDate))
    }

    // -----------------------------------------------------------------------------------------
    // 🔹 Companion Object
    // -----------------------------------------------------------------------------------------
    companion object {
        var weeklyViewSelected: Boolean = true
    }
}
