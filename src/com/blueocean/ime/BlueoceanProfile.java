package com.blueocean.ime;

import java.util.List;

public class BlueoceanProfile {
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	public int key;
	public float posX; //中心点X
	public float posY; //中心点Y
	public float posR; //区域的半径
	public float posType; //区域类型：左摇杆，右摇杆
}
