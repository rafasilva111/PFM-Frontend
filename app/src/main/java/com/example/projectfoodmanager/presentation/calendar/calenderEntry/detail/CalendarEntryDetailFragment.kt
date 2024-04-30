package com.example.projectfoodmanager.presentation.calendar.calenderEntry.detail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentCalendarEntryDetailBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateToFormatDate
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToFormatTime
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
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
class CalendarEntryDetailFragment : Fragment() {


    lateinit var binding: FragmentCalendarEntryDetailBinding
    lateinit var objCalEntry: CalenderEntry
    @Inject
    lateinit var sharedPreference: SharedPreference
    private var tagSelected: Int= -1
    private var oldTagSelected: Int= -1
    lateinit var user: User
    private val calendarViewModel by activityViewModels<CalendarViewModel>()
    private lateinit var savedDate:String
    private lateinit var savedTime:String
    private lateinit var oldDate:LocalDate
    private lateinit var newDate:LocalDate
    private lateinit var oldTime:LocalTime
    private lateinit var newTime:LocalTime
    private lateinit var tagsList:List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindObservers()
        objCalEntry = if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            arguments?.getParcelable("CalenderEntry", CalenderEntry::class.java)!!
        } else {
            arguments?.getParcelable("CalenderEntry")!!
        }

        if (this::binding.isInitialized) {
            return binding.root
        } else {
            binding = FragmentCalendarEntryDetailBinding.inflate(layoutInflater)

            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = sharedPreference.getUserSession()

        setUI()
    }

    private fun setUI() {

        //SET RECEITA

        //-> Load Author name
        binding.authorTV.text = objCalEntry.recipe.created_by.name

        //-> Load Author img
        loadUserImage(binding.authorIV, objCalEntry.recipe.created_by.imgSource)

        //-> Load Recipe img
        loadRecipeImage(binding.imageView,objCalEntry.recipe.img_source)


        //--> AUTHOR NAME
        binding.authorTV.text= objCalEntry.recipe.created_by.name

        //--> AUTHOR VERIFIED
        if(objCalEntry.recipe.created_by.verified){
            binding.verifyUserIV.visibility = View.VISIBLE
        }else{
            binding.verifyUserIV.visibility = View.INVISIBLE
        }

        //--> DATE
        binding.dateTV.text =formatServerTimeToDateString(objCalEntry.recipe.created_date)

        //--> ID
        binding.idTV.text = objCalEntry.recipe.id.toString()

        //--> VERIFIED

 /*       if (objCalEntry.recipe.verified){
            binding.verifyRecipeIV.visibility= View.INVISIBLE
            binding.verifyRecipeTV.visibility= View.INVISIBLE
        }else{
            binding.verifyRecipeIV.visibility= View.VISIBLE
            binding.verifyRecipeTV.visibility= View.VISIBLE
        }*/

        //--> RATING
        binding.ratingRecipeRB.rating = objCalEntry.recipe.source_rating.toFloat()
        binding.ratingMedTV.text = objCalEntry.recipe.source_rating

        //--> TITLE
        binding.recipeTitleTV.text = objCalEntry.recipe.title

        //--> DESCRIPTION
        binding.recipeDescriptionTV.text = objCalEntry.recipe.description
        binding.recipeCL.setOnClickListener {
            findNavController().navigate(R.id.action_calendarEntryDetailFragment_to_receitaDetailFragment,Bundle().apply {
                putParcelable("Recipe",objCalEntry.recipe)
                putFloat("UserPortion",sharedPreference.getUserSession().userPortion.toFloat())
            })
        }

        // get user from shared prefrences
        val user = sharedPreference.getUserSession()

        //--> SAVE
        binding.favoritesIB.isEnabled = false
        if(user.checkIfSaved(objCalEntry.recipe) != -1){
            binding.favoritesIB.setImageResource(R.drawable.ic_favorito_active)
        }
        else
            binding.favoritesIB.setImageResource(R.drawable.ic_favorite_black)

        //--> LIKE
        binding.nLikeTV.text = objCalEntry.recipe.likes.toString()

        binding.likeIB.isEnabled = false
        if(user.checkIfLiked(objCalEntry.recipe) != -1){
            binding.likeIB.setImageResource(R.drawable.ic_like_active)
        }
        else
            binding.likeIB.setImageResource(R.drawable.ic_like_black)


        //SET DETAILS

        //--> SET TAG VALUE
        tagsList = resources.getStringArray(R.array.tagEntryCalender_array).toList()
        oldTagSelected = tagsList.indexOfFirst { it.equals(objCalEntry.tag, ignoreCase = true) }
        tagSelected = oldTagSelected
        binding.tagValTV.text = tagsList[tagSelected]

        //--> SET ON CLICK TAG
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
                    binding.tagCV.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        }

        //--> SET DATE VALUE
        savedDate = formatLocalDateToFormatDate(LocalDateTime.parse(objCalEntry.realization_date,DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))
        binding.dateValTV.text = savedDate

        //--> SET ON DATE CLICK
        binding.dateCV.setOnClickListener {
            showDatePickerDialog()


        }

        binding.dateCV.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.dateCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.dateCV.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        }

        //--> SET TIME VALUE
        savedTime=  formatLocalTimeToFormatTime(LocalDateTime.parse(objCalEntry.realization_date,DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))
        binding.timeValTV.text = savedTime
        //--> SET ON TIME CLICK
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
                    binding.timeCV.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
            false
        }

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
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


    }

    private fun validation():Pair<Boolean,String> {

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        oldDate = LocalDate.parse(savedDate, dateFormatter)
        newDate = LocalDate.parse(binding.dateValTV.text, dateFormatter)

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        oldTime = LocalTime.parse(savedTime, timeFormatter)
        newTime = LocalTime.parse(binding.timeValTV.text, timeFormatter)

        if(tagSelected==oldTagSelected && newDate == oldDate && newTime == oldTime)
            return false to "Nothing changed"

        val localDateTime  = LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))

        if (localDateTime < LocalDateTime.now()) {
            return false to "Date should be before today."
        }

        return true to ""
    }

    private fun getCalenderEntryRequest(): CalenderEntryPatchRequest {

        var calenderEntryPatchRequest: CalenderEntryPatchRequest? = null

        //TODO: Esta condiçao não esta das melhores, é preciso fazer uma analise mais sobria
        //Se a tag selected é diferente da tag antiga e a data hora não mudaram
        if (tagSelected!=oldTagSelected && (newDate == oldDate && newTime.equals(oldTime))) {
            calenderEntryPatchRequest= CalenderEntryPatchRequest(tag=tagsList[tagSelected])
        }else if(tagSelected==oldTagSelected && (newDate != oldDate || newTime != oldTime)){
            calenderEntryPatchRequest= CalenderEntryPatchRequest(realization_date = formatLocalTimeToServerTime(LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))))
        }else{
            calenderEntryPatchRequest= CalenderEntryPatchRequest(tag = binding.tagValTV.text.toString().uppercase(), realization_date = formatLocalTimeToServerTime(LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))))
        }

      /*  val isTagChanged = tagSelected != oldTagSelected
        val isDateTimeChanged = !newDate.equals(oldDate) || !newTime.equals(newTime)
        val newTag = if (isTagChanged) tagsList[tagSelected] else binding.tagValTV.text.toString().uppercase()
        val newRealizationDate = if (isDateTimeChanged) formatLocalTimeToServerTime(LocalDateTime.of(LocalDate.parse(binding.dateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalTime.parse(binding.timeValTV.text, DateTimeFormatter.ofPattern("HH:mm")))) else null
        calenderEntryPatchRequest = CalenderEntryPatchRequest(tag = newTag, realization_date = newRealizationDate)
*/

        return calenderEntryPatchRequest
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

        builder.create()
        builder.show()
    }

    private fun showDatePickerDialog() {

        val currentDate = LocalDate.parse(savedDate,DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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

        val time= LocalTime.parse(savedTime)

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

}