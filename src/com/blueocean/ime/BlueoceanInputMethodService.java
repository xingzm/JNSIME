package com.blueocean.ime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class BlueoceanInputMethodService extends InputMethodService {
	private static final String TAG = "BlueOceanInputMethodService";
	private Context mContext = null;
	private boolean debug = true;
	private long perDealZoomTime = 0;

	@Override public void onCreate() {
		Log.e(TAG, "oncreate");
		BlueoceanCore.blueOceanInputMethodService = this;
		//		BlueOceanConfig.localInputMethodId = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
		super.onCreate();
	}

	@Override public void onInitializeInterface() {
		Log.e(TAG, "onInitializeInterface");
		super.onInitializeInterface();
	}

	@Override public View onCreateInputView() {
		Log.e(TAG, "onCreateInputView");
		return super.onCreateInputView();
	}

	@Override public View onCreateCandidatesView() {
		Log.e(TAG, "onCreateCandidatesView");
		return super.onCreateCandidatesView();
	}

	@Override public void onStartInput(EditorInfo attribute, boolean restarting) {
		Log.e(TAG, "onStartInput");
		super.onStartInput(attribute, restarting);
	}

	@Override public void onFinishInput() {
		Log.e(TAG, "onFinishInput");
		super.onFinishInput();
	}

	@Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
		Log.e(TAG, "onStartInputView");
		super.onStartInputView(attribute, restarting);
	}

	@Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd,
			int candidatesStart, int candidatesEnd) {
		Log.e(TAG, "onUpdateSelection");
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
	}

	@Override public void onDisplayCompletions(CompletionInfo[] completions) {
		Log.e(TAG, "onDisplayCompletions");
		super.onDisplayCompletions(completions);
	}
	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Log.e(TAG, "event.islongpress = " + event.isLongPress() + " event.getrepeatcount = " + event.getRepeatCount());
		//Log.e(TAG, "touchconfig = " + BlueoceanCore.touchConfiging + " keycode = " + keyCode);
		Log.e(TAG, "key scancode = " + event.getScanCode());
		if(419 == event.getScanCode())
		{
			if(BlueoceanCore.zoom_stat)
				return true;
			BlueoceanCore.zoom_stat = true;
			BlueoceanCore.sendMotionZoomOut();
			return true;
		}
		if(418 == event.getScanCode())
		{	
			if(BlueoceanCore.zoom_stat)
				return true;
			BlueoceanCore.zoom_stat = true;
			BlueoceanCore.sendMotionZoomIn();
			return true;
		}
		if (BlueoceanCore.KEYCODE_START_IME == event.getScanCode() || BlueoceanCore.KEYCODE_CLOSE_IME == event.getScanCode()) return true;
		if (!BlueoceanCore.touchConfiging && keyCode == BlueoceanCore.KEYCODE_START_TPCONFIG) {
			Intent intent = new Intent();
			intent.setClass(this.getApplicationContext(), BlueoceanTpConfigActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
			return true;	
		} 
		//		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
		//			if (BlueoceanAccelerationProcess.processAccelerationData(this.getApplicationContext(),keyCode, event)) return true;
		//		}
		if (BlueoceanCore.keyMaping && keyCode != KeyEvent.KEYCODE_MENU && keyCode != BlueoceanCore.KEYCODE_START_TPCONFIG
				&& keyCode != KeyEvent.KEYCODE_BACK) {
			return true;
		} else if (!BlueoceanCore.keyMaping && keyCode != KeyEvent.KEYCODE_MENU  && keyCode != BlueoceanCore.KEYCODE_START_TPCONFIG
				&& keyCode != KeyEvent.KEYCODE_BACK && !BlueoceanCore.touchConfiging) {
			if (BlueoceanCore.sendMotionDown(keyCode)) {
				Log.e(TAG, "keydown sendMotionEvent ok");
				return true;
			}
			if (BlueoceanCore.sendKeyMap(this.getApplicationContext(), KeyEvent.ACTION_DOWN, keyCode)) {
				Log.e(TAG, "sendKeyMap ok");
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e(TAG, "on key up");
		if (BlueoceanCore.KEYCODE_START_IME == event.getScanCode() || BlueoceanCore.KEYCODE_CLOSE_IME == event.getScanCode()) return true;
		if (BlueoceanCore.keyMaping && keyCode != KeyEvent.KEYCODE_MENU && keyCode != BlueoceanCore.KEYCODE_START_TPCONFIG) {
			//&& keyCode != KeyEvent.KEYCODE_BACK) {
			BlueoceanCore.sharedPreferencesConfig(this.getApplicationContext(), keyCode);
			if (keyCode == KeyEvent.KEYCODE_BACK) return super.onKeyUp(keyCode, event);
			return true;
		} else if (!BlueoceanCore.keyMaping && keyCode != KeyEvent.KEYCODE_MENU && keyCode != BlueoceanCore.KEYCODE_START_TPCONFIG
				&& keyCode != KeyEvent.KEYCODE_BACK && !BlueoceanCore.touchConfiging) {
			Log.e(TAG, "key is normal ok");
			//	if (BlueoceanCore.foundTouchKeyMap) {
			Log.e(TAG, "keyup sendMotionEvent ok");
			//	BlueoceanCore.sendMotionUp(keyCode);
			if(BlueoceanCore.sendMotionUp(keyCode))			
				return true;
			//	}
			if (BlueoceanCore.sendKeyMap(this.getApplicationContext(), KeyEvent.ACTION_UP, keyCode)) {
				Log.e(TAG, "keyup sendKeyMap ok");
				return true;
			} 
		}
		return super.onKeyUp(keyCode, event);
	}

}
