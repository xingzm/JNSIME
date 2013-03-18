package com.blueocean.ime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.os.ServiceManager;
import com.android.internal.view.IInputMethodManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;

public	class BlueoceanSwitchIMEBroadcast extends BroadcastReceiver implements Runnable {
		private static final String TAG = "BlueoceanSwitchIMEBroadcast";
		private Dialog d = null;
		private UIHandler handler = new UIHandler();
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String IMEId = arg1.getExtras().getString("COM.BLUEOCEAN_IME_IMEID");
			IBinder b = ServiceManager.getService(Context.INPUT_METHOD_SERVICE);
		    IInputMethodManager service = IInputMethodManager.Stub.asInterface(b);
		    boolean bret = false;
		    try {
		    	bret = service.setInputMethodEnabled(IMEId, true);
		    	Log.e(TAG, "setInputMethodEnabled = " + bret);
		    	final long ident = Binder.clearCallingIdentity();
//		        try {
		        	Settings.Secure.putString(arg0.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, IMEId);
//		            if (ActivityManagerNative.isSystemReady()) {
		            	Intent intent = new Intent(Intent.ACTION_INPUT_METHOD_CHANGED);
		                intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
		                intent.putExtra("input_method_id", IMEId);
		                arg0.sendBroadcast(intent);
		                try{
		                	Thread.sleep(500);
		                } catch (Exception e) {}
		                Log.e(TAG, "Switched to IME = " + IMEId);
//		                if (IMEId.equals(BlueoceanCore.JNSIMEID) && !BlueoceanCore.BlueoceanIMEActivityShowing) {
//		                	intent = new Intent();
//		                	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		                	intent.setClass(arg0, BlueoceanIMEActivity.class);
//		                	arg0.startActivity(intent);
//		                }
		                if (!IMEId.equals(BlueoceanCore.JNSIMEID)) {
//		                	Toast.makeText(arg0, arg0.getString(R.string.switch_off_game_mode), Toast.LENGTH_LONG).show();
//		                	BlueoceanCore.updateNotification(" ");
		                	AlertDialog.Builder b1 = new AlertDialog.Builder(arg0);
		                	b1.setMessage(arg0.getString(R.string.switch_off_game_mode));
//		                	b1.setPositiveButton(arg0.getString(R.string.sure), null);
		                	d = b1.create();
		                	d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		                	d.show();
		                	new Thread(this).start();
		                } else {
//		                	Toast.makeText(arg0, arg0.getString(R.string.switch_to_game_mode), Toast.LENGTH_LONG).show();
//		                	BlueoceanCore.updateNotification(" " + arg0.getString(R.string.switch_to_game_mode));
		                	AlertDialog.Builder b1 = new AlertDialog.Builder(arg0);
		                	b1.setMessage(arg0.getString(R.string.switch_to_game_mode));
//		                	b1.setPositiveButton(arg0.getString(R.string.sure), null);
		                	d = b1.create();
		                	d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		                	d.show();
		                	new Thread(this).start();
		                }
		                Log.e(TAG, "switched to IME = " + IMEId);
//		            }
//		           } finally {
//		               Binder.restoreCallingIdentity(ident);
//		           }

		       } catch (Exception e) {
		    	   Log.e(TAG, e.getMessage());   
		       }
		}
		
		class UIHandler extends Handler {
			public static final int MSG_CLOSE_DIALOG = 0X01;
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_CLOSE_DIALOG:
						if (d != null) d.dismiss();
						break;
				}
			}
		}

		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
				handler.sendEmptyMessage(UIHandler.MSG_CLOSE_DIALOG);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
}
