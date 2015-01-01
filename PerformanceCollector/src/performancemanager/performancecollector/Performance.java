package performancemanager.performancecollector;

import java.util.HashMap;

public interface Performance {
	public Double getMetric(String metric);
	public void putMetrics(String metric, Double value);
	public HashMap<String, Double> getMetrics();
}
