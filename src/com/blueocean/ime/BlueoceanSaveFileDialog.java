package com.blueocean.ime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BlueoceanSaveFileDialog extends Dialog implements OnTouchListener, android.view.View.OnClickListener {
	private static final String TAG = "BlueOceanSaveFileDialog";
	private EditText mFileNameEditText;
	private Button sureBtn;
	private Button noBtn;
	private Context mContext;
	private TextView infoTv;
	public boolean saved = false;
	public Activity activity;
	
	public BlueoceanSaveFileDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub	
		mContext = context;
		setContentView(R.layout.input_file_name);
		mFileNameEditText = (EditText)findViewById(R.id.fileNameText01);
		mFileNameEditText.setOnTouchListener(this);
		sureBtn = (Button)findViewById(R.id.sureButton01);
		sureBtn.setOnClickListener(this);
		noBtn = (Button)findViewById(R.id.noButton01);
		noBtn.setOnClickListener(this);
		infoTv = (TextView)findViewById(R.id.infoText01);
		this.setTitle(R.string.save_file_title);
		saved = false;
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
//		BlueOceanConfig.mFileNameEditText = mFileNameEditText;
		return false;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.sureButton01:
			if (mFileNameEditText.getText().equals("")) {
					mFileNameEditText.setText("Input Text Tks");
					saved = true;
					return;
			}
			saveFile(mFileNameEditText.getText().toString());
			saved = true;
			BlueoceanCore.gameStart = true;
			activity.finish();
			break;
		case R.id.noButton01:
			this.cancel();
			saved = true;
			break;
		}
	}
	private void saveFile(String path) {
		try {
			FileOutputStream fos = mContext.openFileOutput(path, Context.MODE_PRIVATE);
			if (BlueoceanCore.keyList != null) {
				for (int i = 0; i < BlueoceanCore.keyList.size(); i ++) {
					fos.write(String.valueOf(BlueoceanCore.keyList.get(i).key).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(BlueoceanCore.keyList.get(i).posX).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(BlueoceanCore.keyList.get(i).posY).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(BlueoceanCore.keyList.get(i).posR).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(BlueoceanCore.keyList.get(i).posType).getBytes());
					fos.write("\n".getBytes());
				}
			}
			saved = true;
			fos.close();
			cancel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			infoTv.setText(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
