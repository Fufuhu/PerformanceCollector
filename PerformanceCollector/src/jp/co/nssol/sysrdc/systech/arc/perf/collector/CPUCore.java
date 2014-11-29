package jp.co.nssol.sysrdc.systech.arc.perf.collector;

import java.util.Date;
import java.util.HashMap;

public class CPUCore {
	private String id;
	private HashMap<Date, CPUPerformance> performanceMap;

	public CPUCore(String id) {
		this.id = id;
		this.performanceMap = new HashMap<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<Date, CPUPerformance> getPerformanceMap() {
		return performanceMap;
	}

	public void putPerformance(Date date, CPUPerformance performance) {
		performanceMap.put(date, performance);
	}



}
