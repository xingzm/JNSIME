package com.blueocean.ime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

public class BlueoceanAccelerationProcess {
	private static final String TAG = "BlueoceanAccelerationProcess";
	private static int XAxisData = 0;
	private static int YAxisData = 0;
	private static Context mContext = null;
	public static boolean processAccelerationData(Context context, int keyCode, KeyEvent event) {
		mContext = context;
		SharedPreferences sp = mContext.getSharedPreferences("com.blueocean.ime_preferences", mContext.MODE_PRIVATE);
		Log.e(TAG, "boolean = " + sp.getBoolean(mContext.getString(R.string.acceleration_simulation_enable_title), false));
		if (sp.getBoolean(mContext.getString(R.string.acceleration_simulation_enable_title), false)) {
			switch(keyCode) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				generatePositiveXSimulationData();
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				generateNegativeXSimulationData();
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				generatePositiveYSimulationData();
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				generateNegativeYSimulationData();
				break;
			}
			return true;
		}
		return false;
	}
	private static void generatePositiveXSimulationData() {
		if (XAxisData < 1024) XAxisData += 10;
//		XAxisData *= 1024;
		int[] data = new int[3];
		data[0] = -XAxisData;
		data[1] = YAxisData;
		data[2] = 1024;
		setData(data);
	}
	private static void generateNegativeXSimulationData() {
		if (XAxisData > -1024) XAxisData -= 10;
		int[] data = new int[3];
		data[0] = -XAxisData;
		data[1] = YAxisData;
		data[2] = 1024;
		setData(data);
	}
	private static void generatePositiveYSimulationData() {
		if (YAxisData < 1024) YAxisData += 10;
		int[] data = new int[3];
		data[0] = XAxisData;
		data[1] = -YAxisData;
		data[2] = 1024;
		setData(data);
	}
	private static void generateNegativeYSimulationData() {
		if (YAxisData > -1024) YAxisData -= 10;
		int[] data = new int[3];
		data[0] = XAxisData;
		data[1] = -YAxisData;
		data[2] = 1024;
		setData(data);
	}
	private static void setData(int data[]) {
		if (!BlueoceanAccelerationManager.setData(BlueoceanAccelerationManager.fd, data)) {
			AlertDialog.Builder b = new AlertDialog.Builder(mContext);
			b.setMessage(R.string.set_acceleration_data_error);
			b.setPositiveButton(mContext.getString(R.string.sure), null);
			b.show();
		}
	}
}
