package performancemanager.performancecollector;

import java.util.HashMap;

public class IODeviceMap {
	private HashMap<String, IODevice> map;

	public HashMap<String, IODevice> getMap() {
		return map;
	}
	public IODeviceMap() {
		map = new HashMap<>();
	}

	public IODevice getDevice(String id) {
		return map.get(id);
	}

}
