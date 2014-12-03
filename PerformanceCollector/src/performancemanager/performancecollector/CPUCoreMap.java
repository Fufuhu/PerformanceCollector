package performancemanager.performancecollector;

import java.util.HashMap;

public class CPUCoreMap {
	private HashMap<String, CPUCore> map;
	public HashMap<String, CPUCore> getMap() {
		return map;
	}
	public CPUCoreMap() {
		map = new HashMap<>();
	}

	public CPUCore getCPUCore(String id) {
		return map.get(id);
	}

}
