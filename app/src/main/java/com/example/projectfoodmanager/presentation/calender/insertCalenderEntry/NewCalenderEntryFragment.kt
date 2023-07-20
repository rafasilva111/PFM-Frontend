package com.example.projectfoodmanager.presentation.calender.insertCalenderEntry

import android.annotation.SuppressLint
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.databinding.FragmentNewCalenderEntryBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class NewCalenderEntryFragment : Fragment() {

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel: AuthViewModel by viewModels()
    private val calenderViewModel: CalendarViewModel by viewModels()

    lateinit var binding: FragmentNewCalenderEntryBinding
    private var snapHelper: SnapHelper = PagerSnapHelper()
    private lateinit var manager: LinearLayoutManager



    val TAG: String = "NewCalenderEntryFragment"


    private val adapter by lazy {
        FavoritesRecipeCalenderListingAdapter(
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

    @SuppressLint("ClickableViewAccessibility", "WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bindObservers()

        binding.favoritesRV.adapter = adapter

        binding.CloseRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        //valida shared preferences
        // insere as saved recipes
        try {
            val user = sharedPreference.getUserSession()
            val recipes = user.getSavedRecipes()
            if (recipes.isEmpty())
                binding.tvNoRecipes.visibility = View.VISIBLE
            else
                adapter.updateList(user.getSavedRecipes(), user)

        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: User had no shared prefences...")
            // se não tiver shared preferences o user não tem sessão válida
            //tera um comportamento diferente offilne
            authViewModel.logoutUser()
        }


        binding.tagCV.setOnClickListener {
            //TODO: DIALOG CATEGORIAS
            MaterialAlertDialogBuilder(requireContext(),  R.style.ThemeOverlay_App_MaterialAlertDialog)

            .show()
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
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione a data")
                    .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.dialog?.setCanceledOnTouchOutside(false)

            datePicker.show(parentFragmentManager, "DatePicker");

            datePicker.addOnCancelListener {
                datePicker.dismiss()
            }

            datePicker.addOnPositiveButtonClickListener {
                binding.dateValTV.text= datePicker.headerText

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

            val isSystem24Hour = is24HourFormat(context)
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(0)
                    .setMinute(0)
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
            val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))
            val calenderEntryRequest = CalenderEntryRequest(tag = CALENDAR_MEALS_TAG.ALMOCO,formatLocalTimeToServerTime(localDateTime))
            val teste = manager.findFirstCompletelyVisibleItemPosition()
            if (teste == -1)
                toast("No recipe selected...")
            else
                calenderViewModel.createEntryOnCalender(teste,calenderEntryRequest)
        }
    }


    private fun bindObservers() {

        calenderViewModel.createEntryOnCalenderLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

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