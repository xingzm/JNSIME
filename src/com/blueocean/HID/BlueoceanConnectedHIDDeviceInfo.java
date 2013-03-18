package com.blueocean.HID;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

public class BlueoceanConnectedHIDDeviceInfo {
	public int index = 0;
	public UsbDevice usbDevice = null;
	public UsbDeviceConnection usbDeviceConnection = null;
	public Object controllerObj = null;
}
