package com.droidhubworld.nettoollib.macinfo

import android.content.Context
import com.droidhubworld.nettoollib.FindDevice
import com.droidhubworld.nettoollib.NetworkInfo
import com.droidhubworld.nettoollib.models.DeviceItem
import com.droidhubworld.nettoollib.utils.UNKNOWN
import com.droidhubworld.nettoollib.vendor.VendorInfo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */


object MacAddressInfo {
    /**
     * Runs command "ip n"
     *
     * Sample result:
     * <pre>
     * 192.168.147.1 dev wlan0 lladdr 3c:a6:f6:33:54:b3 REACHABLE
     * 192.168.147.114 dev wlan0  FAILED
     * 192.168.147.45 dev wlan0  FAILED
     * 192.168.147.123 dev wlan0  FAILED
     * 192.168.147.180 dev wlan0 lladdr c4:70:0b:17:79:b8 REACHABLE
     * ...
    </pre> *
     */
    fun setMacAddress(
        context: Context,
        deviceItems: List<DeviceItem>
    ) {
        val deviceItemHashMap =
            HashMap<String, DeviceItem>()
        for (deviceItem in deviceItems) {
//            Log.e("added", deviceItem.getDeviceName());
            deviceItemHashMap[deviceItem.ipAddress] = deviceItem
        }
        val currentDeviceIpAddress = NetworkInfo.getDeviceIpAddress(context)
        val currentDeviceItem = deviceItemHashMap[currentDeviceIpAddress]
        if (currentDeviceItem != null) {
            val currentDeviceMacAddress = getCurrentDeviceMacAddress(
                currentDeviceIpAddress
            )
            currentDeviceItem.macAddress = currentDeviceMacAddress
            currentDeviceItem.vendorName = VendorInfo.getVendorName(
                context,
                currentDeviceMacAddress
            )
        }
        val runtime = Runtime.getRuntime()
        try {
            val process = runtime.exec("ip n")
            process.waitFor()
            val exitCode = process.exitValue()
            if (exitCode != 0) {
                return
            }
            val inputStreamReader = InputStreamReader(process.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var macAddress: String
            var ipAddress: String
            var values: List<String>

            bufferedReader.forEachLine {
                values = it.split(" ")
                if (values.size == 6) {

                    /* If line starts with ip address, return macAddress
                     *  192.168.147.180 dev wlan0 lladdr f4:02:23:72:4a:df REACHABLE
                     */
                    ipAddress = values[0]
                    macAddress = values[4]
                    val deviceItem = deviceItemHashMap[ipAddress]
                    if (deviceItem != null) {
                        deviceItem.macAddress = macAddress
                        deviceItem.vendorName = VendorInfo.getVendorName(
                            context,
                            deviceItem.macAddress
                        )
                    }
                }
            }

            bufferedReader.close()

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun getCurrentDeviceMacAddress(ipAddress: String?): String {
        try {
            val localIP = InetAddress.getByName(ipAddress)
            val networkInterface = NetworkInterface.getByInetAddress(localIP)
                ?: return UNKNOWN
            val hardwareAddress = networkInterface.hardwareAddress ?: return UNKNOWN
            val stringBuilder = StringBuilder(18)
            for (b in hardwareAddress) {
                if (stringBuilder.length > 0) {
                    stringBuilder.append(":")
                }
                stringBuilder.append(String.format("%02x", b))
            }
            return stringBuilder.toString()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return UNKNOWN
    }
}

