package performancemanager.performancecollector.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import performancemanager.performancecollector.IODevice;
import performancemanager.performancecollector.IOPerformance;
import performancemanager.performancecollector.dao.IOStatDAO;
import performancemanager.performancecollector.exception.IOPerformanceCollectorException;

public class IOStatDAOTest {

	private static final String  HOSTNAME = "localhost";
	private static final int PORT = 1527;
	private static final String DATABASE = "PerformanceStore";
	private static final String USER = "APP";
	private static final String PASSWORD = "APP";

	private static IOStatDAO DAO = new IOStatDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

	@Test
	public void truncateIOStatMetricsTest01() {
		try {
			DAO.truncateIOStatMetricsTable();
		} catch (IOPerformanceCollectorException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void insertMetricsTest01() {

		try {
			DAO.truncateIOStatMetricsTable();
		} catch (IOPerformanceCollectorException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		String hostname = "MetricsTest01";
		int port = 1527;
		String devicename = "sda1";
		String metricname = "kBRead/s";
		Double metricvalue = 12.5;


		String pattern = "yyyy/MM/dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		String timeString = "2014/12/06 23:15:25";
		Date date = null;
		try {
			date = sdf.parse(timeString);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		Timestamp timestamp = new Timestamp(date.getTime());

		DAO.insertMetrics(hostname, port, timestamp, devicename, metricname, metricvalue);

		String startString = "2014/12/06 23:00:00";
		String endString = "2014/12/06 23:59:59";

		Timestamp start = null;
		Timestamp end = null;
		try {
			date = sdf.parse(startString);
			start = new Timestamp(date.getTime());
			date = sdf.parse(endString);
			end = new Timestamp(date.getTime());
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		IODevice device = null;
		try {
			device = DAO.getIOMetrics(start, end, hostname, port, devicename);
		} catch (IOPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		HashMap<Date, IOPerformance> map = device.getPerformanceMap();
		Collection<Date> dateCollection = map.keySet();
		Collection<IOPerformance> perfCollection =  map.values();

		assertThat(dateCollection.size(), is(1));
		assertThat(perfCollection.size(), is(1));


		long timestampLong = timestamp.getTime();
		Iterator<Date> itDate = dateCollection.iterator();

		/*
		 * パフォーマンス情報の時刻を比較
		 */
		while(itDate.hasNext()) {
			Date d = itDate.next();
			assertThat(d.getTime(), is(timestampLong));
		}

		/*
		 * パフォーマンス情報のメトリック名・値を比較
		 */
		Iterator<IOPerformance> itPerf = perfCollection.iterator();
		while(itPerf.hasNext()) {
			IOPerformance perf = itPerf.next();
			HashMap<String, Double> perfMap = perf.getMetrics();
			assertThat(perfMap.size(), is(1));
			assertThat(perfMap.containsKey(metricname), is(true));
			Double value = perfMap.get(metricname);
			assertEquals(metricvalue, value, 0.01);
		}
		try {
			DAO.truncateIOStatMetricsTable();
			System.out.println("Truncated");
		} catch (IOPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}



}
