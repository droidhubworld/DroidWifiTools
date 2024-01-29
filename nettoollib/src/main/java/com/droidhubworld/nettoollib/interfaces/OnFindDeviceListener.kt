package com.droidhubworld.nettoollib.interfaces

import com.droidhubworld.nettoollib.FindDevice
import com.droidhubworld.nettoollib.models.DeviceItem


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */
interface OnFindDeviceListener {
    fun onStart(deviceFinder: FindDevice)
    fun onFinished(
        deviceFinder: FindDevice,
        deviceItems: List<DeviceItem>
    )

    fun onFailed(deviceFinder: FindDevice, errorCode: Int)
}