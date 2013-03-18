package com.blueocean.ime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class BlueoceanTPActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "BlueoceanTPActivity";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.tpconfig);
        getControllerHandler();
        setControllerListener();
        loadFiles();
        initPreference();
    }
	private void getControllerHandler() {
		BlueoceanPreferences.listFiles = (ListPreference)findPreference(getResources().getString(R.string.list_load_files_key));
        BlueoceanPreferences.listDelFiles = (ListPreference)findPreference(getResources().getString(R.string.list_del_touch_file));
	}
	private void setControllerListener() {
		BlueoceanPreferences.listFiles.setOnPreferenceChangeListener(this);
//		BlueoceanPreferences.listFiles.setOnPreferenceClickListener(this);
		BlueoceanPreferences.listDelFiles.setOnPreferenceChangeListener(this);
	}
	private void  initPreference() {
		SharedPreferences sp = this.getSharedPreferences("com.blueocean.ime_preferences", Context.MODE_PRIVATE);
		String fileName = sp.getString(BlueoceanPreferences.listFiles.getKey(), "null");
		if (!fileName.equals("null")) {
			loadFile(fileName);
			BlueoceanPreferences.listFiles.setSummary(fileName);
		}
	}
	@Override
	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg0.getKey().equals(BlueoceanPreferences.listFiles.getKey())) {
			Log.e(TAG, "select item text = " + arg1);
			SharedPreferences sp = this.getSharedPreferences("com.blueocean.ime_preferences", Context.MODE_PRIVATE);
			Editor e = sp.edit();
			if (arg1.toString().equals("None")) {
				BlueoceanCore.keyList = null;
				e.putString(BlueoceanPreferences.listFiles.getKey(),  arg1.toString());
				e.commit();
				BlueoceanPreferences.listFiles.setSummary(arg1.toString());
				return false;
			}
			loadFile(arg1.toString());
			BlueoceanPreferences.listFiles.setSummary(arg1.toString());
			e.putString(BlueoceanPreferences.listFiles.getKey(), arg1.toString());
			e.commit();
		} else if (arg0.getKey().equals(BlueoceanPreferences.listDelFiles.getKey())) {
			File f = new File(this.getFilesDir().toString() + "/" + arg1.toString());
			if (f.isFile() && f.exists()) 
				if (f.delete()) BlueoceanPreferences.listDelFiles.setSummary(getResources().getString(R.string.removed_file) + " " + arg1.toString());
			loadFiles();
		}
		return false;
	}
	private void loadFiles() {
		File f = new File(getFilesDir().toString());
		File files[] = f.listFiles();
		List<String> listFileEntres = new ArrayList<String>();
		if (files.length > 0) listFileEntres.add("None");
		for (int i = 0; i < files.length; i ++) {
			listFileEntres.add(files[i].getName());
		}
		BlueoceanPreferences.listFiles.setEntries(listFileEntres.toArray(new CharSequence[listFileEntres.size()]));
		BlueoceanPreferences.listFiles.setEntryValues(listFileEntres.toArray(new CharSequence[listFileEntres.size()]));
		BlueoceanPreferences.listDelFiles.setEntries(listFileEntres.toArray(new CharSequence[listFileEntres.size()]));
		BlueoceanPreferences.listDelFiles.setEntryValues(listFileEntres.toArray(new CharSequence[listFileEntres.size()]));
	}
	private void loadFile(String arg1) {
		FileReader fr;
		if (BlueoceanCore.keyList == null) BlueoceanCore.keyList = new ArrayList<BlueoceanProfile>();
		if (BlueoceanCore.keyList.size() > 0)  BlueoceanCore.keyList.clear();
		try {
			fr = new FileReader(this.getFilesDir() + "/" + arg1.toString());
			BufferedReader br = new BufferedReader(fr);
//			BlueoceanCore.keyList = new ArrayList<BlueOceanProfile>();
			String val = br.readLine();
			while (null != val) {
				Log.e(TAG, "read val = " + val);
				if (val.equals('\n')) val = br.readLine();
				BlueoceanProfile bp = new BlueoceanProfile();
				if (val != null && !val.equals("")) {
					bp.key = Integer.valueOf(val);	
				}
				val = br.readLine();
				if ( val != null && !val.equals("")) {
					bp.posX = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posY = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posR = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posType = Float.valueOf(val);
				}
//				listConfig.add(bp);
				BlueoceanCore.keyList.add(bp);
				Log.e(TAG, "load file add BlueOceanProfile key= " + bp.key + " posx= " + bp.posX + " posy= " + bp.posY);
				val = br.readLine();
				BlueoceanCore.gameStart = true;
			}
//			BlueOceanConfig.keyList = listConfig;
//			BlueOceanConfig.gameStart = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
