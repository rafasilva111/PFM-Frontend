package com.example.projectfoodmanager.presentation.calendar

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.databinding.FragmentCalendarBinding
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.currentDate
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInWeekArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToDateString
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfMonth
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfWeek
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment(), ImageLoadingListener {

    /** Binding */
    lateinit var binding: FragmentCalendarBinding

    /** ViewModels */
    private val userViewModel by activityViewModels<UserViewModel>()
    private val calenderViewModel by activityViewModels<CalendarViewModel>()

    /** Constants */
    val TAG: String = "CalenderFragment"

    private val calenderEntriesToBeChecked: MutableList<Int> = mutableListOf()
    private val calenderEntriesToBeUnchecked: MutableList<Int> = mutableListOf()

    private lateinit var manager: LinearLayoutManager

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Adapters */

    private val adapterCalMonth by lazy {
        CalendarAdapter(
            daysInMonthArray(
                selectedDate
            ),
            onItemClicked = { selectedDate ->
                binding.registersDateTV.text= formatLocalDateToDateString(selectedDate)

                val calenderEntries = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())
                // check if any entry in shared if not try getting it from the server
                if (calenderEntries == null)
                    calenderViewModel.getEntryOnCalendar(selectedDate.atStartOfDay())
                else{
                    // update calender entrys list
                    adapterEntry.setList(calenderEntries)
                    binding.nRegistersTV.text= adapterEntry.itemCount.toString()
                    // show no recipes text
                    if (calenderEntries.isEmpty())
                        binding.emptyRegTV.show()
                    else {
                        binding.emptyRegTV.hide()
                    }
                }


            }
        )
    }

    private val adapterCalWeekly by lazy {
        CalendarAdapter(
            daysInWeekArray(
                selectedDate
            ),
            onItemClicked = { selectedDate ->
                binding.registersDateTV.text= formatLocalDateToDateString(selectedDate)

                val calenderEntry = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())

                binding.calEntrysRV.visibility = View.INVISIBLE
                binding.progressBar.show()

                // Update Calender checked entries
                if (calenderEntriesToBeChecked.isNotEmpty() or calenderEntriesToBeUnchecked.isNotEmpty())
                    calenderViewModel.checkCalenderEntries(
                        CalenderEntryCheckListRequest(
                            checked = calenderEntriesToBeChecked,
                            unchecked = calenderEntriesToBeUnchecked
                        )
                    )

                // check if any entry in shared if not try getting it from the server
                if (calenderEntry == null)
                    calenderViewModel.getEntryOnCalendar(selectedDate.atStartOfDay())
                else{
                    // update calender entrys list
                    adapterEntry.setList(calenderEntry)
                    binding.progressBar.hide()

                    // show no recipes text
                    binding.emptyRegTV.isVisible = calenderEntry.isEmpty()

                }
            }
        )
    }

    private val adapterEntry by lazy{
        CalendarEntryAdapter(
            onItemClicked = { _, item ->

                findNavController().navigate(R.id.action_calendarFragment_to_calendarEntryDetailFragment,Bundle().apply {
                    putParcelable("calendar_entry",item)
                })
                changeMenuVisibility(false,activity)
            },
            onDoneClicked = { checkDone, item->


                if (checkDone){

                    if(item.id in calenderEntriesToBeUnchecked)
                        calenderEntriesToBeUnchecked.remove(item.id)

                    calenderEntriesToBeChecked.add(item.id)
                }
                else{
                    if(item.id in calenderEntriesToBeChecked)
                        calenderEntriesToBeChecked.remove(item.id)

                    calenderEntriesToBeUnchecked.add(item.id)
                }


            },
            this
        )
    }


    /** Interfaces */

