package com.blueocean.ime;

public class BlueoceanAccelerationManager {
	static {
		try {
			System.loadLibrary("jnsacc");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	public static int fd = 0;
	public static native int openDevice();
	public static native boolean setInstallDirection(int fd, int installDir);
	public static native boolean setDelay(int fd , int delay);
	public static native boolean setData(int fd, int data[]);
	public static native void closeDevice(int fd);
}
