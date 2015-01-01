package performancemanager.performancecollector;

import java.util.Date;
import java.util.HashMap;


public class IODevice {
	private String deviveName;
	private HashMap<Date, IOPerformance> performanceMap;

	public String getDeviveName() {
		return deviveName;
	}

	public HashMap<Date, IOPerformance> getPerformanceMap() {
		return performanceMap;
	}

	public void setDeviveName(String deviveName) {
		this.deviveName = deviveName;
	}

	public void putPerformance(Date date, IOPerformance performance) {
		performanceMap.put(date, performance);
	}

	/**
	 * IODeviceを作成する
	 * @param name IOデバイス名
	 * @return
	 */
	public IODevice(String name) {
		this.deviveName = name;
		performanceMap = new HashMap<>();
	}


}