/*    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            adapterEntry.imagesLoaded++
            if (adapterEntry.imagesLoaded >= manager.findLastCompletelyVisibleItemPosition()) {
                binding.progressBar.hide()
                binding.calEntrysRV.visibility = View.VISIBLE
            }
            else{
                binding.calEntrysRV.visibility = View.INVISIBLE
            }

        }
    }*/

    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            adapterEntry.imagesLoaded++

            val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
            val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1

            // If all visible images are loaded, hide the progress bar
            if (adapterEntry.imagesLoaded >= visibleItemCount) {
                binding.progressBar.hide()
                binding.calEntrysRV.visibility = View.VISIBLE
            }
        }
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentCalendarBinding.inflate(layoutInflater)
        }


        bindObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()

    }

    override fun onStart() {

        loadUI()
        super.onStart()
    }

    override fun onPause() {

        if (calenderEntriesToBeChecked.isNotEmpty() or calenderEntriesToBeUnchecked.isNotEmpty())
            calenderViewModel.checkCalenderEntries(
                CalenderEntryCheckListRequest(
                    checked = calenderEntriesToBeChecked,
                    unchecked = calenderEntriesToBeUnchecked
                )
            )

        binding.calEntrysRV.visibility = View.INVISIBLE
        binding.progressBar.show()

        super.onPause()
    }

    /**
     *  General
     * */

    private fun setUI() {
        /**
         *  General
         * */

        binding.registersDateTV.text= formatLocalDateToDateString(selectedDate)

        manager = LinearLayoutManager(activity)
        binding.calEntrysRV.layoutManager = manager
        binding.calEntrysRV.adapter = adapterEntry
        binding.calEntrysRV.itemAnimator = null

        /** Check for user portion */
        if( sharedPreference.isFirstPortionAsk()) {
            sharedPreference.saveFirstPortionAsk()
            askUserPortionPreference()
        }


        /**
         *  Navigation
         * */

        binding.addRegisterIB.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_newCalenderEntryFragment)
            changeMenuVisibility(false,activity)

        }

        binding.monthViewBtn.setOnClickListener {
            setMonthView()
        }

        binding.weeklyViewBtn.setOnClickListener {
            setWeeklyView()
        }

        binding.previousBtn.setOnClickListener {
            currentDate = if (weeklyViewSelected)
                currentDate.minusDays(7)
            else
                currentDate.minusMonths(1)
            updateCalenderView()
        }

        binding.nextBtn.setOnClickListener {
            currentDate = if (weeklyViewSelected)
                currentDate.plusDays(7)
            else
                currentDate.plusMonths(1)

            updateCalenderView()
        }

        binding.addBasketIB.setOnClickListener {
            navigateToCalenderShoppingList()
        }

    }

    private fun loadUI() {

        /**
         *  General
         * */
        val activity = requireActivity()

        changeTheme(false, activity, requireContext())
        changeMenuVisibility(true,activity)


        // set Calendar View
        if (weeklyViewSelected)
            setWeeklyView()
        else
            setMonthView()


        val calendarEntry = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())
        calendarEntry?.let {

            if (calendarEntry.isEmpty()) {
                binding.emptyRegTV.show()
            } else {
                binding.emptyRegTV.hide()
                adapterEntry.setList(it)
            }

            binding.nRegistersTV.text = adapterEntry.itemCount.toString()
        }
        //todo else vai buscar a net
    }

    private fun bindObservers() {
        calenderViewModel.getEntryOnCalendarLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        binding.calEntrysRV.show()

                        adapterEntry.setList(it.data!!.result)

                        binding.nRegistersTV.text = it.data.result.size.toString()

                        if (it.data.result.size == 0){
                            binding.emptyRegTV.show()
                        }


                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                        binding.calEntrysRV.hide()
                        binding.emptyRegTV.hide()

                    }
                }
            }
        }

        calenderViewModel.patchCalendarEntryLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        if(it.data!!.checkedDone){
                            toast("Refeição consumida")
                        }else{
                            toast("Refeição não consumida")
                        }

                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.show()

                    }
                }
            }
        }

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.DATA_UPDATED))
                        result.data?.let { sharedPreference.saveUserSession(it) }
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }
    }

    /**
     *  Functions
     * */

    private fun askUserPortionPreference() {

        val myDialog = Dialog(requireContext())
        myDialog.setContentView(layoutInflater.inflate(R.layout.dialog_portion_preferenced_by_user, null))

        // create alert dialog
        myDialog.setCancelable(false)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val numberPicker = myDialog.findViewById<NumberPicker>(R.id.number_picker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 20

        /** Select button  */
        myDialog.findViewById<Button>(R.id.use_first_recipes_portion).setOnClickListener {
            userViewModel.updateUser(UserRequest(user_portion = numberPicker.value))
            myDialog.dismiss()
        }

        /** Ignore button  */
        myDialog.findViewById<Button>(R.id.use_seconds_recipes_portion).setOnClickListener {
            userViewModel.updateUser(UserRequest(user_portion = 0))
            myDialog.dismiss()
        }

        myDialog.show()
    }

    private fun navigateToCalenderShoppingList() {
        val toFromDate: Pair<LocalDate,LocalDate> = if (binding.calMonthRV.visibility == View.VISIBLE)
            getStartAndEndOfMonth(currentDate)
        else
            getStartAndEndOfWeek(currentDate)
        //action_calendarFragment_to_calenderShoppingListFragment
        findNavController().navigate(R.id.action_calendarFragment_to_calenderIngredientsFragment,Bundle().apply {
            putBoolean("calender_view",binding.calMonthRV.visibility == View.VISIBLE)
            putString("from_date",toFromDate.first.atStartOfDay().toString())
            putString("to_date",toFromDate.second.atStartOfDay().toString())
        })
        changeMenuVisibility(false,activity)
    }

    private fun setMonthView() {

        weeklyViewSelected = false

        binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
        binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

        binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
        binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))

        binding.calMonthRV.visibility = View.VISIBLE
        binding.calWeeklyRV.visibility = View.GONE


        currentDate = selectedDate


        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calMonthRV.layoutManager = layoutManager
        binding.calMonthRV.adapter = adapterCalMonth

        updateCalenderView()
    }

    private fun setWeeklyView() {

        weeklyViewSelected = true

        binding.weeklyViewBtn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
        binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

        binding.monthViewBtn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
        binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))

        binding.calMonthRV.visibility = View.GONE
        binding.calWeeklyRV.visibility = View.VISIBLE


        currentDate = selectedDate

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity?.applicationContext, 7)
        binding.calWeeklyRV.layoutManager = layoutManager
        binding.calWeeklyRV.adapter = adapterCalWeekly


        updateCalenderView()
    }

    private fun updateCalenderView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.replaceFirstChar { it.uppercase() }


        if (weeklyViewSelected)
            adapterCalWeekly.updateList(
                daysInWeekArray(
                    currentDate
                )
            )
        else
            adapterCalMonth.updateList(
                daysInMonthArray(
                    currentDate
                )
            )



    }

    /**
     *  Object
     * */

    companion object{
        var weeklyViewSelected: Boolean = true
    }
}