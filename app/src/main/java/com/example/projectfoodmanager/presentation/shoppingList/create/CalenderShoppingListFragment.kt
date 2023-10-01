package com.example.projectfoodmanager.presentation.shoppingList.create

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.databinding.FragmentCalenderIngredientsBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.example.projectfoodmanager.viewmodels.ShoppingListViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class CalenderShoppingListFragment : Fragment() {

    // binding
    lateinit var binding: FragmentCalenderIngredientsBinding

    // viewModels
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val calendarViewModel by activityViewModels<CalendarViewModel>()
    private val shoppingListViewModel by activityViewModels<ShoppingListViewModel>()

    // constants
    private val TAG: String = "CalenderShoppingListFragment" // Updated TAG

        // select date
    private lateinit var toDate: LocalDateTime
    private lateinit var fromDate: LocalDateTime

        // user's portion
    var user_portion: Int = -1 // portion can be -1 -> never selected, 1 -> use recipes portion, 2+ -> use our portions

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val calenderShoppingListAdapter by lazy {
        CalenderShoppingListAdapter(
            context,
            onItemClicked = { pos, item ->
                // Handle item click here
            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Parse dates from arguments
        toDate = LocalDateTime.parse(arguments?.getString("to_date")!!)
        fromDate = LocalDateTime.parse(arguments?.getString("from_date")!!)

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentCalenderIngredientsBinding.inflate(layoutInflater)

            binding.calenderIngridientsLV.layoutManager = LinearLayoutManager(activity)
            binding.calenderIngridientsLV.adapter = calenderShoppingListAdapter



            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind observers tem que ser aqui (não penses confia, já pensas te nisto)

        bindObservers()

        // Set click listener for back button
        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        calendarViewModel.getCalendarIngredients(fromDate, toDate)

        // Initialize shoppingList name

        binding.shoppingListName.text = Editable.Factory.getInstance().newEditable(getString(
            R.string.shopping_list_date,
            fromDate.dayOfMonth,
            fromDate.monthValue,
            toDate.dayOfMonth,
            toDate.monthValue
        ))


        // Initialize date buttons
        initDateButtons()

        // Set click listener for shopping cart and other buttons
        binding.saveShoppingCart.setOnClickListener {
            shoppingListViewModel.postShoppingList(createShoppingListRequest())
        }

        binding.checkSavedRecipes.setOnClickListener {
            // Handle the click for checking saved recipes
            findNavController().navigate(R.id.action_calenderIngredientsFragment_to_shoppingListListingFragment)
        }

        binding.addIngredient.setOnClickListener {
            // Handle the click for adding an ingredient
            toast(getString(R.string.NOT_IMPLEMENT_YET))
        }
    }

    private fun initDateButtons() {
        // Initialize the "From" date button
        binding.fromDateValTV.text = fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.fromDateCV.setOnClickListener {
            showDatePicker(binding.fromDateValTV) { selectedDate ->
                fromDate = selectedDate
                calendarViewModel.getCalendarIngredients(fromDate, toDate)
            }
        }

        // Initialize the "To" date button
        binding.toDateValTV.text = toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.toDateValTV.setOnClickListener {
            showDatePicker(binding.toDateValTV) { selectedDate ->
                toDate = selectedDate
                calendarViewModel.getCalendarIngredients(fromDate, toDate)
            }
        }
    }

    private fun showDatePicker(textView: TextView, onDateSelected: (LocalDateTime) -> Unit) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.dialog?.setCanceledOnTouchOutside(false)

        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = LocalDateTime.of(
                LocalDate.parse(textView.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.MIDNIGHT
            )
            onDateSelected(selectedDate)
        }
    }

    private fun createShoppingListRequest(): ShoppingListRequest {
        return ShoppingListRequest(
            name = binding.shoppingListName.text.toString(),
            shopping_ingredients = calenderShoppingListAdapter.list
        )
    }

    private fun bindObservers() {

        calendarViewModel.getCalendarIngredientsLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        binding.progressBar.isGone = true

                        result.data?.let {
                                it -> calenderShoppingListAdapter.updateList(it.result) }

                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {

                        binding.progressBar.isVisible = true
                    }
                }
            }
        }
        shoppingListViewModel.postShoppingListLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        toast("Successfully created shopping list.")
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        toast("User can't create more shopping list's.",ToastType.ALERT)
                    }
                    is NetworkResult.Loading -> {
                        // todo rui loading bar
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        authViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        toast("User's portion updated successfully.")
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

    override fun onResume() {
        super.onResume()

        // Update UI elements for status bar and navigation bar
        updateSystemBarsAppearance()
    }

    private fun updateSystemBarsAppearance() {
        val window = requireActivity().window

        // set bottom bar color
        window.navigationBarColor = requireContext().getColor(R.color.main_color)

        // Set background color for status and navigation bars
        window.statusBarColor = requireContext().getColor(R.color.background_1)

        // Set text color for status and navigation bars (for Android R and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}
