package com.blueocean.ime;
    
import com.blueocean.HID.BlueoceanHIDService;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.util.Log;

public class BlueoceanIMEActivity extends TabActivity {
    /** Called when the activity is first created. */
	private static final String TAG = "BlueoceanIMEActivity";
	private TabHost mTabHost;
	private LinearLayout ll; 
	private TabWidget tw;
	public static BlueoceanIMEActivity mBlueoceanIMEActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mBlueoceanIMEActivity = this;
        createTab();
        BlueoceanCore.BlueoceanIMEActivityShowing = true;
		Intent intent = new Intent("com.blueocean.ime.blueoceancore");
		this.startService(intent);
		intent = new Intent(BlueoceanHIDService.ACTION_SERVICE);
		this.startService(intent);
	
		if (this.getIntent().getExtras() != null) {
			Log.e(TAG, "start_mode = true");
			this.finish();
		} else {
			Log.e(TAG, "start_mode = false");
		}
    } 
    
    private void createTab() {
    	mTabHost = getTabHost();
    	ll = (LinearLayout)mTabHost.getChildAt(0);
    	tw = (TabWidget)ll.getChildAt(0);

    	RelativeLayout tabIndicator1 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab = (TextView)tabIndicator1.findViewById(R.id.title);
    	tvTab.setText(getResources().getString(R.string.controller));
//    	ImageView iconImage = (ImageView)tabIndicator1.findViewById(R.id.icon);
//    	iconImage.setBackgroundResource(R.drawable.selected);
//    	
    	RelativeLayout tabIndicator2 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab1 = (TextView)tabIndicator2.findViewById(R.id.title);
    	tvTab1.setText(getResources().getString(R.string.tpconfig));
//    	ImageView iconImage1 = (ImageView)tabIndicator2.findViewById(R.id.icon);
//    	iconImage1.setBackgroundResource(R.drawable.unselected);
//    	
    	RelativeLayout tabIndicator3 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab2 = (TextView)tabIndicator3.findViewById(R.id.title);
    	tvTab2.setText(getResources().getString(R.string.game));
//    	ImageView iconImage2 = (ImageView)tabIndicator3.findViewById(R.id.icon);
//    	iconImage2.setBackgroundResource(R.drawable.unselected);
//    	
    	RelativeLayout tabIndicator4 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
    	TextView tvTab3 = (TextView)tabIndicator4.findViewById(R.id.title);
    	tvTab3.setText(getResources().getString(R.string.advance));
//    	ImageView iconImage3 = (ImageView)tabIndicator4.findViewById(R.id.icon);
//    	iconImage3.setBackgroundResource(R.drawable.unselected);
    	
    	Intent intent = new Intent();
    	intent.setClass(this, BlueoceanControllerActivity.class);
    	TabHost.TabSpec spec = mTabHost.newTabSpec(getResources().getString(R.string.controller)).setIndicator(tabIndicator1).setContent(intent);
    	mTabHost.addTab(spec);
    	
    	intent = new Intent();
    	intent.setClass(this, BlueoceanTPActivity.class);
    	spec = mTabHost.newTabSpec(getResources().getString(R.string.tpconfig)).setIndicator(tabIndicator2).setContent(intent);
    	mTabHost.addTab(spec);
    	
    	intent = new Intent();
    	intent.setClass(this, BlueoceanSettingsActivity.class);
    	spec = mTabHost.newTabSpec(getResources().getString(R.string.settings)).setIndicator(tabIndicator3).setContent(intent);
    	mTabHost.addTab(spec);
    	
    	intent = new Intent();
    	intent.setClass(this, BlueoceanAdvanceActivity.class);
    	spec = mTabHost.newTabSpec(getResources().getString(R.string.advance)).setIndicator(tabIndicator4).setContent(intent);
    	mTabHost.addTab(spec);
    	
    	mTabHost.setCurrentTab(0); 
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.e(TAG, "onDestroy");
    	BlueoceanCore.BlueoceanIMEActivityShowing = false;
    }
}
