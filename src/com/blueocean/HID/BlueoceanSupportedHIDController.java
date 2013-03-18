package com.blueocean.HID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueoceanSupportedHIDController {
	
	public static List<Map<String, String>> controllerList = new ArrayList<Map<String, String>>();
	static {
		Map<String, String > map = new HashMap<String, String>();
		map.put("vendor_id", "43045");
		map.put("product_id", "13092");
		controllerList.add(map);
	}
	
}
