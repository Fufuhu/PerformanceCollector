package jp.ne.perf.collector.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jp.ne.perf.collector.CPUCore;
import jp.ne.perf.collector.CPUPerformance;
import jp.ne.perf.collector.CPUPerformanceCollectorException;
import jp.ne.perf.collector.CPUPerformanceDAO;
import jp.ne.perf.collector.PerformanceCollectorException;
import jp.ne.perf.collector.PerformanceDAO;

import org.junit.Test;

public class PerformanceDAOTest {

	private static final String  HOSTNAME = "localhost";
	private static final int PORT = 1527;
	private static final String DATABASE = "PerformanceStore";
	private static final String USER = "APP";
	private static final String PASSWORD = "APP";

	@Test
	public void testGetCPUMetrics01() {
		PerformanceDAO dao = new PerformanceDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

		Date current = new Date();
		Timestamp ts_start = new Timestamp(current.getTime() - 30*60*1000);
		Timestamp ts_end = new Timestamp(current.getTime());
		String targethost = "192.168.0.3";
		String corename = "1";
		String metricname = "usr";

		try {
			CPUCore core = dao.getCPUMetrics(ts_start, ts_end, targethost, corename, metricname);
		} catch (PerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testInsertCPUMetricsTest01() {
		Date date = new Date();
		Timestamp ts_start = new Timestamp(date.getTime());
		Timestamp ts_end = new Timestamp(date.getTime() + 10L);

		CPUPerformanceDAO dao = new CPUPerformanceDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

		String targethost = "192.168.0.3";
		String corename = "1";
		int port = 1527;
		String metricname = "usr";
		Double metricvalue = 20.0d;

		dao.insertCPUMetrics(targethost, port , ts_start, corename, metricname, metricvalue);

		try {
			CPUCore core = dao.getCPUMetrics(ts_start, ts_end, HOSTNAME, corename, metricname);
			HashMap<Date, CPUPerformance> map = core.getPerformanceMap();
			Set<Date> set = map.keySet();

			Iterator<Date> it = set.iterator();
			Date d = it.next();
			CPUPerformance performance = map.get(d);
			Double acquiredvalue = performance.getMetric(metricname);

			assertEquals(metricvalue, acquiredvalue, 0.01d);
		} catch (CPUPerformanceCollectorException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
