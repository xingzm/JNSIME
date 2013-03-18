package com.blueocean.HID;

import com.blueocean.hardware.JoystickTypeF;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class BlueoceanHIDDeviceClass {
	private static final String TAG = "BlueoceanHIDDeviceClass";
	private BlueoceanHIDTypeFData mBlueoceanHIDTypeFData = null;
	private Context context;
	
	public BlueoceanHIDDeviceClass(Context context) {
		this.context = context;
	}
	
	public void HIDDeviceClass(UsbManager manager, UsbDevice device) {
		int vendorId = device.getVendorId();
		int productId = device.getProductId();
		if ((vendorId ==JoystickTypeF. HID_TYPE_F_VENDOR_ID ) && (productId == JoystickTypeF.HID_TYPE_F_PRODUCT_ID)) {
		//	Log.e(TAG, "Found a HID device Type F");
			if (mBlueoceanHIDTypeFData == null) {
				mBlueoceanHIDTypeFData = new BlueoceanHIDTypeFData(context);
			}
			boolean deviceHas = false;
			for (BlueoceanConnectedHIDDeviceInfo bchd: BlueoceanHIDParams.usbDeviceList) {
				if (bchd.usbDevice != null && vendorId == bchd.usbDevice.getVendorId() && productId == bchd.usbDevice.getProductId()) {
					deviceHas = true;
					break;
				}
			}
			//Log.e(TAG, "deviceHas = " + deviceHas);
			if (!deviceHas) {
				
				if (mBlueoceanHIDTypeFData.getData(manager, device)) {
					BlueoceanHIDParams.insertConnectedDevice(device, null);
				}
				
			}
		}
	}
	
	public void HIDDeviceClose(UsbDevice device) {
		if (device != null) {
			int vendorId = device.getVendorId();
			int productId = device.getProductId();
			BlueoceanHIDParams.removeDeattachedDevice(device);
			if (vendorId == JoystickTypeF.HID_TYPE_F_VENDOR_ID && productId == JoystickTypeF.HID_TYPE_F_PRODUCT_ID) {
				if (mBlueoceanHIDTypeFData != null) {
					mBlueoceanHIDTypeFData.closeDevice();
				}
			}
		}
	}
}
