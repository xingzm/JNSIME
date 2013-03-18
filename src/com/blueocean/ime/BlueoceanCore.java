package com.blueocean.ime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.Preference;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.os.SystemClock;
import android.view.IWindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.os.ServiceManager;
import com.android.internal.view.IInputMethodManager;
import com.blueocean.hardware.JoystickTypeF;
import com.blueocean.ime.BlueoceanCore.Pos;

import android.app.ActivityManagerNative;
import android.view.MotionEvent.PointerCoords;  
import android.view.MotionEvent.PointerProperties;  

public class BlueoceanCore extends Service {
	private static final String TAG = "BlueoceanCore";
	private BlueoceanKeyEvent keyEvent;
	private final int MSG_PROCESS_KEY = 0X01;
	private final int MSG_OPEN_DEVICE_ERROR = 0x02;
	public static Context context = null;
	private Service mService;
	public static Preference currentPreference;
	public static BlueoceanInputKeyTip mBlueoceanInputKeyTip;
	public static boolean IMEEnabled = false;
	public static boolean keyMaping = false;
	public static BlueoceanInputMethodService blueOceanInputMethodService;
	private int lastKey = 0;
	private int lastscanCode = 0;
	private static KeyEvent event;
	public static final String JNSIMEID = "com.blueocean.ime/.BlueoceanInputMethodService";
	public  static String lastIMEID = "";
	public static boolean touchConfiging = false;
	public static boolean gameStart = false;
	public static boolean foundTouchKeyMap = false;
	public static List<BlueoceanProfile> keyList;
	public static boolean BlueoceanIMEActivityShowing = false;
	private static NotificationManager mNotificationManager;

	public static final int KEYCODE_BUTTON_1 = 188;
	public static final int KEYCODE_BUTTON_2 = 189;
	public static final int KEYCODE_BUTTON_3 = 190;
	public static final int KEYCODE_BUTTON_4 = 191;
	public static final int KEYCODE_BUTTON_5 = 192;
	public static final int KEYCODE_BUTTON_6 = 193;
	public static final int KEYCODE_BUTTON_7 = 194;
	public static final int KEYCODE_BUTTON_8 = 195;
	public static final int KEYCODE_BUTTON_9 = 196;
	public static final int KEYCODE_BUTTON_10 = 197;
	public static final int KEYCODE_BUTTON_11 = 198;
	public static final int KEYCODE_BUTTON_12 = 199;
	public static final int KEYCODE_BUTTON_13 = 200;
	public static final int KEYCODE_BUTTON_14 = 201;
	public static final int KEYCODE_BUTTON_15 = 202;
	public static final int KEYCODE_BUTTON_16 = 203;

	public static final int KEYCODE_START_IME = 111;
	public static final int KEYCODE_CLOSE_IME = 109;

	public static final int KEYCODE_START_TPCONFIG = KeyEvent.KEYCODE_SEARCH;

	static int s_Touchkey_Pressed_Num = 0;

	public static Instrumentation instrumentation = new Instrumentation();
	private static float gx = 0.0f;
	private static float gy = 0.0f;

	//add by steven
	static List<Pos> pos_list = new ArrayList();
	static boolean pos_list_lock = false;
	static boolean zoom_stat = false;
	public static  Queue<Pos> motionQueue = new ConcurrentLinkedQueue<Pos>();

	public static class Pos
	{
		float x;
		float y;
		int tag;
		int action;

