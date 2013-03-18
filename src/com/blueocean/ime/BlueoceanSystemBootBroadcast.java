package com.blueocean.ime;

import com.blueocean.HID.BlueoceanHIDService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BlueoceanSystemBootBroadcast extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		startBlueoceanCoreService(arg0, arg1);
	}
	private void startBlueoceanCoreService(Context arg0, Intent arg1) {
//		BlueoceanCore.context = arg0;
		Intent intent = new Intent("com.blueocean.ime.blueoceancore");
		arg0.startService(intent);
		intent = new Intent(BlueoceanHIDService.ACTION_SERVICE);
		arg0.startService(intent);
	}
	
	private void startBlueoceanIMEActivity(Context arg0, Intent arg1) {
		Intent intent = new Intent(arg0, BlueoceanIMEActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putBoolean("start_mode", true);
		intent.putExtras(bundle);
		arg0.startActivity(intent);
	}
}
