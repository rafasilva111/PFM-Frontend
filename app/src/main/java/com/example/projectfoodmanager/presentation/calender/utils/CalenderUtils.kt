package com.example.projectfoodmanager.presentation.calender.utils

import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalenderUtils {
    companion object {
        var currentDate: LocalDate = LocalDate.now()
        var selectedDate: LocalDate = LocalDate.now()
        var currentRecipeId: String? = null


        fun formatDateMonthYear(date: LocalDate): String? {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            return date.format(formatter)
        }

        fun formatDate(date: LocalDate): String? {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            return date.format(formatter)
        }

        fun formatTime(time: LocalTime): Any? {
            val formatter = DateTimeFormatter.ofPattern("hh:mm")
            return time.format(formatter)
        }

        fun daysInMonthArray(date: LocalDate): ArrayList<LocalDate?> {

            var daysInMonthArray = ArrayList<LocalDate?>()
            val yearMonth = YearMonth.from(date)
            val daysInMonth = yearMonth.lengthOfMonth()
            val firstOfMonth: LocalDate = date.withDayOfMonth(1)
            val dayOfWeek = firstOfMonth.dayOfWeek.value
            for (i in 1..42) {
                if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(null)
                } else {
                    daysInMonthArray.add(LocalDate.of(date.year, date.month,i - dayOfWeek))
                }
            }

            val allNull = daysInMonthArray.take(7).all { it == null }
            val lastAllNull = daysInMonthArray.takeLast(7).all { it == null }


            if (allNull)
                return ArrayList(daysInMonthArray.drop(7))

            if (lastAllNull)
                return ArrayList(daysInMonthArray.dropLast(7))

            return daysInMonthArray
        }


        fun daysInWeekArray(date: LocalDate): ArrayList<LocalDate?> {
            val days = arrayListOf<LocalDate?>()
            var current = sundayForDate(date)
            if (current != null){
                val endDate = current.plusWeeks(1)
                while (current!!.isBefore(endDate)){
                    days.add(current)
                    current = current.plusDays(1)
                }
            }
            return days

        }

        fun sundayForDate(current: LocalDate): LocalDate? {
            val oneWeekAgo = current.minusWeeks(1)
            var currentHelper = current
            while (currentHelper.isAfter(oneWeekAgo)){
                if (currentHelper.dayOfWeek == DayOfWeek.SUNDAY)
                    return currentHelper
                currentHelper = currentHelper.minusDays(1)
            }

            return null
        }




    }
}
