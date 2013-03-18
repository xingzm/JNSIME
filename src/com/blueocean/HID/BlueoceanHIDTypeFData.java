package com.blueocean.HID;

import java.nio.ByteBuffer;

import com.blueocean.hardware.JoystickTypeF;
import com.blueocean.ime.BlueoceanCore;
import com.blueocean.ime.BlueoceanCore.Pos;
import com.blueocean.ime.BlueoceanMotionManager;
import com.blueocean.ime.BlueoceanPosition;
import com.blueocean.ime.BlueoceanProfile;
import com.blueocean.ime.BlueoceanTpConfigActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.view.MotionEvent.PointerCoords;  
import android.view.MotionEvent.PointerProperties;  

public class BlueoceanHIDTypeFData {
	private static final String TAG = "BlueoceanHIDTypeFData";
	private UsbDeviceConnection mUsbDeviceConnection = null;
	private UsbDeviceConnection mGameUsbDeviceConnection = null;
	private boolean deviceAttached = true;
	private Context context = null;
	private boolean gameDataValid = false; //pressed start & select buttons, then joystick game data is valid.
	private boolean combinationKeysPressed = false;
	private boolean rightMotionKey = false;
	private boolean leftMotionKey = false;
	private float rightJoystickCurrentPosX = 0.0f;
	private float rightJoystickCurrentPosY = 0.0f;
	private float leftJoystickCurrentPosX = 0.0f;
	private float leftJoystickCurrentPosY = 0.0f;
	private float joystickR = 0.0f;
	private float rightJoystickCurrentR = 0.0f;
	private float leftJoystickCurrentR = 0.0f;
	
	private boolean DPADKeyRightPressed = false;
	private boolean DPADKeyLeftPressed = false;
	private boolean DPADKeyUpPressed = false;
	private boolean DPADKeyDownPressed = false;
	private boolean KeyMenuPressed = false;
	private boolean keyOKPressed = false;
	private boolean GameButtonL1Pressed = false;
	private boolean GameButtonR1Pressed = false;
	private boolean GameButtonXPressed = false;
	private boolean GameButtonYPressed = false;
	private boolean GameButtonSelectPressed = false;
	private boolean GameButtonStartPressed = false;
	//add by steven
	private boolean GameButtonAPressed = false;
	private boolean GameButtonBPressed = false;
	private boolean LeftJoystickPresed = false;
	private boolean RightJoystickPresed = false;

	@SuppressWarnings("unchecked")
	private List<String> pressed_Keys = new ArrayList();
	private List<String> relessed_Keys = new ArrayList();
	
	long last_left_press_time = 0;
	long last_right_press_time = 0;
	
	public BlueoceanHIDTypeFData(Context context) {
		this.context = context;
	}
	
