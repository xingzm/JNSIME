package com.blueocean.HID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

public class BlueoceanHIDParams {
	public static final List<BlueoceanConnectedHIDDeviceInfo> usbDeviceList = new ArrayList<BlueoceanConnectedHIDDeviceInfo>();
	private static final String	TAG	= "BlueoceanHIDParams";
	
	public static boolean insertConnectedDevice(UsbDevice device, UsbDeviceConnection usbDeviceConnection) {
		for (BlueoceanConnectedHIDDeviceInfo bchd: usbDeviceList) {
			if (bchd.usbDevice.getVendorId() == device.getVendorId() && bchd.usbDevice.getProductId() == device.getProductId()) {
				return false;
			}
		}
		BlueoceanConnectedHIDDeviceInfo mBlueoceanConnectedHIDDeviceInfo = new BlueoceanConnectedHIDDeviceInfo();
		mBlueoceanConnectedHIDDeviceInfo.usbDevice = device;
		mBlueoceanConnectedHIDDeviceInfo.usbDeviceConnection = usbDeviceConnection;
		usbDeviceList.add(mBlueoceanConnectedHIDDeviceInfo);
		Log.e(TAG, "prev remove usbDeviceList = " + usbDeviceList + "inserted usbdevice = " + device);
		return true;
	}
	
	public static boolean removeDeattachedDevice(UsbDevice device) {
		for (BlueoceanConnectedHIDDeviceInfo bchd: usbDeviceList) {
			if (bchd.usbDevice.getVendorId() == device.getVendorId() && bchd.usbDevice.getProductId() == device.getProductId()) {
				Log.e(TAG, "prev remove usbDeviceList = " + usbDeviceList + " remove usbdevice = " + device);
				usbDeviceList.remove(bchd);
				return true;
			}
		}
		return false;
	}
}
