package com.droidhubworld.nettoollib

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.droidhubworld.nettoollib.interfaces.OnFindDeviceListener
import com.droidhubworld.nettoollib.macinfo.MacAddressInfo
import com.droidhubworld.nettoollib.models.DeviceItem
import com.droidhubworld.nettoollib.utils.ERROR_GATEWAY_ADDRESS
import com.droidhubworld.nettoollib.utils.ERROR_USER_STOPPED
import com.droidhubworld.nettoollib.utils.OTHERS
import com.droidhubworld.nettoollib.utils.WIFI_NOT_CONNECTED
import com.droidhubworld.nettoollib.vendor.VendorInfo.getVendorName
import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */
class FindDevice(val context: Context, val onDeviceFoundListener: OnFindDeviceListener) {

    private var executorService: ExecutorService? = null
    private var isRunning = false
    private var timeout = 500
    private var stopRequested = false

    private val reachableDevices: MutableList<DeviceItem> =
        ArrayList()

    private var handler: Handler? = null

    init {
        handler = Handler(Looper.getMainLooper())
    }

    fun setTimeout(timeout: Int): FindDevice {
        this.timeout = timeout
        return this
    }

    fun start() {
        isRunning = true
        stopRequested = false
        reachableDevices.clear()
        Thread { startPing() }.start()
    }

    fun isRunning(): Boolean {
        return isRunning
    }
    fun isStopRequested(): Boolean {
        return stopRequested
    }

    fun stop() {
        stopRequested = true
        executorService?.shutdownNow()
        if (isRunning) {
            sendFailedEvent(ERROR_USER_STOPPED)
        }
        isRunning = false
    }

    private fun sendStartEvent() {
        handler!!.post { onDeviceFoundListener.onStart(this) }
    }

    private fun sendFailedEvent(errorCode: Int) {
        handler!!.post { onDeviceFoundListener.onFailed(this, errorCode) }
    }

    private fun sendFinishedEvent(deviceItems: List<DeviceItem>) {
        handler!!.post { onDeviceFoundListener.onFinished(this, deviceItems) }
    }

    private fun isBelowAndroidR(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R
    }

    private fun startPing() {
        if (!NetworkInfo.isWifiConnected(context, false, true)) {
            isRunning = false
            sendFailedEvent(WIFI_NOT_CONNECTED)
            return
        }
        executorService = Executors.newFixedThreadPool(255)
        sendStartEvent()
        val gatewayAddress = NetworkInfo.getGatewayAddress(context)

        if (gatewayAddress == null) {
            sendFailedEvent(ERROR_GATEWAY_ADDRESS)
            return
        }

        val lastDotIndex = gatewayAddress.lastIndexOf(".")
        val ipPrefix = gatewayAddress.substring(0, lastDotIndex + 1)
        var ipAddressToPing: String
        try {
            for (i in 0..254) {
                ipAddressToPing = ipPrefix + (i + 1)
                executorService?.execute(Ping(ipAddressToPing))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (stopRequested) {
                sendFailedEvent(ERROR_USER_STOPPED)
            } else {
                sendFailedEvent(OTHERS)
            }
            return
        }

        executorService?.shutdown()
        try {
            val wait: Boolean? = executorService?.awaitTermination(10, TimeUnit.MINUTES)
            if (wait == true) {
                if (isBelowAndroidR()) {
                    MacAddressInfo.setMacAddress(context, reachableDevices)
                }
                if (!stopRequested)
                    sendFinishedEvent(reachableDevices)
            } else {
                sendFailedEvent(OTHERS)
            }
        } catch (e: InterruptedException) {
            if (stopRequested) {
                sendFailedEvent(ERROR_USER_STOPPED)
            } else {
                sendFailedEvent(OTHERS)
            }
            e.printStackTrace()
        }
        isRunning = false
    }


    inner class Ping(private val ipAddress: String) : Runnable {
        override fun run() {
            try {
                val inetAddress = InetAddress.getByName(ipAddress)
//                val macAddress: String = MacAddressInfo.getCurrentDeviceMacAddress(ipAddress)
//                val vendorName = getVendorName(macAddress)
                if (Thread.currentThread().isInterrupted) {
                    return
                }
                if (inetAddress.isReachable(timeout)) {
                    val deviceItem = DeviceItem()
                    // Vendor name and mac address still not set
                    deviceItem.ipAddress = ipAddress
                    deviceItem.deviceName = inetAddress.hostName
//                    deviceItem.macAddress = macAddress
                    reachableDevices.add(deviceItem)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}