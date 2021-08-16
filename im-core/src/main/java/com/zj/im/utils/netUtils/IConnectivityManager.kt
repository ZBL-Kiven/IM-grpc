package com.zj.im.utils.netUtils

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.*
import android.net.NetworkCapabilities.*
import android.os.Build
import com.zj.im.utils.log.logger.printInFile

internal class IConnectivityManager {

    private var stateChangeListener: ((NetWorkInfo) -> Unit)? = null
    private var connectivityManager: ConnectivityManager? = null
    private var netWorkBrodCast: NetWorkBrodCast? = null
    private var context: Application? = null
    private var lastRecord: NetWorkInfo? = null

    fun init(context: Application?, l: ((NetWorkInfo) -> Unit)?) {
        this.context = context
        this.stateChangeListener = l
        clearRegister()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isLowerNChange(context)
        } else {
            this.connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager?
            connectivityManager?.registerNetworkCallback(request, netCallBack)
        }
    }

    fun checkNetWorkValidate(): NetWorkInfo {
        val isActive = isNetWorkActive
        onNetworkChanged(isActive)
        return isActive
    }

    private val request = NetworkRequest.Builder().addCapability(NET_CAPABILITY_INTERNET).build()

    private val netCallBack = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            onNetworkChanged(NetWorkInfo.CONNECTED)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val level = networkCapabilities.signalStrength
                printInFile("ConnectivityManager", "the signal changed to $level")
            }
            super.onCapabilitiesChanged(network, networkCapabilities)
        }

        override fun onLost(network: Network) {
            onNetworkChanged(NetWorkInfo.DISCONNECTED)
            super.onLost(network)
        }

        override fun onUnavailable() {
            onNetworkChanged(NetWorkInfo.DISCONNECTED)
            super.onUnavailable()
        }
    }

    private fun onNetworkChanged(info: NetWorkInfo) {
        if (lastRecord == null) {
            lastRecord = info
        } else if (lastRecord != info) {
            stateChangeListener?.invoke(info)
            lastRecord = info
        }
    }

    @Suppress("DEPRECATION")
    private fun isLowerNChange(context: Context?) {
        netWorkBrodCast = NetWorkBrodCast {
            onNetworkChanged(isNetWorkActive)
        }
        context?.registerReceiver(netWorkBrodCast, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    val isNetWorkActive: NetWorkInfo
        get() {
            return try {
                if (isNetworkConnected()) NetWorkInfo.CONNECTED else NetWorkInfo.DISCONNECTED
            } catch (e: Exception) {
                e.printStackTrace()
                NetWorkInfo.DISCONNECTED
            }
        }

    private fun isNetworkConnected(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val networkCapabilities = connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork)
            if (networkCapabilities != null) {
                networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET) && networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
            } else false
        } else {
            @Suppress("DEPRECATION") (connectivityManager?.activeNetworkInfo as NetworkInfo).let {
                it.isConnected && it.isAvailable
            }
        }
    }

    private fun clearRegister() {
        try {
            connectivityManager?.unregisterNetworkCallback(netCallBack)
            context?.unregisterReceiver(netWorkBrodCast)
        } catch (e: Exception) {
        }
    }

    fun shutDown() {
        clearRegister()
        lastRecord = null
        stateChangeListener = null
        connectivityManager = null
    }
}