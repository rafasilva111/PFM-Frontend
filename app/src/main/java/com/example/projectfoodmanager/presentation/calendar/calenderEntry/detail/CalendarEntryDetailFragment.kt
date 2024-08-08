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
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
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

    private var tagSelected: Int= -1
    private var imagesLoaded: Int = 0

    private lateinit var tagsList:List<String>
    private lateinit var tagsValuesList:List<String>

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Interfaces */

    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            imagesLoaded++
            if (imagesLoaded >= 2) {
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

        //-> Load Author name
        binding.authorTV.text = objCalEntry.recipe.createdBy.name

        //-> Load Author img
        if (objCalEntry.recipe.createdBy.imgSource.isNotEmpty())
            loadUserImage(binding.authorIV, objCalEntry.recipe.createdBy.imgSource) {
                onImageLoaded()
            }
        else
            onImageLoaded()

        //-> Load Recipe img
        if (objCalEntry.recipe.imgSource.isNotEmpty())
            loadRecipeImage(binding.imageView,objCalEntry.recipe.imgSource) {
                onImageLoaded()
            }
        else
            onImageLoaded()


        //--> AUTHOR NAME
        binding.authorTV.text= objCalEntry.recipe.createdBy.name

        //--> AUTHOR VERIFIED
        if(objCalEntry.recipe.createdBy.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        //--> DATE
        binding.dateTV.text =formatServerTimeToDateString(objCalEntry.recipe.createdDate)

        //--> ID
        binding.idTV.text = objCalEntry.recipe.id.toString()

        //--> VERIFIED

        if (objCalEntry.recipe.verified){
            binding.verifyRecipeIV.visibility= View.INVISIBLE
            binding.verifyRecipeTV.visibility= View.INVISIBLE
        }else{
            binding.verifyRecipeIV.visibility= View.VISIBLE
            binding.verifyRecipeTV.visibility= View.VISIBLE
        }

        //--> RATING
        binding.ratingRecipeRB.rating = objCalEntry.recipe.sourceRating.toFloat()
        binding.ratingMedTV.text = objCalEntry.recipe.sourceRating

        //--> TITLE
        binding.recipeTitleTV.text = objCalEntry.recipe.title

        //--> DESCRIPTION
        binding.recipeDescriptionTV.text = objCalEntry.recipe.description
        binding.recipeCL.setOnClickListener {
            findNavController().navigate(R.id.action_calendarEntryDetailFragment_to_receitaDetailFragment,Bundle().apply {
                putInt("recipe_id",objCalEntry.recipe.id)
                putFloat("user_portion",sharedPreference.getUserSession().userPortion.toFloat())
            })
        }


        //--> SAVE
        binding.favoritesIB.isEnabled = false
        if(objCalEntry.recipe.saved){
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        }
        else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

        //--> LIKE
        binding.nLikeTV.text = objCalEntry.recipe.likes.toString()

        binding.likeIB.isEnabled = false
        if(objCalEntry.recipe.liked){
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        }
        else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)

        //--> SET TAG VALUE
        tagsList = resources.getStringArray(R.array.tagEntryCalender_items).toList()
        tagsValuesList = resources.getStringArray(R.array.tagEntryCalender_values).toList()
        tagSelected = tagsValuesList.indexOfFirst { it.equals(objCalEntry.tag, ignoreCase = true) }
        binding.tagValTV.text = tagsList[tagSelected]

        //--> SET ON CLICK TAG
        binding.tagCV.setOnClickListener {
            showTagDialog()
        }



        //--> SET DATE VALUE
        binding.dateValTV.text = formatServerTimeToDateString(objCalEntry.realizationDate)

        //--> SET ON DATE CLICK
        binding.dateCV.setOnClickListener {
            showDatePickerDialog()
        }

        //--> SET TIME VALUE
        binding.timeValTV.text = formatServerTimeToTimeString(objCalEntry.realizationDate)

        //--> SET ON TIME CLICK
        binding.timeCV.setOnClickListener {
            showTimePickerDialog()
        }


        binding.deleteRegIB.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_trash)
                .setTitle("Apagar o registo?")
                .setMessage("Tem certeza de que deseja apagar o registo?")
                .setPositiveButton("Sim") { dialog, which ->
                    // Adicione aqui o código para apagar o registro
                    calendarViewModel.deleteCalendarEntry(objCalEntry)

                }
                .setNegativeButton("Não") { dialog, which ->
                    // Adicione aqui o código para cancelar a exclusão do registro
                    dialog.dismiss()
                }
                .show()
        }

        binding.updateRegIB.setOnClickListener {
            //TODO("Update register")
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_recipe)
                .setTitle("Atualizar o registo?")
                .setMessage("Tem certeza de que deseja atualizar o registo?")
                .setPositiveButton("Sim") { dialog, which ->
                    // Adicione aqui o código para apagar o registro
                    val (valid,message) = validation()
                    if (valid) {
                        calendarViewModel.patchCalendarEntry(calenderEntryId = objCalEntry.id, calenderPatchRequest = getCalenderEntryRequest())
                    }
                    else{
                        Toast(context).showCustomToast (message, requireActivity(),ToastType.ALERT)
                    }
                }
                .setNegativeButton("Não") { dialog, which ->
                    // Adicione aqui o código para cancelar a exclusão do registro
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

    private fun validation():Pair<Boolean,String> {


        val oldDate = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalDate()
        val newDate = LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val oldTime = formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalTime()
        val newTime = LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm"))

        if(tagsValuesList[tagSelected]==objCalEntry.tag && newDate == oldDate && newTime == oldTime)
            return false to "Nothing changed"

        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))

        if (localDateTime < LocalDateTime.now()) {
            return false to "Date should be before today."
        }

        return true to ""
    }

    private fun getCalenderEntryRequest(): CalenderEntryRequest {


        return CalenderEntryRequest(
            tag = resources.getStringArray(R.array.tagEntryCalender_values).toList()[tagSelected],
            realizationDate = formatLocalTimeToServerTime(
                LocalDateTime.of(
                    LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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
        val selectedMillis =  currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a data")
            .setSelection(selectedMillis)
            .build()


        datePicker.addOnCancelListener {
            datePicker.dismiss()
        }

        datePicker.addOnPositiveButtonClickListener {
            if(datePicker.headerText.length == 9){
                binding.dateValTV.text= getString(R.string.date_text, "0" + datePicker.headerText)
            }else{
                binding.dateValTV.text= datePicker.headerText
            }


        }

        datePicker.dialog?.setCanceledOnTouchOutside(false)

        datePicker.show(parentFragmentManager, "DatePicker")

    }

    private fun showTimePickerDialog() {
        val clockFormat =
            if (DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val time= formatServerTimeToLocalDateTime(objCalEntry.realizationDate).toLocalTime()

        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(time.hour)
                .setMinute(time.minute)
                .setTitleText("Digite a que horário pretende")
                .build()
        picker.addOnPositiveButtonClickListener {
            binding.timeValTV.text= getString(R.string.calender_formated_date,String.format("%02d", picker.hour),String.format("%02d", picker.minute))
        }

        picker.addOnCancelListener {
            picker.dismiss()
        }



        MaterialTimePicker.Builder().setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)

        picker.show(parentFragmentManager, "TimePicker");
        picker.dialog?.setCanceledOnTouchOutside(false)
    }



}