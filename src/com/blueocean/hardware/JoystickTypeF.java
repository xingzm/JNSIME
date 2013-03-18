package com.blueocean.hardware;

public class JoystickTypeF {
	//Device vendor-id product-id
	/**HID_TYPE_F_VENDOR_ID = 42789*/
	public static int HID_TYPE_F_VENDOR_ID = 43045;
	/**HID_TYPE_F_PRODUCT_ID = 13092*/
	public static int HID_TYPE_F_PRODUCT_ID = 13092;
	
	// Joystick USB HID device Interface index;
	/**INTERFACE_COUNT=4 */
	public static final int INTERFACE_COUNT = 4;
	/**MOUSE_INTERFACE=0*/
	public static final int MOUSE_INTERFACE = 0;
	/** KEYBOARD_INTERFACE = 1*/
	public static final int KEYBOARD_INTERFACE = 1;
	/** MULTIMEDIA_INTERFACE = 2*/
	public static final int MULTIMEDIA_INTERFACE = 2;
	/** GAME_INTERFACE = 3*/
	public static final int GAME_INTERFACE = 3;
	
	/**KEYBOARD_ENDPOINT_COUNT = 1*/
	public static final int KEYBOARD_ENDPOINT_COUNT = 1;
	/**GAME_ENDPOINT_COUNT = 1*/
	public static final int GAME_ENDPOINT_COUNT = 1;
	
	
	
	// Joystick USB HID Device Interface data length;
	public static final int MOUSE_DATA_LEN = 5;
	public static final int KEYBOARD_DATA_LEN = 8;
	public static final int MULTIMEDIA_DATA_LEN = 3;
	public static final int GAME_DATA_LEN = 8;
	
	// mouse data type
	public static final int MOUSE_ID_INDEX = 0;
	public static final int MOUSE_BUTTON_INDEX = 1;
	public static final int MOUSE_X_AXIS_INDEX = 2; // value from -128 ~  127
	public static final int MOUSE_Y_AXIS_INDEX = 3; // value form -128 ~ 127
	public static final int MOUSE_WHEEL_INDEX = 4;
	
	
	//mouse data id
	public static final int MOUSE_ID = 0XAA;
	
	// mouse button bits
	public static final int MOUSE_LEFT_BUTTON_BIT = 0x01;
	public static final int MOUSE_RIGHT_BUTTON_BIT = 0X02;
	public static final int MOUSE_MIDDLE_BUTTON_BIT = 0X04;
	
	//mouse button state
	public static final int MOUSE_LEFT_BUTTON_PRESSED = 1;
	public static final int MOUSE_RIGHT_BUTTON_PRESSED = 1;
	public static final int MOUSE_MIDDLE_BUTTON_PRESSED = 1;
	
	//keyboard data type
	public static final int KEYBOARD_FUNCTION_KEY_INDEX = 0;
	
	//function key bits
	public static final int KEYBOARD_LEFT_CONTROL_KEY_BIT = 0X01;
	public static final int KEYBOARD_LEFT_SHIT_KEY_BIT = 0X02;
	public static final int KEYBOARD_LEFT_ALT_KEY_BIT = 0X04;
	public static final int KEYBOARD_LEFT_GUI_KEY_BIT = 0X08;
	public static final int KEYBOARD_RIGHT_CONTROL_KEY_BIT = 0X10;
	public static final int KEYBOARD_RIGHT_SHIT_KEY_BIT = 0X20;
	public static final int KEYBOARD_RIGHT_ALT_KEY_BIT = 0X40;
	public static final int KEYBAORD_RIGHT_GUI_KEY_BIT = 0X80;
	
	//multi-media key data type
	public static final int MULTIMEDIA_ID_INDEX = 0;
	
	//multi-media  data id
	public static final int MULTIMEDIA_ID = 0X03; 
	
	//game data type
	public static final int GAME_ID_INDEX = 0;
	public static final int GAME_X_AXIS_INDEX = 1;
	public static final int GAME_Y_AXIS_INDEX = 2;
	public static final int GAME_Z_AXIS_INDEX = 3;
	public static final int GAME_RZ_AXIS_INDEX = 4;
	public static final int GAME_KEY1_INDEX = 5;
	public static final int GAME_KEY2_INDEX = 6;
	public static final int GAME_HELMET_ANGLE_INDEX = 7; //reserve
	
	//game key1 bits
	public static final int GAME_A_KEY1_BIT = 0X01;
	public static final int GAME_B_KEY1_BIT = 0X02;
	public static final int GAME_C_KEY1_BIT = 0X04;
	public static final int GAME_X_KEY1_BIT = 0X08;
	public static final int GAME_Y_KEY1_BIT = 0X10;
	public static final int GAME_Z_KEY1_BIT = 0X20;
	public static final int GAME_L1_KEY1_BIT = 0X40;
	public static final int GAME_R1_KEY1_BIT = 0X80;
	
	//game key2 bits
	public static final int GAME_L2_KEY2_BIT = 0X01;
	public static final int GAME_R2_KEY2_BIT = 0X02;
	public static final int GAME_SELECT_KEY2_BIT = 0X04;
	public static final int GAME_START_KEY2_BIT = 0X08;
	
	public static final int RIGHT_JOYSTICK_TAG = 1;
	public static final int LEFT_JOYSTICK_TAG = 2;
	public static final int JOYSTICK_ZOOM_1_TAG =0x3;
	public static final int JOYSTICK_ZOOM_2_TAG =0x4;
	
	/** game data id
	 * GAME_ID = 0XAF
	 */
	public static final byte GAME_ID = (byte)0XAF;
	
	/**start touch configuration view key code*/
    public static final int KEYBOARD_START_TPCONFIG_INDEX = 2;
	public static final byte KEYBOARD_START_TPCONFIG = 0X65;
	
	public static final byte DPAD_KEY_RIGHT = 0X4F; //index = 3;
	public static final byte DPAD_KEY_LEFT = 0x50; //index = 3;
	public static final byte DPAD_KEY_UP = 0X52; //index = 3;
	public static final byte DPAD_KEY_DOWN = 0X51; //index = 3;
	public static final byte KEY_MENU = 0X65; //index = 3;
	public static final byte KEY_OK = 0X28; //index =3;
	public static final byte GAME_BUTTON_X = 0X08; //index = 5;
	public static final byte GAME_BUTTON_Y = 0X10; //index = 5;
	public static final byte GAME_BUTTON_L1= 0X40; //index=5
	public static final byte GAME_BUTTON_R1 = (byte) 0X80; //index=5
	public static final byte GAME_BUTTON_SELECT = 0X04; //index = 6
	public static final byte GAME_BUTTON_START = 0X08; //index = 6;
	//add by steven	
	public static final byte GAME_BUTTON_A = 0X01;
	public static final byte GAME_BUTTON_B = 0X02;
	
}
