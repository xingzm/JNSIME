package com.blueocean.ime;

public class BlueoceanMotionManager {
	static {
		try {
			System.loadLibrary("jnsmotion");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	public static int fd;
	public static native int openDevice();
	public static native void injectMotionEventDown(int fd, int fingetId, float x, float y);
	public static native void injectMotionEventUp(int fd, int fingerId);
	public static native void closeDevice(int fd);
}
