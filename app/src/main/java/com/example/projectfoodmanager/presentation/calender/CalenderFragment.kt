package com.example.projectfoodmanager.presentation.calender

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.databinding.FragmentCalenderBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.currentDate
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeVisibilityMenu
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToFormatDate
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.CalenderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    lateinit var binding: FragmentCalenderBinding

    val authViewModel: AuthViewModel by viewModels()
    private val calenderViewModel by activityViewModels<CalenderViewModel>()

    val TAG: String = "ProfileFragment"

    private val adapterCalMonth by lazy {
        CalenderAdapter(
            daysInMonthArray(
                currentDate
            ),
            onItemClicked = { selectedDate ->
               //TODO: Confirmar com o rafa
                binding.registersDateTV.text= formatLocalDateToFormatDate(selectedDate)
                calenderViewModel.getEntryOnCalender(selectedDate.atStartOfDay())
            }
        )
    }

    private val adapterCalWeekly by lazy {
        CalenderAdapter(
            CalenderUtils.daysInWeekArray(
                currentDate
            ),
            onItemClicked = { selectedDate ->
                //TODO: Confirmar com o rafa
                binding.registersDateTV.text= formatLocalDateToFormatDate(selectedDate)
                calenderViewModel.getEntryOnCalender(selectedDate.atStartOfDay())
            }
        )
    }

    private val adapterEntry by lazy{
        CalenderEntryAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_calenderFragment_to_calendarEntryDetailFragment,Bundle().apply {
                    putParcelable("CalenderEntry",item)
                })
                changeVisibilityMenu(false,activity)
            },
            onDoneClicked = { checkDone, item->
                calenderViewModel.patchCalenderEntry(item.id, CalenderEntryPatchRequest(checked_done = checkDone))
            }
        )
    }

  /*  private val recipeCalenderAdapter by lazy {
        RecipeCalenderAdapter(
            CalenderUtils.daysInWeekArray(
                currentDate
            ),
            onItemClicked = { text ->

            }
        )
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentCalenderBinding.inflate(layoutInflater)
        setMonthView()
        binding.registersDateTV.text= formatLocalDateToFormatDate(currentDate)

        val manager = LinearLayoutManager(activity)

        binding.calEntrysRV.layoutManager = manager

        bindObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.addRegisterIB.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_newCalenderEntryFragment)
            Helper.changeVisibilityMenu(false,activity)

        }

        binding.monthViewBtn.setOnClickListener {
            setMonthView()
            binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
            binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

            binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
            binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))

            //TODO: ao trocar de vistas o dia que se mantemn selecionado deve continuar o mesmo
        }


        binding.weeklyViewBtn.setOnClickListener {
            setWeeklyView()

            binding.weeklyViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color))
            binding.weeklyViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.white)))

            binding.monthViewBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.grayLightBTN))
            binding.monthViewBtn.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.main_color)))

            //TODO: ao trocar de vistas o dia que se mantemn selecionado deve continuar o mesmo

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
            findNavController().navigate(R.id.action_calenderFragment_to_calenderIngredientsFragment,Bundle().apply {
                putInt("month",currentDate.monthValue)
            })
            Helper.changeVisibilityMenu(false,activity)

        }


        if (Helper.isOnline(view.context)) {
            binding.calEntrysRV.adapter = adapterEntry
            binding.nRegistersTV.text= adapterEntry.itemCount.toString()

            calenderViewModel.getEntryOnCalender(currentDate.atStartOfDay())
        }

    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window


        //BACKGROUND in NAVIGATION BAR
        window.statusBarColor = requireContext().getColor(R.color.background_1)
        window.navigationBarColor = requireContext().getColor(R.color.main_color)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( 0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }
        Helper.changeVisibilityMenu(true,activity)

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

/*    private fun updateMonthView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)
        adapter.updateList(daysInMonthArray(
            currentDate
        ))
    }*/

    private fun setWeeklyView() {

        binding.calMonthRV.visibility = View.GONE
        binding.calWeeklyRV.visibility = View.VISIBLE

        currentDate = LocalDate.now()
        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.replaceFirstChar { it.uppercase() }
        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calWeeklyRV.layoutManager = layoutManager
        binding.calWeeklyRV.adapter = adapterCalWeekly

        //setEventAdapter()
    }

    private fun updateCalenderView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)!!.capitalize()

        if (binding.calMonthRV.visibility == View.VISIBLE) {
            adapterCalMonth.updateList(
                daysInMonthArray(
                    currentDate
                )
            )
        } else {
            adapterCalWeekly.updateList(
                CalenderUtils.daysInWeekArray(
                    currentDate
                )
            )
        }

    }

    private fun bindObservers() {
        calenderViewModel.getEntryOnCalenderLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        adapterEntry.updateList(it.data!!.result)

                        if (it.data.result.size != 0){
                            binding.nRegistersTV.text= it.data.result.size.toString()
                            binding.emptyRegTV.visibility=View.INVISIBLE
                        }else{
                            binding.nRegistersTV.text= "0"
                            binding.emptyRegTV.visibility=View.VISIBLE
                        }

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


        calenderViewModel.patchCalenderEntryLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
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
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        //Todo: RAFA fazer save das calendersEntrys mediante a sharedpreferences e não diretamente
    }


}