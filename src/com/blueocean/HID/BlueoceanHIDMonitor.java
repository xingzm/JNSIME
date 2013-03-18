package com.blueocean.HID;

import java.util.Map;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.os.IBinder;
import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.os.ServiceManager;

public class BlueoceanHIDMonitor implements Runnable {
	private static final String TAG = "BlueoceanHIDMonitor";
	private BlueoceanHIDDeviceClass mBlueoceanHIDDeviceClass = null;
	private UsbManager mUsbManager = null;
	private PendingIntent pi = null;
	private Context mContext = null;
	
	public BlueoceanHIDMonitor(Context context) {
		mContext = context;
		// listen for new devices
		if (mBlueoceanHIDDeviceClass == null) {
			mBlueoceanHIDDeviceClass = new BlueoceanHIDDeviceClass(context);
		}
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		if (pi == null) {
			pi = PendingIntent.getBroadcast(context, 0, new Intent(BlueoceanHIDService.ACTION_USB_PERMISSION), 0);
		}
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        context.registerReceiver(mUsbReceiver, filter);
	}
	
	public void getHIDDevices(Context context) {
		new Thread(this).start();
	}
		private void grantUsbPermission(UsbDevice mDevice) {
		if (mUsbManager != null && mDevice != null) {
		try {
		IBinder b = ServiceManager.getService(Context.USB_SERVICE);
		       IUsbManager service = IUsbManager.Stub.asInterface(b);
		Intent intent = new Intent();
		intent.putExtra(UsbManager.EXTRA_DEVICE, mDevice);
		service.grantDevicePermission(mDevice, mContext.getApplicationInfo().uid);
		service.setDevicePackage(mDevice, mContext.getPackageName());
		intent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);
		           pi.send(mContext, 0, intent);
		} catch (PendingIntent.CanceledException e) {
		           Log.w(TAG, "PendingIntent was cancelled");
		       } catch (RemoteException e) {
		           Log.e(TAG, "IUsbService connection failed", e);
		       }

		}
		}
	public void run() {
		// TODO Auto-generated method stub
		Log.e(TAG, "BlueoceanHIDMonitor thread running");
		while (true) {
			for (UsbDevice device: mUsbManager.getDeviceList().values()) {
				for (Map map: BlueoceanSupportedHIDController.controllerList) {
					if (map.get("vendor_id").equals(String.valueOf(device.getVendorId())) && map.get("product_id").equals(String.valueOf(device.getProductId()))) {
					//	mUsbManager.requestPermission(device, pi);
						grantUsbPermission(device);
						mBlueoceanHIDDeviceClass.HIDDeviceClass(mUsbManager, device);
					}
				}
			}
			
			for (BlueoceanConnectedHIDDeviceInfo bchd: BlueoceanHIDParams.usbDeviceList) {
				boolean deviceRemoved = true;
				for (UsbDevice device: mUsbManager.getDeviceList().values()) {
					if (bchd.usbDevice.getVendorId() == device.getVendorId() && bchd.usbDevice.getProductId() == device.getProductId()) {
						deviceRemoved = false;
					}
				}
				if (deviceRemoved) mBlueoceanHIDDeviceClass.HIDDeviceClose(bchd.usbDevice);
			}
			
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                Log.e(TAG, "ACTION_USB_DEVICE_ATTACHED hid device = " + device);
////                UsbInterface intf = findAdbInterface(device);
////                if (intf != null) {
////                    Log.e(TAG, "Found adb interface " + intf);
////                    setAdbInterface(device, intf);
////                }
//            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                Log.e(TAG, "ACTION_USB_DEVICE_DETACHED hid device = " + device);
////                String deviceName = device.getDeviceName();
////                if (mDevice != null && mDevice.equals(deviceName)) {
////                    log("adb interface removed");
////                    setAdbInterface(null, null);
////                }
//            }
//        }
//    };
}
