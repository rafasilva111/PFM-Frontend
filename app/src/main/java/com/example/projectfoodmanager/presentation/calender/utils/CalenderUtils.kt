package com.example.projectfoodmanager.presentation.calender.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalenderUtils {
    companion object {
        var selectedDate: LocalDate = LocalDate.now()


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

            val daysInMonthArray = ArrayList<LocalDate?>()
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
                currentHelper = current.minusDays(1)
            }

            return null
        }




    }
}
