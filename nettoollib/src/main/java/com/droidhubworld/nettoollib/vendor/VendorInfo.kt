package com.droidhubworld.nettoollib.vendor

import android.content.Context
import com.droidhubworld.nettoollib.utils.UNKNOWN
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale


/**
 * @Author: Anand Patel
 * @Date: 27,January,2024
 * @Email: anandkumara30@gmail.com
 */

object VendorInfo {
    private var json = "[]"
    private var jsonArray: JSONArray? = null
    private fun readVendorFile(context: Context) {
        if (jsonArray != null) {
            return
        }
        try {
            val inputStream = context.assets.open("vendors.json")
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            json = stringBuilder.toString()
            bufferedReader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(JSONException::class)
    private fun getJsonArray(context: Context): JSONArray? {
        if (jsonArray == null) {
            readVendorFile(context)
            jsonArray = JSONArray(json)
        }
        return jsonArray
    }

    fun getVendorName(context: Context, macAddress: String): String {
        try {
            val jsonArray = getJsonArray(context)
            var macAddressPrefix: String
            var vendorName: String
            for (i in 0 until jsonArray!!.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                macAddressPrefix = jsonObject.getString("m")
                vendorName = jsonObject.getString("n")
                if (macAddress.lowercase(Locale.getDefault()).startsWith(
                        macAddressPrefix.lowercase(
                            Locale.getDefault()
                        )
                    )
                ) {
                    return vendorName
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return UNKNOWN
    }
}