	 public boolean getData(UsbManager manager, UsbDevice device) {
		 boolean returnFlag = false;
		 if (device != null)  {
			 int intrfCount = device.getInterfaceCount();
			 if (intrfCount == JoystickTypeF.INTERFACE_COUNT) { // the device interface count is equals joystick type f device interface
				 
				 /**get joystic type f keyboard interface and data */
				 UsbInterface intrf = device.getInterface(JoystickTypeF.KEYBOARD_INTERFACE); // get joystick type f keyboard interface
				 Log.e(TAG, "1  intrf = " + intrf + "getInterfaceClass = " +intrf.getInterfaceClass());
				 if (intrf.getInterfaceClass() == UsbConstants.USB_CLASS_HID)  { // type f keyboard interface protocol is HID
					 if (intrf.getEndpointCount() == JoystickTypeF.KEYBOARD_ENDPOINT_COUNT) { // keyboard interface endpoint count is equals type f keyboard endpont count
						 UsbEndpoint mUsbEndpoint = intrf.getEndpoint(0);
						 if (manager != null) {
							 mUsbDeviceConnection = manager.openDevice(device);
							 if (mUsbDeviceConnection != null && mUsbDeviceConnection.claimInterface(intrf, true)) {
								 new KeyboardDataReader(mUsbEndpoint).start(); 
								 returnFlag = true;
							 } else {
								 mUsbDeviceConnection = null;
								 Log.e(TAG, "mUsbDeviceConnection = " + mUsbDeviceConnection + " KeyboardDataReader thread can't run ");
							 }
						 }
					 }
				 }
				 /**get joystick type f game interface and data */
				 intrf = device.getInterface(JoystickTypeF.GAME_INTERFACE);
				 Log.e(TAG, "2 intrf = " + intrf + "getInterfaceClass = " +intrf.getInterfaceClass());
				 if (intrf.getInterfaceClass() == UsbConstants.USB_CLASS_HID) {
					 if (intrf.getEndpointCount() == JoystickTypeF.GAME_ENDPOINT_COUNT) {
						 UsbEndpoint mUsbEndpoint = intrf.getEndpoint(0);
						 if (manager != null) {
							 if (mGameUsbDeviceConnection == null) {
								 mGameUsbDeviceConnection = manager.openDevice(device);
							 }
							 if (mGameUsbDeviceConnection != null && mGameUsbDeviceConnection.claimInterface(intrf, true)) {
								 new GameDataReader(mUsbEndpoint).start();
								 returnFlag = true;
							 } else {
								 mGameUsbDeviceConnection = null;
								 Log.e(TAG, "mGameUsbDeviceConnection = " + mGameUsbDeviceConnection + " GameDataReader thread can't run ");
							 }
						 }
					 }
				 }
			 }
		 }
		 if (returnFlag) {
//			 BlueoceanMotionManager.fd = BlueoceanMotionManager.openDevice();
		 }
		 return returnFlag;
	 }
	 
	 class KeyboardDataReader extends Thread {
		 private UsbEndpoint endpoint = null;
		 public KeyboardDataReader(UsbEndpoint endpoint) {
			 this.endpoint= endpoint;
		 }
		 @Override
		 public void run() {
			 Log.e(TAG, "KeyboardDataReader thread running");
			 ByteBuffer buffer = ByteBuffer.allocate(JoystickTypeF.KEYBOARD_DATA_LEN);
			 UsbRequest request = new UsbRequest();
			 deviceAttached = true;
			 while (deviceAttached) {
//				 synchronized(this) {
				 request.initialize(mUsbDeviceConnection, endpoint);
				 request.queue(buffer, buffer.limit()); 
				 if (mUsbDeviceConnection.requestWait() == request) {
					 startTouchConfigurationView(buffer.get(JoystickTypeF.KEYBOARD_START_TPCONFIG_INDEX));
					 processKeyBoardKeys(buffer);
					 String str = "";
						for (int i = 0; i < buffer.limit(); i ++) {
							str += Integer.toHexString(buffer.get(i) & 0x00ff) + " ";
						}
						Log.e(TAG, "KEYBOARD str = " + str);
				 }
				 Log.e(TAG, "Keyboard request finished");
//				 }
			 }
			 mUsbDeviceConnection.close();
			 mUsbDeviceConnection = null;
		 }
	 }
	 
