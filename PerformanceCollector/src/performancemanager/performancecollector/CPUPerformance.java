package performancemanager.performancecollector;

import java.util.HashMap;

public class CPUPerformance {
	private HashMap<String, Double> metrics;

	public CPUPerformance() {
		metrics  = new HashMap<>();
	}

	public Double getMetric(String name) {
		return metrics.get(name);
	}
	public void putMetrics(String name, Double value) {
		metrics.put(name, value);
	}

	public HashMap<String, Double> getMetrics() {
		return metrics;
	}

	@Override
	public String toString() {
		return "CPU Performance:" + metrics.toString();
	}
}
