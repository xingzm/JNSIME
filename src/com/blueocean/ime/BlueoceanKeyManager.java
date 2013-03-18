package com.blueocean.ime;
 
import android.util.Log;

public class BlueoceanKeyManager {
	public static final int EV_KEY = 1;
	public static final String TAG = "BlueoceanKeyManager";
	public static native boolean createKeyThread();
	public static native void getKey(BlueoceanKeyEvent event);
	public static native boolean configTouchPanelPos(int x, int y);
	public static native void closeDevices();
	static {
		try {
			System.loadLibrary("jnskey");
		} catch (UnsatisfiedLinkError e) {Log.e(TAG, e.getMessage());}
	}
}
