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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.calender.CalenderEntryDTO
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.databinding.FragmentCalendarBinding
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.currentDate
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.daysInWeekArray
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToFormatDate
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfMonth
import com.example.projectfoodmanager.util.Helper.Companion.getStartAndEndOfWeek
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    // binding
    lateinit var binding: FragmentCalendarBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()
    private val calenderViewModel by activityViewModels<CalendarViewModel>()

    // constants
    val TAG: String = "CalenderFragment"

    private val calenderEntriesToBeChecked: MutableList<CalenderEntryDTO> = mutableListOf()

    // injects

    @Inject
    lateinit var sharedPreference: SharedPreference

    //adapters
    private val adapterCalMonth by lazy {
        CalendarAdapter(
            daysInMonthArray(
                currentDate
            ),
            onItemClicked = { selectedDate ->
                binding.registersDateTV.text= formatLocalDateToFormatDate(selectedDate)

                val calenderEntry = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())
                // check if any entry in shared if not try getting it from the server
                if (calenderEntry == null)
                    calenderViewModel.getEntryOnCalendar(selectedDate.atStartOfDay())
                else{
                    // update calender entrys list
                    adapterEntry.updateList(calenderEntry)
                    binding.nRegistersTV.text= adapterEntry.itemCount.toString()
                    // show no recipes text
                    if (calenderEntry.isEmpty())
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
                currentDate
            ),
            onItemClicked = { selectedDate ->
                //TODO: Confirmar com o rafa
                binding.registersDateTV.text= formatLocalDateToFormatDate(selectedDate)

                val calenderEntry = sharedPreference.getEntryOnCalendar(selectedDate.atStartOfDay())
                // check if any entry in shared if not try getting it from the server
                if (calenderEntry == null)
                    calenderViewModel.getEntryOnCalendar(selectedDate.atStartOfDay())
                else{
                    // update calender entrys list
                    adapterEntry.updateList(calenderEntry)
                    // show no recipes text
                    if (calenderEntry.isEmpty())
                        binding.emptyRegTV.show()
                    else {
                        binding.emptyRegTV.hide()
                    }
                }
            }
        )
    }

    private val adapterEntry by lazy{
        CalendarEntryAdapter(
            onItemClicked = { _, item ->

                findNavController().navigate(R.id.action_calendarFragment_to_calendarEntryDetailFragment,Bundle().apply {
                    putParcelable("CalenderEntry",item)
                })
                changeMenuVisibility(false,activity)
            },
            onDoneClicked = { checkDone, item->
                if (CalenderEntryDTO(id = item.id,checkedDone = !checkDone) !in calenderEntriesToBeChecked)
                    calenderEntriesToBeChecked.add(CalenderEntryDTO(id = item.id, checkedDone = checkDone))
                else
                    calenderEntriesToBeChecked.remove(CalenderEntryDTO(id = item.id, checkedDone = !checkDone))
            }
        )
    }


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

    private fun setUI() {
        /**
         *  General
         * */

        binding.registersDateTV.text= formatLocalDateToFormatDate(selectedDate)
        binding.nRegistersTV.text = nrRecipes

        binding.calEntrysRV.layoutManager = LinearLayoutManager(activity)
        binding.calEntrysRV.adapter = adapterEntry


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
            binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
            binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

            binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
            binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))

        }


        binding.weeklyViewBtn.setOnClickListener {
            setWeeklyView()

            binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
            binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

            binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
            binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))


        }


        binding.previousBtn.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            updateCalenderView()
        }

        binding.nextBtn.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
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

        setMonthView()

        val targetDate = if (selectedDate != currentDate) selectedDate else currentDate
        val calendarEntry = sharedPreference.getEntryOnCalendar(targetDate.atStartOfDay())
        calendarEntry?.let {
            adapterEntry.updateList(it)
            if (calendarEntry.isEmpty()) {
                binding.emptyRegTV.show()
            } else {
                binding.emptyRegTV.hide()
            }
            binding.nRegistersTV.text = adapterEntry.itemCount.toString()
        }
    }

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
            userViewModel.updateUser(UserDTO(user_portion = numberPicker.value))
            myDialog.dismiss()
        }

        /** Ignore button  */
        myDialog.findViewById<Button>(R.id.use_seconds_recipes_portion).setOnClickListener {
            userViewModel.updateUser(UserDTO(user_portion = 0))
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

    override fun onStart() {

        loadUI()
        super.onStart()
    }

    private fun setMonthView() {
        binding.calWeeklyRV.visibility = View.INVISIBLE
        binding.calMonthRV.visibility = View.VISIBLE

        currentDate = LocalDate.now()

        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.replaceFirstChar { it.uppercase() }

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calMonthRV.layoutManager = layoutManager
        binding.calMonthRV.adapter = adapterCalMonth
    }

    private fun setWeeklyView() {

        binding.calMonthRV.visibility = View.GONE
        binding.calWeeklyRV.visibility = View.VISIBLE


        currentDate = LocalDate.now()
        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.replaceFirstChar { it.uppercase() }
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity?.applicationContext, 7)
        binding.calWeeklyRV.layoutManager = layoutManager
        binding.calWeeklyRV.adapter = adapterCalWeekly

        //setEventAdapter()
    }

    private fun updateCalenderView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!

        if (binding.calMonthRV.visibility == View.VISIBLE) {
            adapterCalMonth.updateList(
                daysInMonthArray(
                    currentDate
                )
            )
        } else {
            adapterCalWeekly.updateList(
                daysInWeekArray(
                    currentDate
                )
            )
        }

    }

    private fun bindObservers() {
        calenderViewModel.getEntryOnCalendarLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        binding.calEntrysRV.show()

                        adapterEntry.updateList(it.data!!.result)

                        if (it.data.result.size != 0){
                            nrRecipes= it.data.result.size.toString()
                        }else{
                            nrRecipes= "0"
                            binding.emptyRegTV.show()
                        }
                        binding.nRegistersTV.text = nrRecipes

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

                        if(it.data!!.checked_done){
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


    override fun onPause() {

        // delete notifications
        if (calenderEntriesToBeChecked.isNotEmpty())
            calenderViewModel.checkCalenderEntries(CalenderEntryListUpdate(calenderEntryStateList=calenderEntriesToBeChecked))
        super.onPause()
    }

    companion object{
        var nrRecipes = "0"
    }

}