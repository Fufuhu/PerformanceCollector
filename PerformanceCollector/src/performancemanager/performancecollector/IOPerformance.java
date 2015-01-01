package performancemanager.performancecollector;

import java.util.HashMap;

public class IOPerformance implements Performance {

	HashMap<String, Double> map;

	public IOPerformance() {
		map = new HashMap<>();
	}
	@Override
	public Double getMetric(String metric) {
		// TODO 自動生成されたメソッド・スタブ
		return map.get(metric);
	}

	@Override
	public void putMetrics(String metric, Double value) {
		map.put(metric, value);
	}

	@Override
	public HashMap<String, Double> getMetrics() {
		return map;
	}

}
