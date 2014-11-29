package jp.co.nssol.sysrdc.systech.arc.perf.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;


public class CPUPerformanceCollector implements Runnable {
	public static void main(String[] args) {



		Thread thread = new Thread(new CPUPerformanceCollector("192.168.1.4", 22, "fujiwara", "fujiwara"));
		thread.start();
	}


	/*
	 * 格納先データベースの接続情報
	 */
	private static final String HOSTNAME = "localhost";
	private static final int PORT = 1527;
	private static final String DATABASE = "PerformanceStore";
	private static final String USER = "APP";
	private static final String PASSWORD = "APP";
	/*
	 * DAOの設定
	 */
	private static final PerformanceDAO DAO = new PerformanceDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

	private String host;
	private int port;
	private String user;
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}


	public CPUPerformanceCollector(String host, int port, String user, String password) {
		this.setHost(host);
		this.setPort(port);
		this.setUser(user);
		this.setPassword(password);
	}

	private static final String COMMAND = "export LANG=C; while : ; do mpstat -P ALL | sed -e \"/^$\\|Linux/d\"; sleep 1 ; done ";

	@Override
	public void run() {
		try {
			JSch jsch = new JSch();

			/*
			 * 接続情報のセットアップ
			 *
			 */
			Session session = jsch.getSession(user, host, port);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("exec");


			/*
			 * コマンド実行結果受け取りのためのチャンネルを開ける
			 */
			((ChannelExec)channel).setCommand(COMMAND);
			InputStream in = channel.getInputStream();
			channel.connect();
			/*
			 * BufferedReaderを使って確実に一行ずつ読み込む
			 */
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);



			insertMetrics(br);


			channel.disconnect();
			session.disconnect();
			System.out.println("Channel and Session is Disconnected correctly");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertMetrics(BufferedReader br) throws IOException{
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat clockformat = new SimpleDateFormat("HH:mm:ss");
		Timestamp time = null;
		Date date = null;
		String id = null;

		/*
		 * ヘッダ情報を読み取り
		 */
		String str = br.readLine();
		String[] header = str.split("\\s+");

		while((str = br.readLine()) != null) {
			String[] data = str.split("\\s+");
			for(int i = 0; i < data.length; i++) {
				if (i == 0) {
					date = new Date();
					try {
						System.err.println("date:"+dateformat.format(date));
						Date d = clockformat.parse(data[i]);
						System.err.println("d:"+clockformat.format(d));
						System.err.println("date+d:"+dateformat.format(date)+" "+clockformat.format(d));

						Date formatted = timeformat.parse(dateformat.format(date)+" "+clockformat.format(d));
						System.err.println(timeformat.format(formatted));
						time = new Timestamp(formatted.getTime());
					} catch (ParseException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					//System.out.println(timeformat.format(time));
				} else if (i == 1) {
					id = data[i];
				} else {
					try {
						Double value = Double.parseDouble(data[i]);
						DAO.insertCPUMetrics(this.getHost(), this.getPort(), time, id, header[i], value);
					} catch (NumberFormatException|PerformanceCollectorException e) {
						//e.printStackTrace();
						//System.err.println("想定内の例外: ヘッダ行の読み込み");
						continue;
					}
				}
			}
		}
	}


	private class MyUserInfo implements UserInfo, UIKeyboardInteractive {

		@Override
		public String[] promptKeyboardInteractive(String arg0, String arg1,
				String arg2, String[] arg3, boolean[] arg4) {
			return null;
		}

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptPassphrase(String arg0) {
			return true;
		}

		@Override
		public boolean promptPassword(String arg0) {
			return true;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			return true;
		}

		@Override
		public void showMessage(String arg0) {

		}

	}
}
