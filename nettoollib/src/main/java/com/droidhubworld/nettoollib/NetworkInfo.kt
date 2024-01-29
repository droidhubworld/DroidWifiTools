package com.droidhubworld.nettoollib

import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.droidhubworld.nettoollib.utils.parseIpAddress


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */

object NetworkInfo {
    fun isWifiConnected(context: Context, typeBoth: Boolean, wifiOnly: Boolean?): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val ni = cm.activeNetworkInfo
            if (ni != null) {
                if (typeBoth)
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                else if (wifiOnly == true) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI)
                }
                return ni.isConnected && (ni.type == ConnectivityManager.TYPE_MOBILE)

            }
        } else {
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                if (typeBoth)
                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                else if (wifiOnly == true) {
                    return nc!!.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }
        return false
    }

    private fun getDhcpInfo(context: Context): DhcpInfo {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.dhcpInfo
    }

    fun getGatewayAddress(context: Context): String? {
        return parseIpAddress(getDhcpInfo(context).gateway.toLong())
    }

    fun getDeviceIpAddress(context: Context): String? {
        return parseIpAddress(getDhcpInfo(context).ipAddress.toLong())
    }
}
