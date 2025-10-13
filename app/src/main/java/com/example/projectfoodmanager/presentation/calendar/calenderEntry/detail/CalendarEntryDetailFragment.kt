package com.example.projectfoodmanager.presentation.calendar.calenderEntry.detail

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.databinding.FragmentCalendarEntryDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateTimeToServerTime
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToLocalDateTime
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToTimeString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class CalendarEntryDetailFragment : Fragment(), ImageLoadingListener {


    /** Binding */
    lateinit var binding: FragmentCalendarEntryDetailBinding

    /** ViewModels */
    private val calendarViewModel by activityViewModels<CalendarViewModel>()

    /** Constants */
    lateinit var objCalEntry: CalenderEntry

    private var tagSelected: Int = -1
    private var imagesLoaded: Int = 0

    private lateinit var tagsList: List<String>
    private lateinit var tagsValuesList: List<String>

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Interfaces */

    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            imagesLoaded++
            if (imagesLoaded >= DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD) {
                binding.progressBar.hide()
                binding.recipeCL.visibility = View.VISIBLE
            }


        }
    }


    /**
     *  Android LifeCycle
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentCalendarEntryDetailBinding.inflate(layoutInflater)

            binding.root
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        arguments?.let {

            objCalEntry = if (Build.VERSION.SDK_INT >= 33) {
                // TIRAMISU
                arguments?.getParcelable("calendar_entry", CalenderEntry::class.java)!!
            } else {
                arguments?.getParcelable("calendar_entry")!!
            }

        }

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()

    }


    /**
     *  General
     * */

    private fun setUI() {


        /**
         *  General
         * */

        /** Author */
        binding.authorTV.text = objCalEntry.recipe.createdBy.name

        /** Author image */
        loadUserImage(binding.authorIV, objCalEntry.recipe.createdBy.imgSource) {
            onImageLoaded()
        }

        /** Recipe image */
        if (objCalEntry.recipe.imgSource.isNotEmpty())
            loadRecipeImage(binding.imageView, objCalEntry.recipe.imgSource) {
                onImageLoaded()
            }
        else
            onImageLoaded()

        /** Verified Badge */
        if (objCalEntry.recipe.createdBy.verified) {
            binding.verifyUserIV.visibility = View.VISIBLE
        } else {
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        /** Date */
        binding.dateTV.text = formatServerTimeToDateString(objCalEntry.recipe.createdDate)

        /** ID */
        binding.idTV.text = objCalEntry.recipe.id.toString()

        /** Verified recipe */
        if (objCalEntry.recipe.verified) {
            binding.verifyRecipeIV.visibility = View.INVISIBLE
            binding.verifyRecipeTV.visibility = View.INVISIBLE
        } else {
            binding.verifyRecipeIV.visibility = View.VISIBLE
            binding.verifyRecipeTV.visibility = View.VISIBLE
        }

        /** Rating */
        binding.ratingRecipeRB.rating = objCalEntry.recipe.rating.toFloat()
        binding.ratingMedTV.text = objCalEntry.recipe.rating.toString()

        /** Title */
        binding.recipeTitleTV.text = objCalEntry.recipe.title

        /** Description */
        binding.recipeDescriptionTV.text = objCalEntry.recipe.description
        binding.recipeCL.setOnClickListener {
            findNavController().navigate(
                R.id.action_calendarEntryDetailFragment_to_receitaDetailFragment,
                Bundle().apply {
                    putInt("recipe_id", objCalEntry.recipe.id)
                    putFloat(
                        "user_portion",
                        sharedPreference.getUserSession().userPortion.toFloat()
                    )
                })
        }

        /** Favorites */
        binding.favoritesIB.isEnabled = false
        if (objCalEntry.recipe.saved) {
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        } else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

        /** Likes */
        binding.nLikeTV.text = objCalEntry.recipe.likes.toString()

        binding.likeIB.isEnabled = false
        if (objCalEntry.recipe.liked) {
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        } else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)

        /** Tag */
        tagsList = resources.getStringArray(R.array.tagEntryCalender_items).toList()
        tagsValuesList = resources.getStringArray(R.array.tagEntryCalender_values).toList()
        tagSelected = tagsValuesList.indexOfFirst { it.equals(objCalEntry.tag, ignoreCase = true) }
        binding.tagValTV.text = tagsList[tagSelected]

        /** Tag selection */
        binding.tagCV.setOnClickListener {
            showTagDialog()
        }

        /** Realization date */
        binding.dateValTV.text = formatServerTimeToDateString(objCalEntry.realizationDate)

        /** Date selection */
        binding.dateCV.setOnClickListener {
            showDatePickerDialog()
        }

        /** Realization time */
        binding.timeValTV.text = formatServerTimeToTimeString(objCalEntry.realizationDate)

        /** Time selection */
        binding.timeCV.setOnClickListener {
            showTimePickerDialog()
        }

        /** Delete entry */
        binding.deleteRegIB.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_trash)
                .setTitle("Delete entry?")
                .setMessage("Are you sure you want to delete the entry?")
                .setPositiveButton("Yes") { dialog, which ->
                    calendarViewModel.deleteCalendarEntry(objCalEntry)
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        /** Update entry */
        binding.updateRegIB.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_recipe)
                .setTitle("Update entry?")
                .setMessage("Are you sure you want to update the entry?")
                .setPositiveButton("Yes") { dialog, which ->
                    val (valid, message) = validation()
                    if (valid) {
                        calendarViewModel.patchCalendarEntry(
                            calenderEntryId = objCalEntry.id,
                            calenderPatchRequest = getCalenderEntryRequest()
                        )
                    } else {
                        Toast(context).showCustomToast(message, requireActivity(), ToastType.ALERT)
                    }
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        /**
         *  Navigation
         * */

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun bindObservers() {
        calendarViewModel.deleteCalendarEntryLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Registo apagado com sucesso")
                        findNavController().navigateUp()
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

        calendarViewModel.patchCalendarEntryLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Detalhes da refeição atualizados com sucesso")
                        findNavController().navigateUp()
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

    /**
     *  Functions
     * */

    private fun validation(): Pair<Boolean, String> {


        val oldDate = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalDate()
        val newDate =
            LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val oldTime = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalTime()
        val newTime = LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm"))

        if (tagsValuesList[tagSelected] == objCalEntry.tag && newDate == oldDate && newTime == oldTime)
            return false to "Nothing changed"

        val localDateTime = LocalDateTime.of(
            LocalDate.parse(
                binding.dateValTV.text,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            ), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm"))
        )

        if (localDateTime < LocalDateTime.now()) {
            return false to "Date should be before today."
        }

        return true to ""
    }

    private fun getCalenderEntryRequest(): CalenderEntryRequest {


        return CalenderEntryRequest(
            tag = resources.getStringArray(R.array.tagEntryCalender_values).toList()[tagSelected],
            realizationDate = formatLocalDateTimeToServerTime(
                LocalDateTime.of(
                    LocalDate.parse(
                        binding.dateValTV.text,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm"))
                )
            )
        )
    }

    private fun showTagDialog() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_checked_text_view, tagsList)
        val builder = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Selecione a categoria")
            .setPositiveButton("ok") { _, _ ->
                binding.tagValTV.text = tagsList[tagSelected]
            }
            .setSingleChoiceItems(adapter, tagSelected) { _, which ->
                // Handle the item selection here
                tagSelected = which
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                binding.tagValTV.text = tagsList[tagSelected]
            }

        builder.create()


        builder.show()
    }

    private fun showDatePickerDialog() {

        val currentDate = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalDate()
        val selectedMillis = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setSelection(selectedMillis)
            .build()


        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.addOnPositiveButtonClickListener {
            if (datePicker.headerText.length == 9) {
                binding.dateValTV.text = getString(R.string.date_text, "0" + datePicker.headerText)
            } else {
                binding.dateValTV.text = datePicker.headerText
            }


        }

        datePicker.dialog?.setCanceledOnTouchOutside(false)

        datePicker.show(parentFragmentManager, "DatePicker")

    }

    private fun showTimePickerDialog() {
        val clockFormat =
            if (DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val time = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalTime()

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(time.hour)
                .setMinute(time.minute)
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



        MaterialTimePicker.Builder().setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)

        picker.show(parentFragmentManager, "TimePicker");
        picker.dialog?.setCanceledOnTouchOutside(false)
    }


}