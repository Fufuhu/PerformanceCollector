package performancemanager.performancecollector.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import performancemanager.performancecollector.CPUCore;
import performancemanager.performancecollector.CPUPerformance;
import performancemanager.performancecollector.exception.CPUPerformanceCollectorException;
import performancemanager.performancecollector.exception.PerformanceCollectorException;

public class CPUPerformanceDAO {

	private String hostname;
	private int port;
	private String user;
	private Connection connection;

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		/*
		 * コネクションをクローズするために、
		 * finalizeメソッドをオーバーライド
		 */
		connection.close();
	}


	private String password;
	private String database;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}




	/**
	 * DBへの接続情報を指定してインスタンス化する
	 * @param hostname DBのホスト名またはIPアドレス
	 * @param port DBのポート番号
	 * @param database DBの名前
	 * @param user DBへの接続ユーザ名
	 * @param password DBへの接続パスワード
	 */
	public CPUPerformanceDAO(String hostname, int port, String database, String user, String password) {
		this.setHostname(hostname);
		this.setPort(port);
		this.setUser(user);
		this.setPassword(password);
		this.setDatabase(database);
		this.connectDB();
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassword(String password) {
		this.password = password;
	}



	private static String DRIVER = "org.apache.derby.jdbc.ClientDriver";
	/**
	 * データベースに接続する。
	 * 設定情報はコンストラクタで指定したものに準拠する。
	 */
	private void connectDB() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby://"+this.getHostname()+":"+this.getPort()+"/"+this.getDatabase(), this.getUser(), this.getPassword());
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private static final String INSERT_METRIC_SQL ="INSERT INTO CpuMetrics values(?, ?, ?, ?, ?, ?)";


	/**
	 * CPUのメトリクス値をデータベースに投入する。
	 * @param hostname メトリック取得元のホスト名
	 * @param port メトリック取得元のポート番号
	 * @param timestamp 取得した日時を表すタイムスタンプ
	 * @param corename CPUコア名
	 * @param metricname メトリック名
	 * @param metricvalue メトリック値
	 */
	public void insertCPUMetrics(String hostname, int port, Timestamp timestamp, String corename, String metricname, double metricvalue) throws PerformanceCollectorException{
		try(PreparedStatement ps = connection.prepareStatement(INSERT_METRIC_SQL)) {
			/*
			 * パラメータを設定する。
			 */
			ps.setString(1, hostname);
			ps.setInt(2, port);
			ps.setTimestamp(3, timestamp);
			ps.setString(4, corename);
			ps.setString(5, metricname);
			ps.setDouble(6, metricvalue);

			/*
			 * SQLを実行する。
			 */
			ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new PerformanceCollectorException();
		}
	}

	//一定のタイムレンジのメトリクス値を取得するためのSQL
	private static final String SELECT_BETWEEN_SQL
	= "SELECT MetricName, AcquiredTimeStamp, MetricValue "
			+ "from CPUMetrics where AcquiredTimeStamp between ? AND ? "
			+ "AND HostName= ? AND PortNumber = ? AND CoreName = ?"
			+ " ORDER BY AcquiredTimeStamp";
	private static final String ACQUIRED_TIMESTAMP = "AcquiredTimeStamp";
	private static final String METRIC_NAME="MetricName";
	private static final String METRIC_VALUE = "MetricValue";

	/**
	 * 特定のCPUの指定した時間帯のメトリクス値を取得する
	 * @param start
	 * @param end
	 * @param hostname
	 * @param corename
	 * @param metricname
	 * @return 特定の時間帯のメトリクス値を含んだCPUCoreインスタンス
	 * @throws CPUPerformanceCollectorException
	 */
	public CPUCore getCPUMetrics(Timestamp start, Timestamp end, String hostname, int portnumber, String corename)  throws CPUPerformanceCollectorException {
		CPUCore core = new CPUCore(corename);

		try(PreparedStatement ps = connection.prepareStatement(SELECT_BETWEEN_SQL)) {



			/*
			 * パラメータを設定する。
			 */
			ps.setTimestamp(1, start);
			ps.setTimestamp(2, end);
			ps.setString(3, hostname);
			ps.setInt(4, portnumber);
			ps.setString(5, corename);

			/*
			 * SQLを実行する。
			 */
			ps.execute();

			ResultSet rs = ps.getResultSet();
			while(rs.next()) {
				Timestamp ats = rs.getTimestamp(ACQUIRED_TIMESTAMP);
				Double value = rs.getDouble(METRIC_VALUE);
				String metricname = rs.getString(METRIC_NAME);
				CPUPerformance performance = new CPUPerformance();
				performance.putMetrics(metricname, value);
				core.putPerformance(new Date(ats.getTime()), performance);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			throw new CPUPerformanceCollectorException();
		}

		return core;
	}


	private static final String TRUNCATE_CPU_METRICS_TABLE = "truncate table CPUMetrics";
	/**
	 *
	 * @throws PerformanceCollectorException
	 */
	public void truncateCPUMetricTable() throws CPUPerformanceCollectorException {
		try(PreparedStatement ps = connection.prepareStatement(TRUNCATE_CPU_METRICS_TABLE)) {
			ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			throw new CPUPerformanceCollectorException();
		}
	}

}