	 private void copylist(List<String> des, List<String> src)
	 {
		des.removeAll(des);
		for(String s:src)
		{
		    des.add(s);
		}
	 }
	 private boolean match_PressedKeys(byte bytekey)
	 {		

			for(int i = 0; i < pressed_Keys.size(); i++)
			{
				byte pressed_key = (new Integer(pressed_Keys.get(i))).byteValue();
				
				if(bytekey == pressed_key)
				{	
					
					relessed_Keys.remove(pressed_Keys.get(i));
					return false;
				}
			}
			if(bytekey != 0)
			{
				pressed_Keys.add(bytekey+"");
				Log.e(TAG, "add bytekey = "+bytekey);
				return true;
			}
			else return false;		
	}
	 private void processKeyBoardKeys(ByteBuffer keyBytes) {
		//Log.e("BlueoceanCore", "get keyByte = :"+ keyBytes.get(2)+" "+keyBytes.get(3)+" "+keyBytes.get(4)+" "+keyBytes.get(5)+" "+keyBytes.get(6)+" "+keyBytes.get(7)+" ");	
		// add by steven
		 copylist(relessed_Keys, pressed_Keys);
		 for(int i = 2; i < JoystickTypeF.KEYBOARD_DATA_LEN; i++)
		 {	
			 if(match_PressedKeys(keyBytes.get(i)))
			 {
				 switch (keyBytes.get(i)) {
				 case JoystickTypeF.DPAD_KEY_DOWN:
					 if (!DPADKeyDownPressed) {
						 this.DPADKeyDownPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
					 }
					 break;
				 case JoystickTypeF.DPAD_KEY_LEFT:
					 if (!DPADKeyLeftPressed) {
						 this.DPADKeyLeftPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
					 }
					 break;
				 case JoystickTypeF.DPAD_KEY_RIGHT:
					 if (!DPADKeyRightPressed) {
						 this.DPADKeyRightPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
					 }
					 break;
				 case JoystickTypeF.DPAD_KEY_UP:
					 if (!DPADKeyUpPressed) {
						 this.DPADKeyUpPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
					 }
					 break;
				 case JoystickTypeF.KEY_MENU:
					 if (!KeyMenuPressed) {
						 this.KeyMenuPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU);
					 }
					 break;
				 case JoystickTypeF.KEY_OK:
					 if (!keyOKPressed) {
						 this.keyOKPressed = true;
						 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
					 }
					 break;
			 	}
			 }
		 }
		 for(int i = 0; i< relessed_Keys.size();i++)
		 {
			 byte relese_key = (new Integer(relessed_Keys.get(i))).byteValue();
			 pressed_Keys.remove(relessed_Keys.get(i));
			 switch (relese_key) {
			 case JoystickTypeF.DPAD_KEY_DOWN:
				 if (DPADKeyDownPressed) {
					 this.DPADKeyDownPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_LEFT:
				 if (DPADKeyLeftPressed) {
					 this.DPADKeyLeftPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_RIGHT:
				 if (DPADKeyRightPressed) {
					 this.DPADKeyRightPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_UP:
				 if (DPADKeyUpPressed) {
					 this.DPADKeyUpPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP);
				 }
				 break;
			 case JoystickTypeF.KEY_MENU:
				 if (KeyMenuPressed) {
					 this.KeyMenuPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU);
				 }
				 break;
			 case JoystickTypeF.KEY_OK:
				 if (keyOKPressed) {
					 this.keyOKPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
				 }
				 break;
			 }
		 }
		//add end
		/*	
		for(int i = 2; i< 8; i++)
		{
		    for(int pressed_key: pressed_keys)
		    {
			  if(keyBytes.get(i) == pressed_key)
		    }	
		}
		for(int i = 2; i< 3; i++)
		{
		 byte byteKey = keyBytes.get(i);
		 
		 switch (byteKey) {
			 case JoystickTypeF.DPAD_KEY_DOWN:
				 if (!DPADKeyDownPressed) {
					 this.DPADKeyDownPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_LEFT:
				 if (!DPADKeyLeftPressed) {
					 this.DPADKeyLeftPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_RIGHT:
				 if (!DPADKeyRightPressed) {
					 this.DPADKeyRightPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT);
				 }
				 break;
			 case JoystickTypeF.DPAD_KEY_UP:
				 if (!DPADKeyUpPressed) {
					 this.DPADKeyUpPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP);
				 }
				 break;
			 case JoystickTypeF.KEY_MENU:
				 if (!KeyMenuPressed) {
					 this.KeyMenuPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU);
				 }
				 break;
			 case JoystickTypeF.KEY_OK:
				 if (!keyOKPressed) {
					 this.keyOKPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
				 }
				 break;
			 case 0:
				 if (this.DPADKeyDownPressed) {
					 this.DPADKeyDownPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN);
				 }
				 if (this.DPADKeyLeftPressed) {
					 this.DPADKeyLeftPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT);
				 }
				 if (this.DPADKeyRightPressed) {
					 this.DPADKeyRightPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT);
				 }
				 if (this.DPADKeyUpPressed) {
					 this.DPADKeyUpPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP);
				 }
				 if (this.KeyMenuPressed) {
					 this.KeyMenuPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU);
				 }
				 if (this.keyOKPressed) {
					 this.keyOKPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER);
				 }
				 break;
		 } 
		}
		*/
	 }
	 
