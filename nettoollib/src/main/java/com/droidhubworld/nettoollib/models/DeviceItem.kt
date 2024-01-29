package com.droidhubworld.nettoollib.models

import com.droidhubworld.nettoollib.utils.UNKNOWN


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */
data class DeviceItem(
    var ipAddress: String = UNKNOWN,
    var macAddress: String = UNKNOWN,
    var deviceName: String = UNKNOWN,
    var vendorName: String = UNKNOWN,
)
