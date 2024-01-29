package com.droidhubworld.droidwifitools

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.droidhubworld.nettoollib.FindDevice
import com.droidhubworld.nettoollib.interfaces.OnFindDeviceListener
import com.droidhubworld.nettoollib.models.DeviceItem


class MainActivity : AppCompatActivity(), DeviceListAdapter.OnClickListener {
    lateinit var mAdapter: DeviceListAdapter
    var findDevice: FindDevice? = null

    private val devices: MutableList<String> = ArrayList()
    private var start: Long = 0
    private var end: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeView()
        initFindDevice()
    }


    private fun initializeView() {
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

        val listView: RecyclerView = findViewById<RecyclerView>(R.id.deviceListView)


        mAdapter = DeviceListAdapter()
        mAdapter.setOnClickListener(this)
        listView.adapter = mAdapter

        findViewById<Button>(R.id.btn_stop).isEnabled = false

        findViewById<Button>(R.id.btn_find).setOnClickListener {
            findDevice?.setTimeout(500)?.start()
        }
        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            if (findDevice?.isRunning() == true) {
                findDevice?.stop()
            }
        }
    }

    private fun initFindDevice() {

        findDevice = FindDevice(this, object : OnFindDeviceListener {
            override fun onStart(deviceFinder: FindDevice) {
                start = System.currentTimeMillis()
                findViewById<Button>(R.id.btn_find).isEnabled = false
                findViewById<Button>(R.id.btn_stop).isEnabled = true
                mAdapter.clear()
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            }

            override fun onFinished(deviceFinder: FindDevice, deviceItems: List<DeviceItem>) {
                end = System.currentTimeMillis()
                val time: Float = (end - start) / 1000f
                Toast.makeText(
                    applicationContext, "Scan finished in " + time
                            + " seconds Found : ${deviceItems.size}", Toast.LENGTH_LONG
                ).show()
                for (deviceItem in deviceItems) {
                    val data = """
                        Device Name: ${deviceItem.deviceName}
                        Ip Address: ${deviceItem.ipAddress}
                        MAC Address: ${deviceItem.macAddress}
                        Vendor Name: ${deviceItem.vendorName}
                        """.trimIndent()
                    devices.add(data)
                    Log.e(
                        "onFinished",
                        "ANAND onComplete ::: > $data"
                    )

                }

                Log.e(
                    "onFinished",
                    "ANAND DATA SIZE ::: > ${devices.size}"
                )
                mAdapter.setContent(devices)
                findViewById<Button>(R.id.btn_find).isEnabled = true
                findViewById<Button>(R.id.btn_stop).isEnabled = false
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }

            override fun onFailed(deviceFinder: FindDevice, errorCode: Int) {
                Log.e("FIND DEVICE ERROR", "FIND DEVICE ERROR ERROR CODE : $errorCode")
                findViewById<Button>(R.id.btn_find).isEnabled = true
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                findViewById<Button>(R.id.btn_stop).isEnabled = false
            }

        })


    }

    override fun onItemClick(position: Int, data: String) {
        Toast.makeText(this, "Click on POSITION : $position\n$data", Toast.LENGTH_SHORT).show()
    }
}