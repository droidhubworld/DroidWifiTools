# Droid WiFi Tools
Android library for finding connected devices on the same WiFi network. It can provide IP Addresses, device names, MAC Address and vendor names.
\

<img src="screenshot.png" width="360" height="780">

## Usage
Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Add the dependency

```
dependencies {
    implementation 'com.github.tejmagar:AndroidWiFiTools:1.0.2'
}
```

### Add Permission

```
<uses-permission android:name="android.permission.INTERNET"/>
```

### minSdk 21
```
```

### Find Connected Devices
```
 val findDevice:FindDevice = FindDevice(this, object : OnFindDeviceListener {
            override fun onStart(deviceFinder: FindDevice) {
               
            }

            override fun onFinished(deviceFinder: FindDevice, deviceItems: List<DeviceItem>) {
             
            }

            override fun onFailed(deviceFinder: FindDevice, errorCode: Int) {
                
            }

        })
        
findDevice.start()
```

### Set Timeout
Increasing timeout value may give you better results.

```
findDevice.setTimeout(5000).start();
```

### Get Mac Address from IP Address
```
String macAddress = MacAddressInfo.getMacAddressFromIp("192.168.147.1");
```
Before running this code, make sure you have already run ```deviceFinder.start();``` method.
\
Returns device Mac Address. If not found, it will return "unknown" or ```Constants.UNKOWN```

### Get current device IP Address
```
String ipAddress = findDevice.getCurrentDeviceIpAddress();
// or
String ipAddress = getCurrentDeviceIpAddress();
```

### Get current device Mac Address
```
String currentDeviceIpAddress = findDevice.getCurrentDeviceIpAddress();
String currentDeviceMacAddress = MacAddressInfo.getCurrentDeviceMacAddress(currentDeviceIpAddress);
```

### Get vendor name from Mac Address
```
String vendorName = VendorInfo.getVendorName("08:f4:58:b0:fd:4");
```

returns device Mac Address. If not found, it will return "unknown" or ```Constants.UNKNOWN```

 ```VendorInfo.init(context);``` will be automatically called while starting the device finder. If not, make sure you have initialized it first.
 
