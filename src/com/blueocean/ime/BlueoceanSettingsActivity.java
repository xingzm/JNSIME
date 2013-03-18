package com.blueocean.ime;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class BlueoceanSettingsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("Settings");
        setContentView(view);
    }
}
