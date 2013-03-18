package com.blueocean.ime;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class BlueoceanTpConfigActivity extends Activity implements OnTouchListener, OnClickListener {
	private static final String TAG = "BlueoceanTpConfigActivity";
	public BlueoceanScreenView screenView = null;
	private int backKeyCount = 0;
	private boolean touched = false;
	private int oldKey;
	public List<BlueoceanProfile> keyList;
	private BlueoceanSaveFileDialog saveFileDialog;
	private boolean noTouchData = true;
	private boolean debug = true;
	private BlueoceanPosition bop;
	private float touchX = 0.0f;
	private float touchY = 0.0f;
	private float touchR = 0.0f;
	private boolean hasLeftJoystick = false;
	private boolean hasRightJoystick = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.input_key_tip);
		screenView = (BlueoceanScreenView)findViewById(R.id.screenView01);
		screenView.setOnTouchListener(this);
		screenView.setOnClickListener(this);
		keyList = new ArrayList<BlueoceanProfile>();
		saveFileDialog = new BlueoceanSaveFileDialog(this);
		saveFileDialog.activity = this;
		saveFileDialog.saved = false;
		BlueoceanCore.touchConfiging = true;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		BlueoceanCore.touchConfiging = false;
	}
	@Override
	public void onStop() {
		BlueoceanCore.touchConfiging = false;
		super.onStop();
		this.finish();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
//		Log.e(TAG, "onClick ScrollX = " + arg0.geL + " ScrollY = " + arg0.getScrollY());
	}
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onTouch x = " + arg1.getX() + " y= " + arg1.getY() + " action = " + arg1.getAction());
		