	 class GameDataReader extends Thread {
		 private UsbEndpoint endpoint = null;
		 public GameDataReader(UsbEndpoint endpoint) {
			 this.endpoint= endpoint;
		 }
		 @Override
		 public void run() {
			 Log.e(TAG, "GameDataReader thread running");
			 ByteBuffer buffer = ByteBuffer.allocate(JoystickTypeF.GAME_DATA_LEN);
			 UsbRequest request = new UsbRequest();
			 while (deviceAttached) {
//				 synchronized(this) {
				 request.initialize(mGameUsbDeviceConnection, endpoint);
				 request.queue(buffer, buffer.limit());
				 if (mGameUsbDeviceConnection.requestWait() == request) {
					 processGameData(buffer);
					 String str = "";
						for (int i = 0; i < buffer.limit(); i ++) {
							str += Integer.toHexString(buffer.get(i) & 0x00ff);
						}
					//	Log.e(TAG, "GAME str = " + str);
				 }
				// Log.e(TAG, "GameDataReader request finished");
//				 }
			 }
			 mGameUsbDeviceConnection.close();
			 mGameUsbDeviceConnection = null;
		 }
	 }
	 
	 private void checkGameDataValid(byte data) {
		 Log.e(TAG, "1 checkGameDataValid data = " + data);
		 if (((data & JoystickTypeF.GAME_START_KEY2_BIT) == JoystickTypeF.GAME_START_KEY2_BIT) 
				 && ((data & JoystickTypeF.GAME_SELECT_KEY2_BIT) == JoystickTypeF.GAME_SELECT_KEY2_BIT) ) {
			 combinationKeysPressed = true;
		 } else if (data == 0) {
			 if (combinationKeysPressed) {
				 this.gameDataValid = this.gameDataValid ? false : true;
				 String imeStr = "";
				 if (this.gameDataValid) {
					imeStr = BlueoceanCore.JNSIMEID;
				 } else {
					 imeStr = BlueoceanCore.lastIMEID;
				 }
				 Intent intent = new Intent();
				intent.setAction("COM.BLUEOCEAN_IME_SWITCH_IME");
				intent.putExtra("COM.BLUEOCEAN_IME_IMEID", imeStr);
				context.sendBroadcast(intent);
			 }
			 combinationKeysPressed = false;
		 }
	 }
	 
	 private double calcSinA(int bx, int by, int joystickType) {
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int x = Math.abs(ox - bx);
		 int y = Math.abs(oy - by);
		 double r = Math.sqrt(Math.pow((double) x, 2) + Math.pow((double)y, 2));
		 if (joystickType == BlueoceanPosition.TYPE_LEFT_JOYSTICK) {
			 this.leftJoystickCurrentR = (float) r;
		 } else if (joystickType == BlueoceanPosition.TYPE_RIGHT_JOYSTICK) {
			 this.rightJoystickCurrentR = (float) r;
		 }
		 this.joystickR = 127;
		 double sin = ((double)y) / r;
		 return sin;
	 }
	 
