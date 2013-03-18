package com.blueocean.ime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

public class BlueoceanControllerActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
	public static final String TAG = "BlueoceanControllerActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       addPreferencesFromResource(R.xml.controller);
       getControllerHandle();
       setControllerListener();
       startBlueoceanCoreService();
       BlueoceanCore.context = this;
    }
	private void startBlueoceanCoreService() {
		BlueoceanCore.context = this;
		Intent intent = new Intent("com.blueocean.ime.blueoceancore");
		this.startService(intent);
	}
	private void getControllerHandle() {
		BlueoceanPreferences.mKeyMappingPreference = (PreferenceScreen)findPreference(getResources().getString(R.string.controller_1));
		BlueoceanPreferences.listDpadCenter = (Preference)findPreference(getResources().getString(R.string.dpad_center));
		BlueoceanPreferences.listDpadCenterKey = BlueoceanPreferences.listDpadCenter.getKey();
		BlueoceanPreferences.listDpadLeft = (Preference)findPreference(getResources().getString(R.string.dpad_left));
		BlueoceanPreferences.listDpadLeftKey = BlueoceanPreferences.listDpadLeft.getKey();
		BlueoceanPreferences.listDpadRight = (Preference)findPreference(getResources().getString(R.string.dpad_right));
		BlueoceanPreferences.listDpadRightKey = BlueoceanPreferences.listDpadRight.getKey();
		BlueoceanPreferences.listDpadDown = (Preference)findPreference(getResources().getString(R.string.dpad_down));
		BlueoceanPreferences.listDpadDownKey = BlueoceanPreferences.listDpadDown.getKey();
		BlueoceanPreferences.listDpadUp = (Preference)findPreference(getResources().getString(R.string.dpad_up));
		BlueoceanPreferences.listDpadUpKey = BlueoceanPreferences.listDpadUp.getKey();
		BlueoceanPreferences.listButtonA = (Preference)findPreference(getResources().getString(R.string.button_a));
		BlueoceanPreferences.listButtonAKey = BlueoceanPreferences.listButtonA.getKey();
		BlueoceanPreferences.listButtonB = (Preference)findPreference(getResources().getString(R.string.button_b));
		BlueoceanPreferences.listButtonBKey = BlueoceanPreferences.listButtonB.getKey();
		BlueoceanPreferences.listButtonC = (Preference)findPreference(getResources().getString(R.string.button_c));
		BlueoceanPreferences.listButtonCKey = BlueoceanPreferences.listButtonC.getKey();
		BlueoceanPreferences.listButtonX =  (Preference)findPreference(getResources().getString(R.string.button_x));
		BlueoceanPreferences.listButtonXKey = BlueoceanPreferences.listButtonX.getKey();
		BlueoceanPreferences.listButtonY = (Preference)findPreference(getResources().getString(R.string.button_y));
		BlueoceanPreferences.listButtonYKey = BlueoceanPreferences.listButtonY.getKey();
		BlueoceanPreferences.listButtonZ = (Preference)findPreference(getResources().getString(R.string.button_z));
		BlueoceanPreferences.listButtonZKey = BlueoceanPreferences.listButtonZ.getKey();
		BlueoceanPreferences.listButtonL1 = (Preference)findPreference(getResources().getString(R.string.button_l1));
		BlueoceanPreferences.listButtonL1Key = BlueoceanPreferences.listButtonL1.getKey();
		BlueoceanPreferences.listButtonR1 = (Preference)findPreference(getResources().getString(R.string.button_r1));
		BlueoceanPreferences.listButtonR1Key = BlueoceanPreferences.listButtonR1.getKey();
		BlueoceanPreferences.listButtonL2 = (Preference)findPreference(getResources().getString(R.string.button_l2));
		BlueoceanPreferences.listButtonL2Key = BlueoceanPreferences.listButtonL2.getKey();
		BlueoceanPreferences.listButtonR2 = (Preference)findPreference(getResources().getString(R.string.button_r2));
		BlueoceanPreferences.listButtonR2Key = BlueoceanPreferences.listButtonR2.getKey();
		BlueoceanPreferences.listButtonThumbl = (Preference)findPreference(getResources().getString(R.string.button_thumbl));
		BlueoceanPreferences.listButtonThumblKey = BlueoceanPreferences.listButtonThumbl.getKey();
		BlueoceanPreferences.listButtonThumbr = (Preference)findPreference(getResources().getString(R.string.button_thumbr));
		BlueoceanPreferences.listButtonThumbrKey = BlueoceanPreferences.listButtonThumbr.getKey();
		BlueoceanPreferences.listButtonStart = (Preference)findPreference(getResources().getString(R.string.button_start));
		BlueoceanPreferences.listButtonStartKey = BlueoceanPreferences.listButtonStart.getKey();
		BlueoceanPreferences.listButtonSelect = (Preference)findPreference(getResources().getString(R.string.button_select));
		BlueoceanPreferences.listButtonSelectKey = BlueoceanPreferences.listButtonSelect.getKey();
		BlueoceanPreferences.listButtonMode = (Preference)findPreference(getResources().getString(R.string.button_mode));
		BlueoceanPreferences.listButtonModeKey = BlueoceanPreferences.listButtonMode.getKey();
		BlueoceanPreferences.listButton1 = (Preference)findPreference(getResources().getString(R.string.button_1));
		BlueoceanPreferences.listButton1Key = BlueoceanPreferences.listButton1.getKey();
		BlueoceanPreferences.listButton2 = (Preference)findPreference(getResources().getString(R.string.button_2));
		BlueoceanPreferences.listButton2Key = BlueoceanPreferences.listButton2.getKey();
		BlueoceanPreferences.listButton3 = (Preference)findPreference(getResources().getString(R.string.button_3));
		BlueoceanPreferences.listButton3Key = BlueoceanPreferences.listButton3.getKey();
		BlueoceanPreferences.listButton4 = (Preference)findPreference(getResources().getString(R.string.button_4));
		BlueoceanPreferences.listButton4Key = BlueoceanPreferences.listButton4.getKey();
		BlueoceanPreferences.listButton5 = (Preference)findPreference(getResources().getString(R.string.button_5));
		BlueoceanPreferences.listButton5Key = BlueoceanPreferences.listButton5.getKey();
		BlueoceanPreferences.listButton6 = (Preference)findPreference(getResources().getString(R.string.button_6));
		BlueoceanPreferences.listButton6Key = BlueoceanPreferences.listButton6.getKey();
		BlueoceanPreferences.listButton7 = (Preference)findPreference(getResources().getString(R.string.button_7));
		BlueoceanPreferences.listButton7Key = BlueoceanPreferences.listButton7.getKey();
		BlueoceanPreferences.listButton8 = (Preference)findPreference(getResources().getString(R.string.button_8));
		BlueoceanPreferences.listButton8Key = BlueoceanPreferences.listButton8.getKey();
		BlueoceanPreferences.listButton9 = (Preference)findPreference(getResources().getString(R.string.button_9));
		BlueoceanPreferences.listButton9Key = BlueoceanPreferences.listButton9.getKey();
		BlueoceanPreferences.listButton10 = (Preference)findPreference(getResources().getString(R.string.button_10));
		BlueoceanPreferences.listButton10Key = BlueoceanPreferences.listButton10.getKey();
		BlueoceanPreferences.listButton11 = (Preference)findPreference(getResources().getString(R.string.button_11));
		BlueoceanPreferences.listButton11Key = BlueoceanPreferences.listButton11.getKey();
		BlueoceanPreferences.listButton12 = (Preference)findPreference(getResources().getString(R.string.button_12));
		BlueoceanPreferences.listButton12Key = BlueoceanPreferences.listButton12.getKey();
		BlueoceanPreferences.listButton13 = (Preference)findPreference(getResources().getString(R.string.button_13));
		BlueoceanPreferences.listButton13Key = BlueoceanPreferences.listButton13.getKey();
		BlueoceanPreferences.listButton14 = (Preference)findPreference(getResources().getString(R.string.button_14));
		BlueoceanPreferences.listButton14Key = BlueoceanPreferences.listButton14.getKey();
		BlueoceanPreferences.listButton15 = (Preference)findPreference(getResources().getString(R.string.button_15));
		BlueoceanPreferences.listButton15Key = BlueoceanPreferences.listButton15.getKey();
		BlueoceanPreferences.listButton16 = (Preference)findPreference(getResources().getString(R.string.button_16));
		BlueoceanPreferences.listButton16Key = BlueoceanPreferences.listButton16.getKey();
		BlueoceanPreferences.listA = (Preference)this.findPreference(getResources().getString(R.string.a));
		BlueoceanPreferences.listAKey = BlueoceanPreferences.listA.getKey();
		BlueoceanPreferences.listB = (Preference)this.findPreference(getResources().getString(R.string.b));
		BlueoceanPreferences.listBKey = BlueoceanPreferences.listB.getKey();
		BlueoceanPreferences.listC = (Preference)this.findPreference(getResources().getString(R.string.c));
		BlueoceanPreferences.listCKey = BlueoceanPreferences.listC.getKey();
		BlueoceanPreferences.listD = (Preference)this.findPreference(getResources().getString(R.string.d));
		BlueoceanPreferences.listDKey = BlueoceanPreferences.listD.getKey();
		BlueoceanPreferences.listE = (Preference)this.findPreference(getResources().getString(R.string.e));
		BlueoceanPreferences.listEKey = BlueoceanPreferences.listE.getKey();
		BlueoceanPreferences.listF = (Preference)this.findPreference(getResources().getString(R.string.f));
		BlueoceanPreferences.listFKey = BlueoceanPreferences.listF.getKey();
		BlueoceanPreferences.listG = (Preference)this.findPreference(getResources().getString(R.string.g));
		BlueoceanPreferences.listGKey = BlueoceanPreferences.listG.getKey();
		BlueoceanPreferences.listH = (Preference)this.findPreference(getResources().getString(R.string.h));
		BlueoceanPreferences.listHKey = BlueoceanPreferences.listH.getKey();
		BlueoceanPreferences.listI = (Preference)this.findPreference(getResources().getString(R.string.i));
		BlueoceanPreferences.listIKey = BlueoceanPreferences.listI.getKey();
		BlueoceanPreferences.listJ = (Preference)this.findPreference(getResources().getString(R.string.j));
		BlueoceanPreferences.listJKey = BlueoceanPreferences.listJ.getKey();
		BlueoceanPreferences.listK = (Preference)this.findPreference(getResources().getString(R.string.k));
		BlueoceanPreferences.listKKey = BlueoceanPreferences.listK.getKey();
		BlueoceanPreferences.listL = (Preference)this.findPreference(getResources().getString(R.string.l));
		BlueoceanPreferences.listLKey = BlueoceanPreferences.listL.getKey();
		BlueoceanPreferences.listM = (Preference)this.findPreference(getResources().getString(R.string.m));
		BlueoceanPreferences.listMKey = BlueoceanPreferences.listM.getKey();
		BlueoceanPreferences.listN = (Preference)this.findPreference(getResources().getString(R.string.n));
		BlueoceanPreferences.listNKey = BlueoceanPreferences.listN.getKey();
		BlueoceanPreferences.listO = (Preference)this.findPreference(getResources().getString(R.string.o));
		BlueoceanPreferences.listOKey = BlueoceanPreferences.listO.getKey();
		BlueoceanPreferences.listP = (Preference)this.findPreference(getResources().getString(R.string.p));
		BlueoceanPreferences.listPKey = BlueoceanPreferences.listP.getKey();
		BlueoceanPreferences.listQ = (Preference)this.findPreference(getResources().getString(R.string.q));
		BlueoceanPreferences.listQKey = BlueoceanPreferences.listQ.getKey();
		BlueoceanPreferences.listR = (Preference)this.findPreference(getResources().getString(R.string.r));
		BlueoceanPreferences.listRKey = BlueoceanPreferences.listR.getKey();
		BlueoceanPreferences.listS = (Preference)this.findPreference(getResources().getString(R.string.s));
		BlueoceanPreferences.listSKey = BlueoceanPreferences.listS.getKey();
		BlueoceanPreferences.listT = (Preference)this.findPreference(getResources().getString(R.string.t));
		BlueoceanPreferences.listTKey = BlueoceanPreferences.listT.getKey();
		BlueoceanPreferences.listU = (Preference)this.findPreference(getResources().getString(R.string.u));
		BlueoceanPreferences.listUKey = BlueoceanPreferences.listU.getKey();
		BlueoceanPreferences.listV = (Preference)this.findPreference(getResources().getString(R.string.v));
		BlueoceanPreferences.listVKey = BlueoceanPreferences.listV.getKey();
		BlueoceanPreferences.listW = (Preference)this.findPreference(getResources().getString(R.string.w));
		BlueoceanPreferences.listWKey = BlueoceanPreferences.listW.getKey();
		BlueoceanPreferences.listX = (Preference)this.findPreference(getResources().getString(R.string.x));
		BlueoceanPreferences.listXKey = BlueoceanPreferences.listX.getKey();
		BlueoceanPreferences.listY = (Preference)this.findPreference(getResources().getString(R.string.y));
		BlueoceanPreferences.listYKey = BlueoceanPreferences.listY.getKey();
		BlueoceanPreferences.listZ = (Preference)this.findPreference(getResources().getString(R.string.z));
		BlueoceanPreferences.listZKey = BlueoceanPreferences.listZ.getKey();
		//add by steven		
		BlueoceanPreferences.mClearKeyMap = (Preference)this.findPreference(getResources().getString(R.string.clear));
		//add end		
		Log.e(TAG, "Preferences init finished");
	}
	
	private void setControllerListener() {
		//add by steven
		BlueoceanPreferences.mClearKeyMap.setOnPreferenceClickListener(this);
		//add end
		BlueoceanPreferences.mKeyMappingPreference.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDpadCenter.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listDpadCenter.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDpadLeft.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listDpadLeft.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDpadRight.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listDpadRight.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDpadDown.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listDpadDown.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDpadUp.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listDpadUp.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonA.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonA.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonB.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonB.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonC.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonC.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonX.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonX.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonY.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonY.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonZ.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonZ.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonL1.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonL1.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonR1.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonR1.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonL2.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonL2.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonR2.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonR2.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonThumbl.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonThumbl.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonThumbr.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonThumbr.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonStart.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonStart.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonSelect.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonSelect.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButtonMode.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButtonMode.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton1.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton1.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton2.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton2.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton3.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton3.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton4.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton4.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton5.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton5.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton6.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton6.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton7.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton7.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton8.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton8.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton9.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton9.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton10.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton10.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton11.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton11.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton12.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton12.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton13.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton13.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton14.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton14.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton15.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton15.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listButton16.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listButton16.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listA.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listA.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listB.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listB.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listC.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listC.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listD.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listD.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listE.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listE.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listF.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listF.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listG.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listG.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listH.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listH.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listI.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listI.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listK.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listK.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listL.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listL.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listM.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listM.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listN.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listN.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listO.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listO.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listP.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listP.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listQ.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listQ.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listR.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listR.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listS.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listS.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listT.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listT.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listU.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listU.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listV.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listV.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listW.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listW.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listX.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listX.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listY.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listY.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listZ.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.listZ.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	private void ResetKeyMap()
	{
		SharedPreferences sp = this.getSharedPreferences("com.blueocean.ime_preferences", Context.MODE_PRIVATE);
		Editor e = sp.edit();
		e.clear();
		e.commit();
			
	}
	@Override
	public boolean onPreferenceClick(Preference arg0) {
		if(arg0.equals(BlueoceanPreferences.mClearKeyMap))
		{
			ResetKeyMap();
			Toast.makeText(this, R.string.clear_success, Toast.LENGTH_SHORT).show();
				return true;
		}		
		// TODO Auto-generated method stub
		if (arg0.getKey().equals(BlueoceanPreferences.mKeyMappingPreference.getKey())) BlueoceanCore.setButtonSummary();
		else {
			BlueoceanCore.keyMaping = true;
			BlueoceanCore.currentPreference = arg0;
			BlueoceanCore.mBlueoceanInputKeyTip = new BlueoceanInputKeyTip(this, 0);
			BlueoceanCore.mBlueoceanInputKeyTip.show();
		}
		return false;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		BlueoceanCore.context = null;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
//		Log.e(TAG, " onGenericMotionEvent x = " + event.getX() + " y = " + event.getY() + "  scancode = " + event.getSource());
		return super.onGenericMotionEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e(TAG, " onTouchEvent x = " + event.getX() + " y = " + event.getY() + "  scancode = " + event.getSource());
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, " onkeydown keycode = " + keyCode + " scancode = " + event.getScanCode());
		return super.onKeyDown(keyCode, event);
	}
}
