package com.blueocean.ime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.preference.Preference;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class BlueoceanInputKeyTip extends Dialog {
	private static final String TAG = "BlueoceanInputKeyTip";
	public Preference currentPreference;
	private Context context;
	public static Context mContext;
	
	public BlueoceanInputKeyTip(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.setTitle(context.getResources().getString(R.string.str_input_key_tip));
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
}