		public Pos(float x, float y, int action, int tag)
		{
			this.x = x;
			this.y = y;
			this.tag = tag;
			this.action = action;
		}
		public Pos(float x, float y, int action)
		{
			this.x = x;
			this.y = y;
			tag = 0xff;
			this.action = action;
		}
		public Pos(float x, float y)
		{
			this.x = x;
			this.y = y;
			tag = 0xff;
		}
		void setTag(int tag)
		{
			this.tag = tag;
		}

	}
	//add end
	public void init() {
		lastIMEID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		if (lastIMEID.equals(JNSIMEID)) {
			SharedPreferences sp = context.getSharedPreferences("com.blueocean.ime_preferences", context.MODE_PRIVATE);
			lastIMEID = sp.getString("LASTIMEID", "");
		} else {
			SharedPreferences sp = context.getSharedPreferences("com.blueocean.ime_preferences", context.MODE_PRIVATE);
			Editor e = sp.edit();
			e.putString("LASTIMEID", lastIMEID);
			e.commit();
		}
		Log.e(TAG, "lastIMEID = " + lastIMEID);
		keyList = new ArrayList<BlueoceanProfile>();
	}
	public void startCore() {
		keyEvent = new BlueoceanKeyEvent();
		//		keyEventProcessHandler.postDelayed(KeyManagerRunable, 5000);
		Log.e(TAG, " startCore");
		if (!BlueoceanKeyManager.createKeyThread()) {
			Log.e(TAG, "createKeyThread failed ");
			Message msg = keyEventProcessHandler.obtainMessage(MSG_OPEN_DEVICE_ERROR);
			keyEventProcessHandler.sendMessage(msg);
		} else {
			keyEvent = new BlueoceanKeyEvent();
			//	   	   keyEventProcessHandler.postDelayed(KeyManagerRunable, 100);
			context = this;
		}
		new Thread() {
			public void run() {
				while (true) {
					BlueoceanKeyManager.getKey(keyEvent);
					//					Log.e(TAG, "runable type = " + keyEvent.type);
					if (keyEvent.type == BlueoceanKeyManager.EV_KEY) {
						Message msg = keyEventProcessHandler.obtainMessage(MSG_PROCESS_KEY);
						keyEventProcessHandler.sendMessage(msg);
					}
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//				keyEventProcessHandler.postDelayed(KeyManagerRunable, 5000);
			}
		}.start();

		new Thread() 
		{
			public void run()
			{
				Pos pos;
				while(true)
				{
					try
					{
						if((pos = motionQueue.poll()) != null)
						{	
							int action = pos.action;
							switch(action)
							{
							case MotionEvent.ACTION_DOWN:
								BlueoceanCore.sendMotionDownByInstrumentation(pos);
								break;
							case MotionEvent.ACTION_MOVE:
								BlueoceanCore.sendMotionMoveByInstrumentation(pos);
								break;
							case MotionEvent.ACTION_UP:
								BlueoceanCore.sendMotionUpByInstrumentation(pos);
								break;
							}
						}
					}
					catch(Exception e)
					{
						motionQueue =  new ConcurrentLinkedQueue<Pos>();
						e.printStackTrace();
					}	

				}

			}
		}.start();

	}
	Handler keyEventProcessHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MSG_PROCESS_KEY:
				Log.e(TAG, "lastscanCode = " + lastscanCode + " keyEvent.scancode = " + keyEvent.scanCode + "keyEvent.type = " + keyEvent.type);
				if (lastscanCode != keyEvent.scanCode) {
					Log.e(TAG, "MSG TYPE = " + keyEvent.type + " scancode = " + keyEvent.scanCode + " keycode = " + keyEvent.keyCode + " value = " + keyEvent.value);
					if (keyEvent.type == BlueoceanKeyManager.EV_KEY && keyEvent.scanCode == KEYCODE_START_IME) {
						if (!IMEEnabled) {
							IMEEnabled = true;
							enableIME(JNSIMEID);
							BlueoceanMotionManager.fd = BlueoceanMotionManager.openDevice();
							BlueoceanAccelerationManager.fd = BlueoceanAccelerationManager.openDevice();
						}
					} else if (keyEvent.type == BlueoceanKeyManager.EV_KEY && keyEvent.scanCode == KEYCODE_CLOSE_IME){
						if (IMEEnabled) {
							Log.e(TAG, "lastIMEID = " + lastIMEID + " will be switch");
							enableIME(lastIMEID);
							IMEEnabled = false;
							BlueoceanMotionManager.closeDevice(BlueoceanMotionManager.fd);
							BlueoceanMotionManager.fd = 0;
							BlueoceanAccelerationManager.closeDevice(BlueoceanAccelerationManager.fd);
							BlueoceanAccelerationManager.fd = 0;
						}
					}
					lastscanCode = keyEvent.scanCode;
				}	
				break;
			case MSG_OPEN_DEVICE_ERROR:
				//				AlertDialog.Builder b = new AlertDialog.Builder(BlueoceanIMEActivity.mBlueoceanIMEActivity);
				//				b.setMessage(mService.getApplication().getResources().getString(R.string.open_device_error));
				//				b.setPositiveButton(mService.getApplication().getResources().getString(R.string.cancel), null);
				//				b.show();
				//	Toast.makeText(BlueoceanIMEActivity.mBlueoceanIMEActivity, mService.getApplication().getResources().getString(R.string.open_device_error), Toast.LENGTH_SHORT).show();
				break;
			}
			//			keyEventProcessHandler.postDelayed(KeyManagerRunable, 5000);
		}
	};

	public static void sharedPreferencesConfig(Context mContext, int keyCode) {
		context = mContext;
		SharedPreferences sp = mContext.getSharedPreferences("com.blueocean.ime_preferences", Context.MODE_PRIVATE);
		Editor e = sp.edit();
		String str = "";
		switch (keyCode) {
		case KeyEvent.KEYCODE_0:
			str = "0";
			break;
		case KeyEvent.KEYCODE_1:
			str = "1";
			break;
		case KeyEvent.KEYCODE_2:
			str = "2";
			break;
		case KeyEvent.KEYCODE_3:
			str = "3";
			break;
		case KeyEvent.KEYCODE_4:
			str = "4";
			break;
		case KeyEvent.KEYCODE_5:
			str = "5";
			break;
		case KeyEvent.KEYCODE_6:
			str = "6";
			break;
		case KeyEvent.KEYCODE_7:
			str = "7";
			break;
		case KeyEvent.KEYCODE_8:
			str = "8";
			break;
		case KeyEvent.KEYCODE_9:
			str = "9";
			break;
		case KeyEvent.KEYCODE_A:
			str = "A";
			break;
		case KeyEvent.KEYCODE_B:
			str = "B";
			break;
		case KeyEvent.KEYCODE_C:
			str = "C";
			break;
		case KeyEvent.KEYCODE_D:
			str = "D";
			break;
		case KeyEvent.KEYCODE_E:
			str = "E";
			break;
		case KeyEvent.KEYCODE_F:
			str = "F";
			break;
		case KeyEvent.KEYCODE_G:
			str = "G";
			break;
		case KeyEvent.KEYCODE_H:
			str = "H";
			break;
		case KeyEvent.KEYCODE_I:
			str = "I";
			break;
		case KeyEvent.KEYCODE_J:
			str = "J";
			break;
		case KeyEvent.KEYCODE_K:
			str = "K";
			break;
		case KeyEvent.KEYCODE_L:
			str = "L";
			break;
		case KeyEvent.KEYCODE_N:
			str = "N";
			break;
		case KeyEvent.KEYCODE_O:
			str = "O";
			break;
		case KeyEvent.KEYCODE_P:
			str = "P";
			break;
		case KeyEvent.KEYCODE_Q:
			str = "Q";
			break;
		case KeyEvent.KEYCODE_R:
			str = "R";
			break;
		case KeyEvent.KEYCODE_S:
			str = "S";
			break;
		case KeyEvent.KEYCODE_T:
			str = "T";
			break;
		case KeyEvent.KEYCODE_U:
			str = "U";
			break;
		case KeyEvent.KEYCODE_W:
			str = "W";
			break;
		case KeyEvent.KEYCODE_Y:
			str = "Y";
			break;
		case KeyEvent.KEYCODE_X:
			str = "X";
			break;
		case KeyEvent.KEYCODE_Z:
			str = "Z";
			break;
		case KeyEvent.KEYCODE_M:
			str = "M";
			break;
		case KeyEvent.KEYCODE_V:
			str = "V";
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			str = "KEYCODE_VOLUME_DOWN";
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			str = "KEYCODE_VOLUME_UP";
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:
			str = "KEYCODE_ALT_LEFT";
			break;
		case KeyEvent.KEYCODE_ALT_RIGHT:
			str = "KEYCODE_ALT_LEFT";
			break;
		case KeyEvent.KEYCODE_BACKSLASH:
			str = "KEYCODE_BACKSLASH";
			break;
		case KeyEvent.KEYCODE_BUTTON_A:
			str = "KEYCODE_BUTTON_A";
			break;
		case KeyEvent.KEYCODE_BUTTON_B:
			str = "KEYCODE_BUTTON_B";
			break;
		case KeyEvent.KEYCODE_BUTTON_C:
			str = "KEYCODE_BUTTON_C";
			break;
		case KeyEvent.KEYCODE_BUTTON_L1:
			str = "KEYCODE_BUTTON_L1";
			break;
		case KeyEvent.KEYCODE_BUTTON_L2:
			str = "KEYCODE_BUTTON_L2";
			break;
		case KeyEvent.KEYCODE_BUTTON_MODE:
			str = "KEYCODE_BUTTON_MODE";
			break;
		case KeyEvent.KEYCODE_BUTTON_R1:
			str = "KEYCODE_BUTTON_R1";
			break;
		case KeyEvent.KEYCODE_BUTTON_R2:
			str = "KEYCODE_BUTTON_R2";
			break;
		case KeyEvent.KEYCODE_BUTTON_SELECT:
			str = "KEYCODE_BUTTON_SELECT";
			break;
		case KeyEvent.KEYCODE_BUTTON_START:
			str = "KEYCODE_BUTTON_START";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBL:
			str = "KEYCODE_BUTTON_THUMBL";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBR:
			str = "KEYCODE_BUTTON_THUMBR";
			break;
		case KeyEvent.KEYCODE_BUTTON_X:
			str = "KEYCODE_BUTTON_X";
			break;
		case KeyEvent.KEYCODE_BUTTON_Y:
			str = "KEYCODE_BUTTON_Y";
			break;
		case KeyEvent.KEYCODE_BUTTON_Z:
			str = "KEYCODE_BUTTON_Z";
			break;
		case KeyEvent.KEYCODE_COMMA:
			str = "KEYCODE_COMMA";
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			str = "KEYCODE_DPAD_CENTER";
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			str = "KEYCODE_DPAD_DOWN";
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			str = "KEYCODE_DPAD_LEFT";
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			str = "KEYCODE_DPAD_RIGHT";
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			str = "KEYCODE_DPAD_UP";
			break;
		case KeyEvent.KEYCODE_ENTER:
			str = "KEYCODE_ENTER";
			break;
		case KeyEvent.KEYCODE_EQUALS:
			str = "KEYCODE_EQUALS";
			break;
		case KeyEvent.KEYCODE_GRAVE:
			str = "KEYCODE_GRAVE";
			break;
		case KeyEvent.KEYCODE_LEFT_BRACKET:
			str = "KEYCODE_LEFT_BRACKET";
			break;
		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			str = "KEYCODE_RIGHT_BRACKET";
			break;
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			str = "KEYCODE_MEDIA_FAST_FORWARD";
			break;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			str = "KEYCODE_MEDIA_NEXT";
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			str = "KEYCODE_MEDIA_PLAY_PAUSE";
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			str = "KEYCODE_MEDIA_PREVIOUS";
			break;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
			str = "KEYCODE_MEDIA_REWIND";
			break;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			str = "KEYCODE_MEDIA_STOP";
			break;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			str = "KEYCODE_PAGE_DOWN";
			break;
		case KeyEvent.KEYCODE_PAGE_UP:
			str = "KEYCODE_PAGE_UP";
			break;
		case KeyEvent.KEYCODE_PERIOD:
			str = "KEYCODE_PERIOD";
			break;
		case KeyEvent.KEYCODE_PLUS:
			str = "KEYCODE_PLUS";
			break;
		case KeyEvent.KEYCODE_POUND:
			str = "KEYCODE_POUND";
			break;
		case KeyEvent.KEYCODE_SEARCH:
			str = "KEYCODE_SEARCH";
			break;
		case KeyEvent.KEYCODE_MINUS:
			str = "KEYCODE_MINUS";
			break;
		case KeyEvent.KEYCODE_SEMICOLON:
			str = "KEYCODE_SEMICOLON";
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			str = "KEYCODE_SHIFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			str = "KEYCODE_SHIFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SLASH:
			str = "KEYCODE_SLASH";
			break;
		case KeyEvent.KEYCODE_SOFT_LEFT:
			str = "KEYCODE_SOFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SOFT_RIGHT:
			str = "KEYCODE_SOFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SPACE:
			str = "KEYCODE_SPACE";
			break;
		case KeyEvent.KEYCODE_STAR:
			str = "KEYCODE_STAR";
			break;
		case KeyEvent.KEYCODE_TAB:
			str = "KEYCODE_TAB";
			break;
		case KeyEvent.KEYCODE_UNKNOWN:
			str = "KEYCODE_UNKNOWN";
			break;
		case KeyEvent.KEYCODE_BACK:
			str = "UNKNOWN";
			break;
		default: break;
		}
		e.putString(currentPreference.getKey(), str);
		currentPreference.setSummary(str);
		//		if (!BlueOceanConfig.tipDialog.preference.getSummary().equals("UNKNOWN"))
		e.putInt(currentPreference.getKey() + "_INT", keyCode);
		e.commit();
		keyMaping = false;
		mBlueoceanInputKeyTip.dismiss();
	}

	public static boolean sendKeyMap(Context mContext, int action, int keyCode) {
		if (keyCode == 0) return false;
		SharedPreferences sp = null;
		context = mContext;
		try { 
			sp = mContext.getSharedPreferences("com.blueocean.ime_preferences", Context.MODE_PRIVATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDpadCenterKey + "_INT", 0)){
			Log.e(TAG, "keyCode = " + keyCode + " action= " + action + " will send = KEYCODE_DPAD_CENTER");
			sendKeyEvent(action, KeyEvent.KEYCODE_DPAD_CENTER);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDpadLeftKey + "_INT", 0)) {
			Log.e(TAG, "keyCode = " + keyCode + " action= " + action + " will send = KEYCODE_DPAD_LEFT");
			sendKeyEvent(action, KeyEvent.KEYCODE_DPAD_LEFT);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDpadRightKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_DPAD_RIGHT);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDpadDownKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_DPAD_DOWN);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDpadUpKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_DPAD_UP);
			return true;
		} 
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonAKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_A);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonBKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_B);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonCKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_C);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonXKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_X);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonYKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_Y);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonZKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_Z);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonL1Key + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_L1);
			return true;
		} 
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonR1Key + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_R1);//Log.d("testdownandup", "getlock");
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonL2Key + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_L2);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonR2Key + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_R2);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonThumblKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_THUMBL);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonThumbrKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_THUMBR);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonStartKey + "_INT", 0)) { 
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_START);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonSelectKey + "_INT", 0)) { 
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_SELECT);
			return true;
		}	
		if (keyCode == sp.getInt(BlueoceanPreferences.listButtonModeKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_BUTTON_MODE);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton1Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_1);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton2Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_2);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton3Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_3);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton4Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_4);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton5Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_5);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton6Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_6);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton7Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_7);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton8Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_8);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton9Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_9);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton10Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_10);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton11Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_11);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton12Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_12);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton13Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_13);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton14Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_14);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton15Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_15);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listButton16Key + "_INT", 0)) {
			sendKeyEvent(action, KEYCODE_BUTTON_16);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listAKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_A);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listBKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_B);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listCKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_C);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listDKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_D);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listEKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_E);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listFKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_F);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listGKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_G);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listHKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_H);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listIKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_I);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listJKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_J);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listKKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_K);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listLKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_L);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listMKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_M);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listNKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_N);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listOKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_O);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listPKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_P);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listQKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_Q);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listRKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_R);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listSKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_S);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listTKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_T);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listUKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_U);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listVKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_V);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listWKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_W);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listXKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_X);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listYKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_Y);
			return true;
		}
		if (keyCode == sp.getInt(BlueoceanPreferences.listZKey + "_INT", 0)) {
			sendKeyEvent(action, KeyEvent.KEYCODE_Z);
			return true;
		}
		return false;
		//		sendKeyEvent(action, keyCode);
	}

	public static void setButtonSummary() {
		SharedPreferences sp = null;
		try {
			sp = context.getSharedPreferences("com.blueocean.ime_preferences", context.MODE_PRIVATE);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		String defValue = "UNKNOWN";
		BlueoceanPreferences.listDpadCenter.setSummary(sp.getString(BlueoceanPreferences.listDpadCenter.getKey(), defValue));
		BlueoceanPreferences.listDpadLeft.setSummary(sp.getString(BlueoceanPreferences.listDpadLeft.getKey(), defValue));
		BlueoceanPreferences.listDpadRight.setSummary(sp.getString(BlueoceanPreferences.listDpadRight.getKey(), defValue));
		BlueoceanPreferences.listDpadDown.setSummary(sp.getString(BlueoceanPreferences.listDpadDown.getKey(), defValue));
		BlueoceanPreferences.listDpadUp.setSummary(sp.getString(BlueoceanPreferences.listDpadUp.getKey(), defValue));
		BlueoceanPreferences.listButtonA.setSummary(sp.getString(BlueoceanPreferences.listButtonA.getKey(), defValue));
		BlueoceanPreferences.listButtonB.setSummary(sp.getString(BlueoceanPreferences.listButtonB.getKey(), defValue));
		BlueoceanPreferences.listButtonC.setSummary(sp.getString(BlueoceanPreferences.listButtonC.getKey(), defValue));
		BlueoceanPreferences.listButtonX.setSummary(sp.getString(BlueoceanPreferences.listButtonX.getKey(), defValue));
		BlueoceanPreferences.listButtonY.setSummary(sp.getString(BlueoceanPreferences.listButtonY.getKey(), defValue));
		BlueoceanPreferences.listButtonZ.setSummary(sp.getString(BlueoceanPreferences.listButtonZ.getKey(), defValue));
		BlueoceanPreferences.listButtonL1.setSummary(sp.getString(BlueoceanPreferences.listButtonL1.getKey(), defValue));
		BlueoceanPreferences.listButtonR1.setSummary(sp.getString(BlueoceanPreferences.listButtonR1.getKey(), defValue));
		BlueoceanPreferences.listButtonL2.setSummary(sp.getString(BlueoceanPreferences.listButtonL2.getKey(), defValue));
		BlueoceanPreferences.listButtonR2.setSummary(sp.getString(BlueoceanPreferences.listButtonR2.getKey(), defValue));
		BlueoceanPreferences.listButtonThumbl.setSummary(sp.getString(BlueoceanPreferences.listButtonThumbl.getKey(), defValue));
		BlueoceanPreferences.listButtonThumbr.setSummary(sp.getString(BlueoceanPreferences.listButtonThumbr.getKey(), defValue));
		BlueoceanPreferences.listButtonStart.setSummary(sp.getString(BlueoceanPreferences.listButtonStart.getKey(), defValue));
		BlueoceanPreferences.listButtonSelect.setSummary(sp.getString(BlueoceanPreferences.listButtonSelect.getKey(), defValue));
		BlueoceanPreferences.listButtonMode.setSummary(sp.getString(BlueoceanPreferences.listButtonMode.getKey(), defValue));
		BlueoceanPreferences.listButton1.setSummary(sp.getString(BlueoceanPreferences.listButton1.getKey(), defValue));
		BlueoceanPreferences.listButton2.setSummary(sp.getString(BlueoceanPreferences.listButton2.getKey(), defValue));
		BlueoceanPreferences.listButton3.setSummary(sp.getString(BlueoceanPreferences.listButton3.getKey(), defValue));
		BlueoceanPreferences.listButton4.setSummary(sp.getString(BlueoceanPreferences.listButton4.getKey(), defValue));
		BlueoceanPreferences.listButton5.setSummary(sp.getString(BlueoceanPreferences.listButton5.getKey(), defValue));
		BlueoceanPreferences.listButton6.setSummary(sp.getString(BlueoceanPreferences.listButton6.getKey(), defValue));
		BlueoceanPreferences.listButton7.setSummary(sp.getString(BlueoceanPreferences.listButton7.getKey(), defValue));
		BlueoceanPreferences.listButton8.setSummary(sp.getString(BlueoceanPreferences.listButton8.getKey(), defValue));
		BlueoceanPreferences.listButton9.setSummary(sp.getString(BlueoceanPreferences.listButton9.getKey(), defValue));
		BlueoceanPreferences.listButton10.setSummary(sp.getString(BlueoceanPreferences.listButton10.getKey(), defValue));
		BlueoceanPreferences.listButton11.setSummary(sp.getString(BlueoceanPreferences.listButton11.getKey(), defValue));
		BlueoceanPreferences.listButton12.setSummary(sp.getString(BlueoceanPreferences.listButton12.getKey(), defValue));
		BlueoceanPreferences.listButton13.setSummary(sp.getString(BlueoceanPreferences.listButton13.getKey(), defValue));
		BlueoceanPreferences.listButton14.setSummary(sp.getString(BlueoceanPreferences.listButton14.getKey(), defValue));
		BlueoceanPreferences.listButton15.setSummary(sp.getString(BlueoceanPreferences.listButton15.getKey(), defValue));
		BlueoceanPreferences.listButton16.setSummary(sp.getString(BlueoceanPreferences.listButton16.getKey(), defValue));
		BlueoceanPreferences.listA.setSummary(sp.getString(BlueoceanPreferences.listA.getKey(), defValue));
		BlueoceanPreferences.listB.setSummary(sp.getString(BlueoceanPreferences.listB.getKey(), defValue));
		BlueoceanPreferences.listC.setSummary(sp.getString(BlueoceanPreferences.listC.getKey(), defValue));
		BlueoceanPreferences.listD.setSummary(sp.getString(BlueoceanPreferences.listD.getKey(), defValue));
		BlueoceanPreferences.listE.setSummary(sp.getString(BlueoceanPreferences.listE.getKey(), defValue));
		BlueoceanPreferences.listF.setSummary(sp.getString(BlueoceanPreferences.listF.getKey(), defValue));
		BlueoceanPreferences.listG.setSummary(sp.getString(BlueoceanPreferences.listG.getKey(), defValue));
		BlueoceanPreferences.listH.setSummary(sp.getString(BlueoceanPreferences.listH.getKey(), defValue));
		BlueoceanPreferences.listI.setSummary(sp.getString(BlueoceanPreferences.listI.getKey(), defValue));
		BlueoceanPreferences.listJ.setSummary(sp.getString(BlueoceanPreferences.listJ.getKey(), defValue));
		BlueoceanPreferences.listK.setSummary(sp.getString(BlueoceanPreferences.listK.getKey(), defValue));
		BlueoceanPreferences.listL.setSummary(sp.getString(BlueoceanPreferences.listL.getKey(), defValue));
		BlueoceanPreferences.listM.setSummary(sp.getString(BlueoceanPreferences.listM.getKey(), defValue));
		BlueoceanPreferences.listN.setSummary(sp.getString(BlueoceanPreferences.listN.getKey(), defValue));
		BlueoceanPreferences.listO.setSummary(sp.getString(BlueoceanPreferences.listO.getKey(), defValue));
		BlueoceanPreferences.listP.setSummary(sp.getString(BlueoceanPreferences.listP.getKey(), defValue));
		BlueoceanPreferences.listQ.setSummary(sp.getString(BlueoceanPreferences.listQ.getKey(), defValue));
		BlueoceanPreferences.listR.setSummary(sp.getString(BlueoceanPreferences.listR.getKey(), defValue));
		BlueoceanPreferences.listS.setSummary(sp.getString(BlueoceanPreferences.listS.getKey(), defValue));
		BlueoceanPreferences.listT.setSummary(sp.getString(BlueoceanPreferences.listT.getKey(), defValue));
		BlueoceanPreferences.listU.setSummary(sp.getString(BlueoceanPreferences.listU.getKey(), defValue));
		BlueoceanPreferences.listV.setSummary(sp.getString(BlueoceanPreferences.listV.getKey(), defValue));
		BlueoceanPreferences.listW.setSummary(sp.getString(BlueoceanPreferences.listW.getKey(), defValue));
		BlueoceanPreferences.listX.setSummary(sp.getString(BlueoceanPreferences.listX.getKey(), defValue));
		BlueoceanPreferences.listY.setSummary(sp.getString(BlueoceanPreferences.listY.getKey(), defValue));
		BlueoceanPreferences.listZ.setSummary(sp.getString(BlueoceanPreferences.listZ.getKey(), defValue));
	}

	public static void sendKeyEvent(int action, int keyCode) {
		//		if (blueOceanInputMethodService == null) {
		//			Log.e(TAG, " blueOceanInputMethodService is null so can't inject key event");
		//			return;
		//		}
		event = new KeyEvent(action, keyCode);
		sendKeyEventByInstrumentation(event);

		//		if (blueOceanInputMethodService.getCurrentInputConnection().sendKeyEvent(event)) {
		//			Log.e(TAG, "blueOceanInputMethodService input connection sent a keyevent: action = " + action + " keyCode = " + keyCode);
		//		}
	}
	public static synchronized void sendMotionZoomIn()
	{
		BlueoceanCore.motionQueue.offer(new Pos(200, 300, MotionEvent.ACTION_DOWN, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
		BlueoceanCore.motionQueue.offer(new Pos(200, 500, MotionEvent.ACTION_DOWN, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));
		
		for(int i = 0; i<10; i++)
		{
			BlueoceanCore.motionQueue.offer(new Pos(200, 300 +5*i, MotionEvent.ACTION_MOVE, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
			BlueoceanCore.motionQueue.offer(new Pos(200, 500 -5*i, MotionEvent.ACTION_MOVE, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));

		}
		BlueoceanCore.motionQueue.offer(new Pos(200, 300 +5 * 9, MotionEvent.ACTION_UP, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
		BlueoceanCore.motionQueue.offer(new Pos(200, 500 -5 * 9, MotionEvent.ACTION_UP, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));
		/*
		Log.d(TAG, "zoom in");
		while(pos_list_lock);
		sendMotionDownByInstrumentation(0x3,200,300);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		Log.d(TAG, "pos 1 down");
		while(pos_list_lock);
		sendMotionDownByInstrumentation(0x4,200,500);

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		Log.d(TAG, "pos 2 down");
		for(int i = 1; i < 10; i++)
		{
			while(pos_list_lock);
			sendMotionMoveByInstrumentation(0x3,200, 300 + 5*i);

			Log.d(TAG, "pos 1 move");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {	
				e.printStackTrace();			
			}

			while(pos_list_lock);
			sendMotionMoveByInstrumentation(0x4,200,500 - 5*i);

			Log.d(TAG, "pos 2 move");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {	
				e.printStackTrace();			
			}

		}
		while(pos_list_lock);
		sendMotionUpByInstrumentation(0x3,200,345);
		Log.d(TAG, "pos 1 up");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		while(pos_list_lock);
		sendMotionUpByInstrumentation(0x4,200,455);
		Log.d(TAG, "pos 2 up");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		while(pos_list_lock);
		*/
		BlueoceanCore.zoom_stat = false;
		
	}
	public static synchronized void sendMotionZoomOut()
	{
		Log.d(TAG, "zoom out");
		BlueoceanCore.motionQueue.offer(new Pos(200, 300, MotionEvent.ACTION_DOWN, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
		BlueoceanCore.motionQueue.offer(new Pos(200, 500, MotionEvent.ACTION_DOWN, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));
		
		for(int i = 0; i<10; i++)
		{
			BlueoceanCore.motionQueue.offer(new Pos(200, 300 -5*i, MotionEvent.ACTION_MOVE, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
			BlueoceanCore.motionQueue.offer(new Pos(200, 500 +5*i, MotionEvent.ACTION_MOVE, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));

		}
		BlueoceanCore.motionQueue.offer(new Pos(200, 300 - 5 * 9, MotionEvent.ACTION_UP, JoystickTypeF.JOYSTICK_ZOOM_1_TAG));
		BlueoceanCore.motionQueue.offer(new Pos(200, 500 + 5 * 9, MotionEvent.ACTION_UP, JoystickTypeF.JOYSTICK_ZOOM_2_TAG));
		/*
		while(pos_list_lock);
		sendMotionDownByInstrumentation(0x3,200,300);
		Log.d(TAG, "pos 1 down");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		while(pos_list_lock);
		sendMotionDownByInstrumentation(0x4,200,500);
		Log.d(TAG, "pos 2 down");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		for(int i = 0; i < 10; i++)
		{
			while(pos_list_lock);
			sendMotionMoveByInstrumentation(0x3,200,300 - 5*i);

			Log.d(TAG, "pos 1 move");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {	
				e.printStackTrace();			
			}

			while(pos_list_lock);
			sendMotionMoveByInstrumentation(0x4,200,500 + 5*i);

			Log.d(TAG, "pos 2 move");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {	
				e.printStackTrace();			
			}

		}
		while(pos_list_lock);
		sendMotionUpByInstrumentation(0x3,200,255);

		Log.d(TAG, "pos 1 up");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		while(pos_list_lock);
		sendMotionUpByInstrumentation(0x4,200,545);
		Log.d(TAG, "pos 2 up");

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {	
			e.printStackTrace();			
		}

		while(pos_list_lock);
		*/
		BlueoceanCore.zoom_stat = false;
	}
	public static void sendMotionDownByInstrumentation(Pos pos_in) 
	{

		pos_list.add(pos_in);
		final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
		final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
		Log.d(TAG,"pointcount="+pos_list.size());

		Log.d(TAG, "add pos x ="+pos_in.x+"y="+pos_in.y );
		for(int i = 0; i <pos_list.size(); i++)
		{
			Pos pos  = pos_list.get(i);
			PointerProperties pp = new PointerProperties();  
			pp.id= i;  
			pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
			properties[i] = pp;  

			PointerCoords pc = new PointerCoords();  
			pc.x = pos.x;  
			pc.y = pos.y;  
			pc.pressure = 1;  
			pc.size = 1;  
			pointerCoords[i] = pc;  
		}
		Log.d(TAG, "itroter list ok");
		//add by steven
		Log.d(TAG,"x ="+pointerCoords[0].x+",y="+pointerCoords[0].y);
		if(pos_list.size() == 1)
		{
			Log.d(TAG, "listsize = "+pos_list.size());
			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					MotionEvent.ACTION_DOWN, 1, properties,  
					pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
		}
		else 
		{
			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					(MotionEvent.ACTION_POINTER_DOWN | ((pos_list.size()-1) << 8)), pos_list.size(), properties, pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
			Log.d(TAG, "-----------------------------the "+pos_list.size()+"th pos---------------------------");			
		}	

	}
	public static void sendMotionMoveByInstrumentation(Pos pos_in)
	{

		final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
		final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
		Log.d(TAG,"pointcount="+pos_list.size());
		if(pos_list.size() == 0)
		{
			pos_list_lock = false;
			return;
		}

		//Log.d("testdownandup", "add pos x ="+tx+"y="+ty );
		for(int i = 0; i <pos_list.size(); i++)
		{
			Pos pos  = pos_list.get(i);
			PointerProperties pp = new PointerProperties();   
			pp.id= i;  
			pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
			properties[i] = pp;  

			PointerCoords pc = new PointerCoords();  
			pc.x = pos.x;  
			pc.y = pos.y;  
			pc.pressure = 1;  
			pc.size = 1;  
			if(pos.tag == pos_in.tag)
			{
				pc.x = pos_in.x;  
				pc.y = pos_in.y;
				pos.x = pos_in.x;
				pos.y = pos_in.y;
				pos_list.set(i,pos);
			}
			pointerCoords[i] = pc;  
		}
		Log.d("testdownandup", "itroter list ok");
		//add by steven
		if(pos_list.size() == 1)

			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					MotionEvent.ACTION_MOVE, 1, properties,  
					pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
		else 
		{
			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					(MotionEvent.ACTION_MOVE | ((pos_list.size()-1) << 8)), pos_list.size(), properties, pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
			Log.d(TAG, "-----------------------------the "+pos_list.size()+"th pos moved---------------------------");			
		}

	}
	public static void sendMotionUpByInstrumentation(Pos pos_in) {


		final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
		final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
		int rm_index = 0;

		if(pos_list.size() == 0)
		{	
			pos_list_lock = false;
			return ;
		}
		Log.d(TAG, "del pos x ="+pos_in.x+"y="+pos_in.y );
		for(int i =0; i<pos_list.size(); i++)
		{
			Pos pos = pos_list.get(i);
			//	if(pos.x == gx && pos.y == gy)
			//	{
			//		
			//		Log.d(TAG,"remove x="+gx+ ", y="+gy);
			PointerProperties pp = new PointerProperties();
			//long downTime = SystemClock.uptimeMillis();  
			//long eventTime = SystemClock.uptimeMillis();     
			pp.id= i;  
			pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
			properties[i] = pp;  

			PointerCoords pc = new PointerCoords();  
			pc.x = pos.x;  
			pc.y = pos.y;  
			pc.pressure = 1;  
			pc.size = 1;  

			if(pos.x == pos_in.x && pos.y == pos_in.y) 
				rm_index = i;
			if(pos.tag == pos_in.tag)
			{
				pc.x = pos_in.x;
				pc.y = pos_in.y;
				rm_index = i;
			}

			pointerCoords[i] = pc;  
			Log.d(TAG, "tag = "+pos_in.tag );

			//break;
			//}
			//instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, tx, ty, 0));			
		}

		//add by steven
		Log.d(TAG,"remove x="+pos_in.x+ ", y="+pos_in.y);
		Log.d(TAG,"remove x="+pointerCoords[rm_index].x+", y="+pointerCoords[rm_index].y);

		PointerCoords temp =  pointerCoords[rm_index];
		pointerCoords[rm_index] = pointerCoords[pos_list.size()-1];
		pointerCoords[pos_list.size()-1] = temp;
		Log.d(TAG,"change ok");

		if(pos_list.size() == 1)
			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					MotionEvent.ACTION_UP, 1, properties,  
					pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
		else
			instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
					(MotionEvent.ACTION_POINTER_UP | ((pos_list.size()-1) << 8)), pos_list.size(), properties,  
					pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));

		pos_list.remove(rm_index);

	}
	public static void sendMotionMoveByInstrumentation(int intag,float x,float y)
	{
		final float tx = x;
		final float ty = y;
		final int tag  = intag;

		if(pos_list.size() == 0)
		{
			pos_list_lock = false;
			return;
		}


		new Thread("") {
			public void run() {

				while(pos_list_lock)
				{
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {	
						e.printStackTrace();			
					}
				}
				pos_list_lock = true;

				Log.d("testdownandup", "getlock");
				//pos_list.add(new Pos(tx,ty));
				final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
				final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
				Log.d(TAG,"pointcount="+pos_list.size());
				if(pos_list.size() == 0)
				{
					pos_list_lock = false;
					return;
				}

				//Log.d("testdownandup", "add pos x ="+tx+"y="+ty );
				for(int i = 0; i <pos_list.size(); i++)
				{
					Pos pos  = pos_list.get(i);
					PointerProperties pp = new PointerProperties();   
					pp.id= i;  
					pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
					properties[i] = pp;  

					PointerCoords pc = new PointerCoords();  
					pc.x = pos.x;  
					pc.y = pos.y;  
					pc.pressure = 1;  
					pc.size = 1;  
					if(pos.tag == tag)
					{
						pc.x = tx;  
						pc.y = ty;
						pos.x = tx;
						pos.y = ty;
						pos_list.set(i,pos);
					}
					pointerCoords[i] = pc;  
				}
				Log.d("testdownandup", "itroter list ok");
				//add by steven
				if(pos_list.size() == 1)

					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							MotionEvent.ACTION_MOVE, 1, properties,  
							pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
				else 
				{
					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							(MotionEvent.ACTION_MOVE | ((pos_list.size()-1) << 8)), pos_list.size(), properties, pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
					Log.d(TAG, "-----------------------------the "+pos_list.size()+"th pos moved---------------------------");			
				}
				pos_list_lock = false;
			}
			//add end
		}.start();
	}

	public static boolean sendMotionEvent(int action, int keyCode) {
		Log.e(TAG, "action = " + action + " keyCode = " + keyCode);
		if (sendMotionDown(keyCode)) {
			return true;
		}
		return false;
	}
	public static boolean sendMotionDown(int keyCode) {
		if (keyList == null || !gameStart) {
			return false;
		}
		for (int i = 0; i < keyList.size(); i++) {
			Log.e(TAG, "keyList i = " + i + " posx = " + keyList.get(i).posX + " posy = " + keyList.get(i).posY + " key = " + keyList.get(i).key);
			if (keyList.get(i).key == keyCode) {
				BlueoceanProfile mBlueOceanProfile = keyList.get(i);
				gx = mBlueOceanProfile.posX;
				gy = mBlueOceanProfile.posY + 25.0f;
				Log.d(TAG, "find a touch: key= " + keyList.get(i).key + " posX= " + keyList.get(i).posX + " posY= " + keyList.get(i).posY);
				//				BlueoceanMotionManager.injectMotionEventDown(BlueoceanMotionManager.fd, x, y);				
				//	sendMotionDownByInstrumentation(gx, gy);
				BlueoceanCore.motionQueue.offer(new Pos(gx,gy,MotionEvent.ACTION_DOWN));
				foundTouchKeyMap = true;
				return true;
			}
		}

		// test
		//		
		//		new Thread("") {
		//			public void run() {
		//				float x = 0;
		//				float y = 400;
		//				while(true) {
		////					BlueoceanMotionManager.injectMotionEventDown(BlueoceanMotionManager.fd, x, y);	
		////					BlueoceanMotionManager.injectMotionEventUp(BlueoceanMotionManager.fd);
		//					sendMotionDownByInstrumentation(x, y);
		//					sendMotionUpByInstrumentation(x, y);
		//					x++;
		//					if (x > 1300) x = 0;
		//					Log.e(TAG, " x = " + x + " y = " + y);
		//					try {
		//						Thread.sleep(50);
		//					} catch (InterruptedException e) {
		//						
		//					}
		//				}
		//				
		//			}
		//		}.start();
		foundTouchKeyMap = false;
		return false;
	}
	public static void sendKeyEventByInstrumentation(final KeyEvent keyEvent) {
		new Thread("") {
			public void run() {
				//				instrumentation.sendKeyDownUpSync(keyEvent.getKeyCode());
				Log.e(TAG, "send keycode = "+ keyEvent.getScanCode());
				instrumentation.sendKeySync(keyEvent);

				try {
					Thread.sleep(5);
				} catch (InterruptedException e){
					e.printStackTrace();
				}

			}
		}.start();
	}
	public static void sendMotionDownByInstrumentation(float x, float y) 
	{
		sendMotionDownByInstrumentation(0, x, y);
	}
	public static void sendMotionDownByInstrumentation(int intag, float x, float y) {
		final float tx  = x;
		final float ty = y;
		final int tag = intag;

		new Thread("") {
			public void run() {
				//		instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, tx, ty, 0));
				Log.d(TAG, "enter Motiondown");
				Log.d(TAG, "pos_list_lock="+pos_list_lock);
				// add by steven
				while(pos_list_lock)
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {	
						e.printStackTrace();			
					}
				}
				pos_list_lock = true;
				Log.d("testdownandup", "getlock");
				Pos pos_tmp = new Pos(tx,ty);
				pos_tmp.setTag(tag);
				pos_list.add(pos_tmp);
				final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
				final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
				Log.d(TAG,"pointcount="+pos_list.size());

				Log.d(TAG, "add pos x ="+tx+"y="+ty );
				for(int i = 0; i <pos_list.size(); i++)
				{
					Pos pos  = pos_list.get(i);
					PointerProperties pp = new PointerProperties();
					//long downTime = SystemClock.uptimeMillis();  
					//long eventTime = SystemClock.uptimeMillis();     
					pp.id= i;  
					pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
					properties[i] = pp;  

					PointerCoords pc = new PointerCoords();  
					pc.x = pos.x;  
					pc.y = pos.y;  
					pc.pressure = 1;  
					pc.size = 1;  
					pointerCoords[i] = pc;  
				}
				Log.d(TAG, "itroter list ok");
				//add by steven
				Log.d(TAG,"x ="+pointerCoords[0].x+",y="+pointerCoords[0].y);
				if(pos_list.size() == 1)
				{
					Log.d(TAG, "listsize = "+pos_list.size());
					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							MotionEvent.ACTION_DOWN, 1, properties,  
							pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
				}
				else 
				{
					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							(MotionEvent.ACTION_POINTER_DOWN | ((pos_list.size()-1) << 8)), pos_list.size(), properties, pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
					Log.d(TAG, "-----------------------------the "+pos_list.size()+"th pos---------------------------");			
				}	

				pos_list_lock = false;
				Log.d(TAG, "unlock");			
			}
			//add end
		}.start();
	}
	public static void sendMotionUpByInstrumentation(float x, float y) 
	{
		sendMotionUpByInstrumentation(0, x, y);
	}
	public static void sendMotionUpByInstrumentation(final int tag, float x, float y) {
		final float tx  = x;
		final float ty = y;

		// add end
		new Thread("") {
			public void run() {
				// add by steven
				while(pos_list_lock)
				{
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {	
						e.printStackTrace();			
					}
				}	
				Log.d(TAG,"pointcount="+pos_list.size());
				pos_list_lock = true;
				//Log.d("testdownandup", "getlock");
				final PointerProperties[] properties = new PointerProperties[pos_list.size()];  
				final PointerCoords[] pointerCoords = new PointerCoords[pos_list.size()];  
				int rm_index = 0;

				if(pos_list.size() == 0)
				{	
					pos_list_lock = false;
					return ;
				}
				Log.d(TAG, "del pos x ="+tx+"y="+ty );
				for(int i =0; i<pos_list.size(); i++)
				{
					Pos pos = pos_list.get(i);
					//	if(pos.x == gx && pos.y == gy)
					//	{
					//		
					//		Log.d(TAG,"remove x="+gx+ ", y="+gy);
					PointerProperties pp = new PointerProperties();
					//long downTime = SystemClock.uptimeMillis();  
					//long eventTime = SystemClock.uptimeMillis();     
					pp.id= i;  
					pp.toolType = MotionEvent.TOOL_TYPE_FINGER;  
					properties[i] = pp;  

					PointerCoords pc = new PointerCoords();  
					pc.x = pos.x;  
					pc.y = pos.y;  
					pc.pressure = 1;  
					pc.size = 1;  

					if(pos.x == tx && pos.y == ty) 
						rm_index = i;
					if(pos.tag == tag)
					{
						pc.x = tx;
						pc.y = ty;
						rm_index = i;
					}

					pointerCoords[i] = pc;  
					Log.d(TAG, "tag = "+tag );

					//break;
					//}
					//instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, tx, ty, 0));			
				}

				//add by steven
				Log.d(TAG,"remove x="+tx+ ", y="+ty);
				Log.d(TAG,"remove x="+pointerCoords[rm_index].x+", y="+pointerCoords[rm_index].y);

				PointerCoords temp =  pointerCoords[rm_index];
				pointerCoords[rm_index] = pointerCoords[pos_list.size()-1];
				pointerCoords[pos_list.size()-1] = temp;
				Log.d(TAG,"change ok");

				if(pos_list.size() == 1)
					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							MotionEvent.ACTION_UP, 1, properties,  
							pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));
				else
					instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
							(MotionEvent.ACTION_POINTER_UP | ((pos_list.size()-1) << 8)), pos_list.size(), properties,  
							pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0));

				pos_list.remove(rm_index);


				pos_list_lock = false;

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {	
				}
				//}


				//add end				
			}	
		}.start();
	}
	public static boolean sendMotionUp(int keyCode) {
		//		BlueoceanMotionManager.injectMotionEventUp(BlueoceanMotionManager.fd);
		float x;
		float y;

		for (int i = 0; i < keyList.size(); i++) {
			Log.e(TAG, "keyList i = " + i + " posx = " + keyList.get(i).posX + " posy = " + keyList.get(i).posY + " key = " + keyList.get(i).key);
			if (keyList.get(i).key == keyCode) {
				BlueoceanProfile mBlueOceanProfile = keyList.get(i);
				x = mBlueOceanProfile.posX;
				y = mBlueOceanProfile.posY + 25.0f;
				Log.d(TAG, "find a touch: key= " + keyList.get(i).key + " posX= " + keyList.get(i).posX + " posY= " + keyList.get(i).posY);
				BlueoceanCore.motionQueue.offer(new Pos(x, y, MotionEvent.ACTION_UP));
				//sendMotionUpByInstrumentation(x, y);
				Log.d(TAG,"touch key up ok");
				return true;
			}
		}
		return false;
		//sendMotionUpByInstrumentation(gx, gy);
		//Log.d(TAG,"touch key up ok");
	}
	private boolean enableIME(String IMEId) {
		Intent intent = new Intent();
		intent.setAction("COM.BLUEOCEAN_IME_SWITCH_IME");
		intent.putExtra("COM.BLUEOCEAN_IME_IMEID", IMEId);
		this.sendBroadcast(intent);
		return true;
	}
	public static void updateNotification(String info) {
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification mNotification = new Notification(R.drawable.icon, context.getString(R.string.app_name) + info, System.currentTimeMillis());
		Intent intent = new Intent(context.getApplicationContext(), BlueoceanIMEActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		mNotification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.app_name), mPendingIntent);
		mNotificationManager.notify(1, mNotification);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		context = this.getApplicationContext();
		//		context = this;
		mService = this;
		init();
		startCore();
		updateNotification("");
	}
}