	 private void processRightJoystickData(byte bx, byte by) { // x = buffer[3] y = buffer[4]
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int ux = bx;
		 int uy = by;
		 if (bx < 0) ux = 256 + bx;
		 if (by < 0) uy = 256 + by;
		 
             

		 
//		 if (bx != 0x7f || by != 0x7f) {
			 if (BlueoceanCore.keyList != null) 
			 {
				 for (BlueoceanProfile bp: BlueoceanCore.keyList)
				 {
					 if (bp.posR > 0 && bp.posType == BlueoceanPosition.TYPE_RIGHT_JOYSTICK) 
					 {
						 double sin = calcSinA(ux, uy, BlueoceanPosition.TYPE_RIGHT_JOYSTICK);
						 double touchR1 = (bp.posR/this.joystickR) * this.rightJoystickCurrentR;
						// Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " rightJoystickCurrentR = " + rightJoystickCurrentR);
						 double y = touchR1 * sin;
						 double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
						 float rawX = 0.0f;
						 float rawY = 0.0f;
						 if (ux < ox && uy < oy) {  //坐标轴上半部的左
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY - (float)y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis positive left part");
						 } else if (ux > ox && uy < oy) {  //坐标轴上半部的右
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY - (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis positive right part");
						 } else if (ux < ox && uy > oy) { //坐标轴下半部的左
							 rawX = bp.posX  - (float) x;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis negtive left part");
						 } else if (ux > ox && uy > oy) { //坐标轴下半部的右
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis negtiveleft part");
						 } else if (ux == ox && uy < oy) { //Y轴变化
							 rawX = bp.posX;
							 rawY = bp.posY - (float)y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis Y < 0x7f");
						 } else if (ux == ox && uy > oy) { //Y轴变化
							 rawX = bp.posX;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis Y > 0x7f");
						 } else if (ux < ox && uy == oy) { //X轴变化
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY;
							 rightMotionKey = true;
							 Log.e(TAG, "axis X < 0x7f");
						 } else if (ux > ox && uy == oy) { //X轴变化
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY;
							 rightMotionKey = true;
							 Log.e(TAG, "axis X  > 0x7f");
						 } else if (ux == ox && uy == oy && rightMotionKey) {
							 Log.e(TAG, "right  you release map");
							 BlueoceanCore.motionQueue.offer(new Pos(rightJoystickCurrentPosX, rightJoystickCurrentPosY, 
									 MotionEvent.ACTION_UP, JoystickTypeF.RIGHT_JOYSTICK_TAG));
							 //	BlueoceanCore.sendMotionUpByInstrumentation(JoystickTypeF.RIGHT_JOYSTICK_TAG,rightJoystickCurrentPosX, rightJoystickCurrentPosY);
							 rightMotionKey = false;
							 RightJoystickPresed = false;
						 }
						 if (rightMotionKey) {
								if(!RightJoystickPresed)
								{
								//	BlueoceanCore.sendMotionDownByInstrumentation(JoystickTypeF.RIGHT_JOYSTICK_TAG,bp.posX, bp.posY);
									BlueoceanCore.motionQueue.offer(new Pos(bp.posX, bp.posY, MotionEvent.ACTION_DOWN, JoystickTypeF.RIGHT_JOYSTICK_TAG));
									RightJoystickPresed = true;								
								}
							
								if(RightJoystickPresed)
								{
									if((rawX != rightJoystickCurrentPosX) && (rawY != rightJoystickCurrentPosY))
									//BlueoceanCore.sendMotionMoveByInstrumentation(JoystickTypeF.RIGHT_JOYSTICK_TAG,rawX, rawY);
										if(System.currentTimeMillis() - last_right_press_time > 50)
										{		
											BlueoceanCore.motionQueue.offer(new Pos(rawX, rawY, MotionEvent.ACTION_MOVE, JoystickTypeF.RIGHT_JOYSTICK_TAG));
											last_right_press_time = System.currentTimeMillis();
										}
									}
							
					//	 Log.e(TAG, "right test up bp.posR = " + bp.posR + " bp.postX = " + bp.posX + " bp.posY = " + bp.posY
					//			 + " y = " + y + " x = " + x + " rawX = " + rawX + " rawY = " + rawY + " bx = " + Integer.toHexString(bx) 
					//			 + " by = " + Integer.toHexString(by) + " ux = " + ux + " uy = " + uy);
					 	}
						rightJoystickCurrentPosX = rawX;
						rightJoystickCurrentPosY = rawY;
				 	}
				 }
		 }
		 /*
		 else
		 {
			   PointerProperties[] properties = new PointerProperties[1];  
		 	   PointerCoords[] pointerCoords = new PointerCoords[1];  
			   PointerProperties pp = new PointerProperties();
			   PointerCoords pc = new PointerCoords();
			   pp.id= 0;  
           		   pp.id= MotionEvent.TOOL_TYPE_UNKNOWN;  
            		   pc.x =  ((float)bx)/127.0f;
			   pc.y =  ((float)by)/127.0f;
			    properties[0] = pp;
			    pointerCoords[0] = pc;
			   BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),  
                		MotionEvent.ACTION_DOWN, 1, properties,  
             		  pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0)); 
		 }
		*/
	 }
	 
	 private void processLeftJoystickData(byte bx, byte by) { // x = buffer[3] y = buffer[4]
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int ux = bx;
		 int uy = by;
		 if (bx < 0) ux = 256 + bx;
		 if (by < 0) uy = 256 + by;
	//	BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
	//								 MotionEvent.ACTION_MOVE, rightJoystickCurrentPosX, rightJoystickCurrentPosY, 0));
		 
//		 if (bx != 0x7f || by != 0x7f) {
			 if (BlueoceanCore.keyList != null) 
			 {
				 for (BlueoceanProfile bp: BlueoceanCore.keyList) 
				 {
					 if (bp.posR > 0 && bp.posType == BlueoceanPosition.TYPE_LEFT_JOYSTICK) 
					 {
						 double sin = calcSinA(ux, uy, BlueoceanPosition.TYPE_LEFT_JOYSTICK);
//						 double y = bp.posR * sin;
//						 double x = Math.sqrt(Math.pow(bp.posR, 2) - Math.pow(y, 2));
						 double touchR1 = (bp.posR/this.joystickR) * this.leftJoystickCurrentR;
					//	 Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " leftJoystickCurrentR = " + leftJoystickCurrentR);
						 double y = touchR1 * sin;
						 double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
						 float rawX = 0.0f;
						 float rawY = 0.0f;
						 if (ux < ox && uy < oy) {  //坐标轴上半部的左
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY - (float)y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis positive left part");
						 } else if (ux > ox && uy < oy) {  //坐标轴上半部的右
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY - (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis positive right part");
						 } else if (ux < ox && uy > oy) { //坐标轴下半部的左
							 rawX = bp.posX  - (float) x;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis negtive left part");
						 } else if (ux > ox && uy > oy) { //坐标轴下半部的右
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis negtiveleft part");
						 } else if (ux == ox && uy < oy) { //Y轴变化
							 rawX = bp.posX;
							 rawY = bp.posY - (float)y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis Y < 0x7f");
						 } else if (ux == ox && uy > oy) { //Y轴变化
							 rawX = bp.posX;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis Y > 0x7f");
						 } else if (ux < ox && uy == oy) { //X轴变化
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY;
							 leftMotionKey = true;
							 Log.e(TAG, "axis X < 0x7f");
						 } else if (ux > ox && uy == oy) { //X轴变化
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY;
							 leftMotionKey = true;
							 Log.e(TAG, "axis X  > 0x7f");
						 } else if (ux == ox && uy == oy && leftMotionKey) {
							 Log.e(TAG, "left joystick you release map");
							// BlueoceanCore.sendMotionUpByInstrumentation(JoystickTypeF.LEFT_JOYSTICK_TAG,leftJoystickCurrentPosX, leftJoystickCurrentPosY);
							 BlueoceanCore.motionQueue.offer(new Pos(leftJoystickCurrentPosX, leftJoystickCurrentPosY, 
									 MotionEvent.ACTION_UP, JoystickTypeF.LEFT_JOYSTICK_TAG));
							 leftMotionKey = false;
							 LeftJoystickPresed = false;
						 }
						 
						 if (leftMotionKey) 
						 {
							
							if(!LeftJoystickPresed)
							{
							//	BlueoceanCore.sendMotionDownByInstrumentation(JoystickTypeF.LEFT_JOYSTICK_TAG, bp.posX, bp.posY);							Log.d("BlueoceanCore","leftjoy down"); 
								BlueoceanCore.motionQueue.offer(new Pos(bp.posX, bp.posY, MotionEvent.ACTION_DOWN, JoystickTypeF.LEFT_JOYSTICK_TAG));
								LeftJoystickPresed = true;
							}
							
							if(LeftJoystickPresed)
							{
									//BlueoceanCore.sendMotionMoveByInstrumentation(JoystickTypeF.LEFT_JOYSTICK_TAG,rawX, rawY);
								if((rawX != leftJoystickCurrentPosX) && (rawY != leftJoystickCurrentPosY))
									//BlueoceanCore.sendMotionMoveByInstrumentation(JoystickTypeF.RIGHT_JOYSTICK_TAG,rawX, rawY);
								 	if(System.currentTimeMillis() - last_left_press_time > 50)
								 	{	
								 		BlueoceanCore.motionQueue.offer(new Pos(rawX, rawY, MotionEvent.ACTION_MOVE, JoystickTypeF.LEFT_JOYSTICK_TAG));
								 		last_left_press_time = System.currentTimeMillis();
								 	}
						 	}
						   	
					//	 Log.e(TAG, "left test up bp.posR = " + bp.posR + " bp.postX = " + bp.posX + " bp.posY = " + bp.posY
					//			 + " y = " + y + " x = " + x + " rawX = " + rawX + " rawY = " + rawY + " bx = " + Integer.toHexString(bx) 
					//			 + " by = " + Integer.toHexString(by) + " ux = " + ux + " uy = " + uy);
						 }
					 leftJoystickCurrentPosX = rawX;
					leftJoystickCurrentPosY = rawY;
					 }
				 }
		      }
	 }
	 
	 private void startTouchConfigurationView(byte data) {
		 if (!BlueoceanCore.touchConfiging && data ==JoystickTypeF. KEYBOARD_START_TPCONFIG && this.gameDataValid) {
			 Log.e(TAG, "start BlueoceanTpConfigActivity" );
			 Intent intent = new Intent();
			 intent.setClass(context,  BlueoceanTpConfigActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 context.startActivity(intent);
		 }
	 }
	 
	 private void processGameData(ByteBuffer buffer) {
		 if (buffer.get(JoystickTypeF.GAME_ID_INDEX)  != JoystickTypeF.GAME_ID) {
			 Log.e(TAG, "game data invalid, got id = " + buffer.get(JoystickTypeF.GAME_ID_INDEX) + " correct id = " + JoystickTypeF.GAME_ID);
		 } else {
			 checkGameDataValid(buffer.get(JoystickTypeF.GAME_KEY2_INDEX));
			 processRightJoystickData(buffer.get(JoystickTypeF.GAME_Z_AXIS_INDEX), buffer.get(JoystickTypeF.GAME_RZ_AXIS_INDEX));
			 processLeftJoystickData(buffer.get(JoystickTypeF.GAME_X_AXIS_INDEX), buffer.get(JoystickTypeF.GAME_Y_AXIS_INDEX));
			 processGameButtonKey(buffer);
		 }
	 }
	 
	 private void processGameButtonKey(ByteBuffer buffer) {
					
		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_X) == JoystickTypeF.GAME_BUTTON_X)
		 {
			if (!this.GameButtonXPressed) {
					 this.GameButtonXPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_X);
			}
		 }
		 else 
		 {
			 if (this.GameButtonXPressed) {
					 this.GameButtonXPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_X);
			 }	
	         }

		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_Y) == JoystickTypeF.GAME_BUTTON_Y)
		 {
			if (!this.GameButtonYPressed) {
					 this.GameButtonYPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_Y);
			}
		 }
		 else 
		 {
			 if (this.GameButtonYPressed) {
					 this.GameButtonYPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_Y);
			 }	
	         }
		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_A) == JoystickTypeF.GAME_BUTTON_A)
		 {
			if (!this.GameButtonAPressed) {
					 this.GameButtonAPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A);
			}
		 }
		 else 
		 {
			 if (this.GameButtonAPressed) {
					 this.GameButtonAPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A);
			 }	
	         }
		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_B) == JoystickTypeF.GAME_BUTTON_B)
		 {
			if (!this.GameButtonBPressed) {
					 this.GameButtonBPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B);
			}
		 }
		 else 
		 {
			 if (this.GameButtonBPressed) {
					 this.GameButtonBPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B);
			 }	
	         }
		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_R1) == JoystickTypeF.GAME_BUTTON_R1)
		 {
			if (!this.GameButtonR1Pressed) {
					 this.GameButtonR1Pressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_R1);
			}
		 }
		 else 
		 {
			 if (this.GameButtonR1Pressed) {
					 this.GameButtonR1Pressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_R1);
			 }	
	         } 
		 if((buffer.get(5) & JoystickTypeF.GAME_BUTTON_L1 )== JoystickTypeF.GAME_BUTTON_L1)
		 {
			if (!this.GameButtonL1Pressed) {
					 this.GameButtonL1Pressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_L1);
			}
		 }
		 else 
		 {
			 if (this.GameButtonL1Pressed) {
					 this.GameButtonL1Pressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_L1);
			 }	
	         } 
		 /*		
		 switch (buffer.get(5)) {
			 case JoystickTypeF.GAME_BUTTON_X:
				 if (!this.GameButtonXPressed) {
					 this.GameButtonXPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_X);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_Y:
				 if (!this.GameButtonYPressed) {
					 this.GameButtonYPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_Y);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_A:
				 if (!this.GameButtonAPressed) {
					 this.GameButtonAPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_A);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_B:
				 if (!this.GameButtonBPressed) {
					 this.GameButtonBPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_B);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_R1:
				 if (!this.GameButtonR1Pressed) {
					 this.GameButtonR1Pressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_R1);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_L1:
				 if (!this.GameButtonL1Pressed) {
					 this.GameButtonL1Pressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_L1);
				 }
				 break;
			 case 0:
				 if (this.GameButtonXPressed) {
					 this.GameButtonXPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_X);
				 }
				 if (this.GameButtonYPressed) {
					 this.GameButtonYPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_Y);
				 }
				 if (this.GameButtonL1Pressed) {
					 this.GameButtonL1Pressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_L1);
				 }
				 if (this.GameButtonR1Pressed) {
					 this.GameButtonR1Pressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_R1);
				 }
				  if (this.GameButtonAPressed) {
					 this.GameButtonAPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_A);
				 }
				 if (this.GameButtonBPressed) {
					 this.GameButtonBPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_B);
				 }
				 break;
				
		 }*/
		 switch (buffer.get(6)) {
			 case JoystickTypeF.GAME_BUTTON_START:
				 if (!GameButtonStartPressed) {
					 this.GameButtonStartPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_START);
				 }
				 break;
			 case JoystickTypeF.GAME_BUTTON_SELECT:
				 if (!GameButtonSelectPressed) {
					 this.GameButtonSelectPressed = true;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BUTTON_SELECT);
				 }
				 break;
			 case 0:
				 if (this.GameButtonSelectPressed) {
					 this.GameButtonSelectPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_SELECT);
				 }
				 if (this.GameButtonStartPressed) {
					 this.GameButtonStartPressed = false;
					 BlueoceanCore.sendKeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BUTTON_START);
				 }
				 break;
		 }
	 }
	 
	 public void closeDevice() {
		 Log.e(TAG, "Device closed");
		 deviceAttached = false;
	 }
}
