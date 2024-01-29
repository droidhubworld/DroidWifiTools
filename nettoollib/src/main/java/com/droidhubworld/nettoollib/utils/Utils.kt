package com.droidhubworld.nettoollib.utils

import org.apache.commons.lang3.ArrayUtils
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */

fun parseIpAddress(ip: Long): String? {
    try {
        val byteAddress = BigInteger.valueOf(ip).toByteArray()
        ArrayUtils.reverse(byteAddress)
        return InetAddress.getByAddress(byteAddress).hostAddress
    } catch (e: UnknownHostException) {
        e.printStackTrace()
    }
    return null
}