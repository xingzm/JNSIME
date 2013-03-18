package com.blueocean.ime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.TextView;

public class BlueoceanAdvanceActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "BlueoceanAdvanceActivity";
	private static final String[] degress = new String[]  {
		"90", "0", "270", "180",
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.acceleration);
        getControllerHandle();
        setControllerListener();
        setDefaultValue();
    }
	private void getControllerHandle() {
		BlueoceanPreferences.acceleration_dir_title = (ListPreference)this.findPreference(getString(R.string.acceleration_dir_title));
		BlueoceanPreferences.acceleration_sensitivity = (ListPreference)this.findPreference(getString(R.string.acceleration_sensitivity_title));
		BlueoceanPreferences.acceleration_simulation_enable = (CheckBoxPreference)this.findPreference(getString(R.string.acceleration_simulation_enable_title));
	}
	private void setControllerListener() {
		BlueoceanPreferences.acceleration_dir_title.setOnPreferenceClickListener(this);
		BlueoceanPreferences.acceleration_dir_title.setOnPreferenceChangeListener(this);
		BlueoceanPreferences.acceleration_sensitivity.setOnPreferenceClickListener(this);
		BlueoceanPreferences.acceleration_sensitivity.setOnPreferenceChangeListener(this);
	}
	private void setDefaultValue() {
		BlueoceanPreferences.acceleration_dir_title.setSummary(BlueoceanPreferences.acceleration_dir_title.getValue());
		BlueoceanPreferences.acceleration_sensitivity.setSummary(BlueoceanPreferences.acceleration_sensitivity.getValue());
	}
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg0.getKey().equals(getString(R.string.acceleration_dir_title))) {
			String summary = degress[Integer.parseInt(arg1.toString())] + getString(R.string.degree); 
			BlueoceanPreferences.acceleration_dir_title.setSummary(summary);
			SharedPreferences sp = getSharedPreferences("com.blueocean.ime_preferences", this.MODE_PRIVATE);
			Editor e = sp.edit();
			e.putString(arg0.getKey(), summary);
			e.commit();
			setAccelerationDirection(Integer.parseInt(arg1.toString()));
		} else if (arg0.getKey().equals(getString(R.string.acceleration_sensitivity_title))) {
			BlueoceanPreferences.acceleration_sensitivity.setSummary(arg1.toString() + "ms");
			SharedPreferences sp = getSharedPreferences("com.blueocean.ime_preferences", this.MODE_PRIVATE);
			Editor e = sp.edit();
			e.putString(arg0.getKey(), arg1.toString() + "ms");
			e.commit();
			setAccelerationSensitvity(Integer.parseInt(arg1.toString()));
		}
		return false;
	}
	private void setAccelerationDirection(int direction) {
		if (!BlueoceanCore.IMEEnabled) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage(R.string.not_open_acceleration_device_yet);
			b.setPositiveButton(R.string.sure, null);
			b.show();
		} else {
			if (!BlueoceanAccelerationManager.setInstallDirection(BlueoceanAccelerationManager.fd, direction)) {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setMessage(R.string.set_acceleration_direction_error);
				b.setPositiveButton(R.string.sure, null);
				b.show();
			}
			Log.e(TAG, "set acceleration install direction = " + direction);
		}
	}
	private void setAccelerationSensitvity(int delay) {
		if (!BlueoceanCore.IMEEnabled) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage(R.string.not_open_acceleration_device_yet);
			b.setPositiveButton(R.string.sure, null);
			b.show();
		} else {
			if (!BlueoceanAccelerationManager.setInstallDirection(BlueoceanAccelerationManager.fd, delay)) {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setMessage(R.string.set_acceleration_delay_error);
				b.setPositiveButton(R.string.sure, null);
				b.show();
			}
			Log.e(TAG, "set acceleration install sensitivity = " + delay);
		}
	}
	@Override
	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPreferenceClick");
		SharedPreferences sp = getSharedPreferences("com.blueocean.ime_preferences", this.MODE_PRIVATE);
		String value = sp.getString(arg0.getKey(), "");
		arg0.setDefaultValue(value);
		Log.e(TAG, " value1 = " + value);
		return true;
	}
}
