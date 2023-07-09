package com.example.projectfoodmanager.presentation.calender

import java.time.LocalDate
import java.time.LocalTime

class EventTrash(name: String, time: LocalTime, date: LocalDate) {
    var name: String
    var time: LocalTime
    var date: LocalDate



    init {
        this.name = name
        this.time = time
        this.date = date
    }



    companion object {
        var eventsList = arrayListOf<EventTrash>()

        fun eventsForDate(date: LocalDate): ArrayList<EventTrash> {
            val events: ArrayList<EventTrash> = arrayListOf()
            for (event in eventsList){
                if (event.date == date)
                    events.add(event)
            }
            return events
        }
    }
}