package com.example.projectfoodmanager.util

class ObservableList<T> : ArrayList<T>() {
    private val observers: MutableList<(List<T>) -> Unit> = mutableListOf()

    fun addObserver(observer: (List<T>) -> Unit) {
        observers.add(observer)
    }

    fun notifyObservers() {
        for (observer in observers) {
            observer(this)
        }
    }
}