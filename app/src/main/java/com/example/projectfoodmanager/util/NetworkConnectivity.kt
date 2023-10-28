package com.example.projectfoodmanager.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

class NetworkConnectivity (private val context: Context):LiveData<Boolean>(){


    private val connectivityManager:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkConnectionCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }
    }

    override fun onActive() {
        super.onActive()
        updateNetworkConnection()
        context.registerReceiver(networkReciever, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }





    private fun updateNetworkConnection() {
        val networkConnetion: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(networkConnetion?.isConnected == true)
    }

    private val networkReciever = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateNetworkConnection()
        }
    }
}