//		bop = new BlueoceanPosition();
//		//bop.x = arg1.getX();
//		//bop.y = arg1.getY();
//		bop.x = arg1.getRawX();
//		bop.y = arg1.getRawY();
//		bop.msg = "";
//		screenView.drawCircle(arg1.getX(), arg1.getY());
////		screenView.drawInfo("Input Key");
//		screenView.drawNow(true);
//		backKeyCount = 0;
//		touched = true;
//		saveFileDialog.saved = false;
//		noTouchData = false;
		
		
		switch (arg1.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchX = arg1.getRawX(); //yuan dian
				touchY = arg1.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float tx = arg1.getRawX(); //zhong dian
				float ty = arg1.getRawY();
				float tr = (float)Math.sqrt(Math.pow(Math.abs(touchX - tx) , 2) + Math.pow(Math.abs(touchY - ty) , 2));
				touchR = tr;
				bop = new BlueoceanPosition();
				bop.x = touchX;
				bop.y = touchY;
				bop.r = touchR;
				bop.msg = "";
				backKeyCount = 0;
				touched = true;
				saveFileDialog.saved = false;
				noTouchData = false;
				screenView.drawNow(true);
				screenView.drawCircle2(touchX, touchY, touchR);
				Log.e(TAG, "touch R = " + touchR + " touchX = " + touchX + " touchY = " + touchY);
				break;
			case MotionEvent.ACTION_UP:
				if (touchR == 0) {
					bop = new BlueoceanPosition();
					bop.x = arg1.getRawX();
					bop.y = arg1.getRawY();
					bop.r = 0;
					bop.msg = "";
					screenView.drawNow(true);
//					screenView.drawCircle(arg1.getX(), arg1.getY());
					screenView.drawCircle(arg1.getRawX(), arg1.getRawY());
					backKeyCount = 0;
					touched = true;
					saveFileDialog.saved = false;
					noTouchData = false;
				}
				break;
		}
		return false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "onkeyDOwn"); 
		if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
			backKeyCount ++;
			if (backKeyCount == 1 && noTouchData) return super.onKeyDown(keyCode, event);
			if (backKeyCount < 2) {
				screenView.drawNow(false);
				touched = false;
				BlueoceanCore.gameStart = true;
				touchR = 0;
				return false;
			}
		}
		Log.e(TAG, "touched = " + touched + " oldkey = " +oldKey + " event.getKeyCode = " + event.getKeyCode());
		if (touched && (oldKey != event.getKeyCode())) {
			if (!drawInfo(event)) return false;
			BlueoceanProfile mProfile = new BlueoceanProfile();
			mProfile.key = event.getKeyCode();
//			mProfile.key = event.getScanCode();
			mProfile.posX = screenView.getTouchX();  
			mProfile.posY = screenView.getTouchY();
			mProfile.posR = screenView.getTouchR();
			mProfile.posType = screenView.getCircleType();
			keyList.add(mProfile);
			BlueoceanCore.keyList = keyList;
			if (event.getKeyCode() != KeyEvent.KEYCODE_SEARCH) { //配置joystick的按键
				oldKey = event.getKeyCode();
			}
			if (debug) Log.e(TAG, "config a key pos scankey= " + oldKey + " posx= " + mProfile.posX + " posy= " + mProfile.posY);
			screenView.drawNow(false);
			touched = false;
			backKeyCount ++;
			return false;
		}
		if (KeyEvent.KEYCODE_BACK == event.getKeyCode() && !saveFileDialog.saved) {
			requestToSaveFile();
		}
		return super.onKeyDown(keyCode, event);
	}
	private void requestToSaveFile() {
		saveFileDialog.show();
	}
	private boolean drawInfo(KeyEvent event) {
		if (bop.r > 0 && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) { //touchR == 0 则是触摸点和按键的映射，touchR > 0则是摇杆区域映射
			bop.color = Color.GREEN;
			DisplayMetrics dm = this.getResources().getDisplayMetrics();
			if ((bop.x > (dm.widthPixels / 2)) && ((bop.x - bop.r) <=  (dm.widthPixels / 2))) { //如果圆的中心点X坐标bop.x落在屏的右半边，则这个圆是右摇杆区域，反之则是左摇杆区域
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			} else if ((bop.x < (dm.widthPixels /2)) && ((bop.x + bop.r) >= (dm.widthPixels /2))) {
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			}else if (bop.r <= 30) {
				Log.e(TAG, "bop.r = " + bop.r + " invalid joystick_area");
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			}
			if ((bop.x - bop.r) > (dm.widthPixels/2)) { //右摇杆区域
				if (hasRightJoystick) {
					Toast.makeText(this, this.getString(R.string.has_right_joystick), Toast.LENGTH_SHORT).show();
					return false;
				}
				bop.msg = this.getString(R.string.right_joystick);
				hasRightJoystick = true;
				bop.type = BlueoceanPosition.TYPE_RIGHT_JOYSTICK;
				screenView.setCircleType(bop.type);
			} else {
				if (hasLeftJoystick) {
					Toast.makeText(this, this.getString(R.string.has_left_joystick), Toast.LENGTH_SHORT).show();
					return false;
				}
				bop.msg = this.getString(R.string.left_joystick);
				hasLeftJoystick = true;
				bop.type = BlueoceanPosition.TYPE_LEFT_JOYSTICK;
				screenView.setCircleType(bop.type);
			}
			screenView.posList.add(bop); 
			touchR = 0;
			return true;
		} else if (touchR > 0) return false;
		bop.color = Color.GREEN;
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_0:
			bop.msg = "0";
			break;
		case KeyEvent.KEYCODE_1:
			bop.msg = "1";
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			bop.msg = "VOLUME_DOWN";
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			bop.msg = "VOLUME_UP";
			break;
		case KeyEvent.KEYCODE_2:
			bop.msg = "2";
			break;
		case KeyEvent.KEYCODE_3:
			bop.msg = "3";
			break;
		case KeyEvent.KEYCODE_4:
			bop.msg = "4";
			break;
		case KeyEvent.KEYCODE_5:
			bop.msg = "5";
			break;
		case KeyEvent.KEYCODE_6:
			bop.msg = "6";
			break;
		case KeyEvent.KEYCODE_7:
			bop.msg = "7";
			break;
		case KeyEvent.KEYCODE_8:
			bop.msg = "8";
			break;
		case KeyEvent.KEYCODE_9:
			bop.msg = "9";
			break;
		case KeyEvent.KEYCODE_A:
			bop.msg = "A";
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:
			bop.msg = "ALT_LEFT";
			break;
		case KeyEvent.KEYCODE_ALT_RIGHT:
			bop.msg = "ALT_RIGHT";
			break;
		case KeyEvent.KEYCODE_APOSTROPHE:
			bop.msg = "APOSTROPHE";
			break;
		case KeyEvent.KEYCODE_B:
			bop.msg = "B";
			break;
		case KeyEvent.KEYCODE_BACKSLASH:
			bop.msg = "BACKSLASH";
			break;
		case KeyEvent.KEYCODE_C:
			bop.msg = "C";
			break;
		case KeyEvent.KEYCODE_COMMA:
			bop.msg = "COMMA";
			break;
		case KeyEvent.KEYCODE_D:
			bop.msg = "D";
			break;
		case KeyEvent.KEYCODE_E:
			bop.msg = "E";
			break;
		case KeyEvent.KEYCODE_EQUALS:
			bop.msg = "EQUALS";
			break;
		case KeyEvent.KEYCODE_F:
			bop.msg = "F";
			break;
		case KeyEvent.KEYCODE_G:
			bop.msg = "G";
			break;
		case KeyEvent.KEYCODE_H:
			bop.msg = "H";
			break;
		case KeyEvent.KEYCODE_I:
			bop.msg = "I";
			break;
		case KeyEvent.KEYCODE_J:
			bop.msg = "J";
			break;
		case KeyEvent.KEYCODE_K:
			bop.msg = "K";
			break;
		case KeyEvent.KEYCODE_L:
			bop.msg = "L";
			break;
		case KeyEvent.KEYCODE_N:
			bop.msg = "N";
			break;
		case KeyEvent.KEYCODE_M:
			bop.msg = "M";
			break;
		case KeyEvent.KEYCODE_O:
			bop.msg = "O";
			break;
		case KeyEvent.KEYCODE_P:
			bop.msg = "P";
			break;
		case KeyEvent.KEYCODE_Q:
			bop.msg = "Q";
			break;
		case KeyEvent.KEYCODE_R:
			bop.msg = "R";
			break;
		case KeyEvent.KEYCODE_S:
			bop.msg = "S";
			break;
		case KeyEvent.KEYCODE_T:
			bop.msg = "T";
			break;
		case KeyEvent.KEYCODE_U:
			bop.msg = "U";
			break;
		case KeyEvent.KEYCODE_V:
			bop.msg = "V";
			break;
		case KeyEvent.KEYCODE_W:
			bop.msg = "W";
			break;
		case KeyEvent.KEYCODE_X:
			bop.msg = "X";
			break;
		case KeyEvent.KEYCODE_Y:
			bop.msg = "Y";
			break;
		case KeyEvent.KEYCODE_Z:
			bop.msg = "Z";
			break;
		case KeyEvent.KEYCODE_BUTTON_A:
			bop.msg = "BUTTON_A";
			break;
		case KeyEvent.KEYCODE_BUTTON_B:
			bop.msg = "BUTTON_B";
			break;
		case KeyEvent.KEYCODE_BUTTON_C:
			bop.msg = "BUTTON_C";
			break;
		case KeyEvent.KEYCODE_BUTTON_L1:
			bop.msg = "BUTTON_L1";
			break;
		case KeyEvent.KEYCODE_BUTTON_L2:
			bop.msg = "BUTTON_L2";
			break;
		case KeyEvent.KEYCODE_BUTTON_MODE:
			bop.msg = "BUTTON_MODE";
			break;
		case KeyEvent.KEYCODE_BUTTON_R1:
			bop.msg = "BUTTON_R1";
			break;
		case KeyEvent.KEYCODE_BUTTON_R2:
			bop.msg = "BUTTON_R2";
			break;
		case KeyEvent.KEYCODE_BUTTON_SELECT:
			bop.msg = "BUTTON_SELECT";
			break;
		case KeyEvent.KEYCODE_BUTTON_START:
			bop.msg = "BUTTON_START";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBL:
			bop.msg = "BUTTON_THUMBL";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBR:
			bop.msg = "BUTTON_THUMBR";
			break;
		case KeyEvent.KEYCODE_BUTTON_X:
			bop.msg = "BUTTON_X";
			break;
		case KeyEvent.KEYCODE_BUTTON_Y:
			bop.msg = "BUTTON_Y";
			break;
		case KeyEvent.KEYCODE_BUTTON_Z:
			bop.msg = "BUTTON_Z";
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			bop.msg = "DPAD_CENTER";
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			bop.msg = "DPAD_DWON";
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			bop.msg = "DPAD_LEFT";
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			bop.msg = "DPAD_RIGHT";
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			bop.msg = "DPAD_UP";
			break;
		case KeyEvent.KEYCODE_LEFT_BRACKET:
			bop.msg = "LEFT_BRACKET";
			break;
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			bop.msg = "MEDIA_FAST_FORWARD";
			break;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			bop.msg = "MEDIA_NEXT";
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			bop.msg = "MEDIA_PLAY_PAUSE";
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			bop.msg = "MEDIA_PREVIOUS";
			break;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
			bop.msg = "MEDIA_REWIND";
			break;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			bop.msg = "MEDIA_STOP";
			break;
		case KeyEvent.KEYCODE_MINUS:
			bop.msg = "MINUS";
			break;
		case KeyEvent.KEYCODE_NUM:
			bop.msg = "NUM";
			break;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			bop.msg = "PAGE_DOWN";
			break;
		case KeyEvent.KEYCODE_PAGE_UP:
			bop.msg = "PAGE_UP";
			break;
		case KeyEvent.KEYCODE_PICTSYMBOLS:
			bop.msg = "PICTSYMBOLS";
			break;
		case KeyEvent.KEYCODE_PLUS:
			bop.msg = "PLUS";
			break;
		case KeyEvent.KEYCODE_POUND:
			bop.msg = "POUND";
			break;
		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			bop.msg = "RIGHT_BRACKET";
			break;
		case KeyEvent.KEYCODE_SEARCH:
			bop.msg = "SEARCH";
			break;
		case KeyEvent.KEYCODE_SEMICOLON:
			bop.msg = "SEMICOLON";
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			bop.msg = "SHIFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			bop.msg = "SHIFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SLASH:
			bop.msg = "SLASH";
			break;
		case KeyEvent.KEYCODE_SOFT_LEFT:
			bop.msg = "SOFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SOFT_RIGHT:
			bop.msg = "SOFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SPACE:
			bop.msg = "SPACE";
			break;
		case KeyEvent.KEYCODE_STAR:
			bop.msg = "STAR";
			break;
		case KeyEvent.KEYCODE_SYM:
			bop.msg = "SYM";
			break;
		case KeyEvent.KEYCODE_TAB:
			bop.msg = "TAB";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_1:
			bop.msg = "BUTTON_1";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_2:
			bop.msg = "BUTTON_2";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_3:
			bop.msg = "BUTTON_3";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_4:
			bop.msg = "BUTTON_4";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_5:
			bop.msg = "BUTTON_5";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_6:
			bop.msg = "BUTTON_6";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_7:
			bop.msg = "BUTTON_7";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_8:
			bop.msg = "BUTTON_8";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_9:
			bop.msg = "BUTTON_9";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_10:
			bop.msg = "BUTTON_10";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_11:
			bop.msg = "BUTTON_11";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_12:
			bop.msg = "BUTTON_12";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_13:
			bop.msg = "BUTTON_13";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_14:
			bop.msg = "BUTTON_14";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_15:
			bop.msg = "BUTTON_15";
			break;
		case BlueoceanCore.KEYCODE_BUTTON_16:
			bop.msg = "BUTTON_16";
			break;
		default: break;
		}
		screenView.posList.add(bop);
		return true;
	}
}
