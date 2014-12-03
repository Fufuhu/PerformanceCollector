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
import java.util.Set;

import org.junit.Test;

import performancemanager.performancecollector.CPUCore;
import performancemanager.performancecollector.CPUPerformance;
import performancemanager.performancecollector.CPUPerformanceCollectorException;
import performancemanager.performancecollector.CPUPerformanceDAO;
import performancemanager.performancecollector.PerformanceCollectorException;

public class PerformanceDAOTest {

	private static final String  HOSTNAME = "localhost";
	private static final int PORT = 1527;
	private static final String DATABASE = "PerformanceStore";
	private static final String USER = "APP";
	private static final String PASSWORD = "APP";

	private static CPUPerformanceDAO dao = new CPUPerformanceDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

	@Test
	public void testGetCPUMetrics01() {


		Date current = new Date();
		Timestamp ts_start = new Timestamp(current.getTime() - 30*60*1000);
		Timestamp ts_end = new Timestamp(current.getTime());
		String targethost = "192.168.0.3";
		int portnumber = 1527;
		String corename = "1";
		String metricname = "usr";

		try {
			CPUCore core = dao.getCPUMetrics(ts_start, ts_end, targethost, portnumber, corename);
		} catch (CPUPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * CPUMetricsの初期化機能をテストする。
	 */
	@Test
	public void testTruncateCPUTable01() {

		/*
		 * 挿入するデータの準備
		 */
		String targethost = "192.168.0.3";
		int port = 1527;
		String corename = "1";
		String metricname = "usr";
		Double metricValue = 20.0d;

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd hh:mm:ss");
		String dateString = "2014/12/01 23:05:05";
		Timestamp time = null;


		try {
			time = new Timestamp(sdf.parse(dateString).getTime());
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		 * CPUのメトリクス値を挿入する。
		 */
		dao.insertCPUMetrics(targethost, port , time, corename, metricname, metricValue);


		/*
		 * データ取得時のタイムレンジを設定する。
		 */
		String dateStringStart = "2014/12/01 23:00:00";
		String dateStringEnd = "2014/12/01 23:10:00";
		Timestamp timeStart = null;
		Timestamp timeEnd = null;

		try {
			timeStart = new Timestamp(sdf.parse(dateStringStart).getTime());
			timeEnd = new Timestamp(sdf.parse(dateStringEnd).getTime());
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		CPUCore core = null;
		try {
			core = dao.getCPUMetrics(timeStart, timeEnd, targethost, port, corename);
		} catch (CPUPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		HashMap<Date, CPUPerformance> map = core.getPerformanceMap();
		Collection<CPUPerformance> collection = map.values();

		/*
		 * この時点ではコレクションのサイズは0ではないはずなので
		 * サイズが0の場合はエラーとして処理する。
		 */
		if(collection.size() == 0) {
			fail();
		}

		try {
			dao.truncateCPUMetricTable();
		} catch (PerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			fail();
		}

		try {
			core = dao.getCPUMetrics(timeStart, timeEnd, targethost, port, corename);
		} catch (CPUPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


		/*
		 * truncate処理後のcollectionの取得
		 */
		map = core.getPerformanceMap();
		collection = map.values();
		assertThat(collection.size(), is(0));

	}

	/**
	 * CPUメトリクスを取得する。
	 * 複数のメトリクスを取得する。
	 */
	@Test
	public void testGetCPUMetrics03() {
		/*
		 * [前処理]
		 * CPUメトリクスを挿入する。
		 */

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		/*
		 * 2014/12/01 23:00:00 ～ 2014/12/01 23:59:59 までの時間を取得する。
		 */
		String dateStringLowerLimit_Outside = "2014/12/1 22:59:59"; // 開始時間外
		String dateStringLowerLimit_Inside  = "2014/12/1 23:00:00"; // 開始時間内
		String dateStringHigherLimit_Inside = "2014/12/1 23:59:59"; // 終了時間内
		String dateStringHigherLimit_Outside= "2014/12/2 00:00:00"; // 終了時間外

		/*
		 * Date型に文字列を変換
		 */
		Date dateLowerLimit_Outside = null;
		Date dateLowerLimit_Inside = null;
		Date dateHigherLimit_Inside = null;
		Date dateHigherLimit_Outside = null;
		try {
			dateLowerLimit_Outside = sdf.parse(dateStringLowerLimit_Outside);
			dateLowerLimit_Inside = sdf.parse(dateStringLowerLimit_Inside);
			dateHigherLimit_Inside = sdf.parse(dateStringHigherLimit_Inside);
			dateHigherLimit_Outside = sdf.parse(dateStringHigherLimit_Outside);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		 * Timestamp型に文字列を変換
		 */
		Timestamp tsLowerLimit_Outside = new Timestamp(dateLowerLimit_Outside.getTime());
		Timestamp tsLowerLimit_Inside = new Timestamp(dateLowerLimit_Inside.getTime());
		Timestamp tsHigherLimit_Inside = new Timestamp(dateHigherLimit_Inside.getTime());
		Timestamp tsHigherLimit_Outside = new Timestamp(dateHigherLimit_Outside.getTime());

		/*
		 * データを挿入する。
		 */
		String hostname = "192.168.0.3";
		String corename = "1";
		int port = 1527;
		String metricname = "usr";
		Double metricvalue = 20.0d;

		dao.insertCPUMetrics(hostname, port, tsLowerLimit_Outside, corename, metricname, metricvalue);
		dao.insertCPUMetrics(hostname, port, tsLowerLimit_Inside, corename, metricname, metricvalue);
		dao.insertCPUMetrics(hostname, port, tsHigherLimit_Inside, corename, metricname, metricvalue);
		dao.insertCPUMetrics(hostname, port, tsHigherLimit_Outside, corename, metricname, metricvalue);

		CPUCore core = null;
		try {
			core = dao.getCPUMetrics(tsLowerLimit_Inside, tsHigherLimit_Inside, hostname, port, corename);
		} catch (CPUPerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		HashMap<Date, CPUPerformance> map = core.getPerformanceMap();
		Collection<CPUPerformance> collection = map.values();
		int collection_length = collection.size();
		assertThat(collection_length, is(2));

		try {
			dao.truncateCPUMetricTable();
		} catch (PerformanceCollectorException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@Test
	public void testInsertCPUMetricsTest01() {
		Date date = new Date();
		Timestamp ts_start = new Timestamp(date.getTime());
		Timestamp ts_end = new Timestamp(date.getTime() + 10L);

		//CPUPerformanceDAO dao = new CPUPerformanceDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

		String targethost = "192.168.0.3";
		String corename = "1";
		int port = 1527;
		String metricname = "usr";
		Double metricvalue = 20.0d;

		dao.insertCPUMetrics(targethost, port, ts_start, corename, metricname, metricvalue);

		try {
			CPUCore core = dao.getCPUMetrics(ts_start, ts_end, targethost, port, corename);
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
