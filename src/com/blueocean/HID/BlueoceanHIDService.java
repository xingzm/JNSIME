package com.blueocean.HID;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BlueoceanHIDService extends Service {

	public static final String ACTION_SERVICE = "com.blueocean.hid.BlueoceanHIDService";
	public static final String ACTION_USB_PERMISSION = "com.blueocean.hid.BLUEOCEAN_USB_PERMISSION";
	
	private BlueoceanHIDMonitor mBlueoceanHIDMonitor = null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		if (mBlueoceanHIDMonitor == null) {
			mBlueoceanHIDMonitor  = new BlueoceanHIDMonitor(this.getApplicationContext());
		}
		mBlueoceanHIDMonitor.getHIDDevices(getApplicationContext());
	}

}
