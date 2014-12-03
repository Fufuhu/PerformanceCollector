package performancemanager.performancecollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import performancemanager.performancecollector.dao.IOStatDAO;
import performancemanager.performancecollector.exception.IOStatHeaderException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class IOStatCollector implements Runnable{

	public static void main(String[] args)  {

		IOStatCollector collector = new IOStatCollector("192.168.1.4", 22, "fujiwara", "fujiwara");
		Thread thread = new Thread(collector);
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
	private static final IOStatDAO DAO = new IOStatDAO(HOSTNAME, PORT, DATABASE, USER, PASSWORD);

	private String host;
	private int port;
	private String user;
	private String password;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public IOStatCollector(String host, int port, String user, String password) {
		this.setHost(host);
		this.setPort(port);
		this.setUser(user);
		this.setPassword(password);
	}

	private static String COMMAND = "export LANG=C; while : ; do date +\"%Y/%m/%d %k:%M:%S\"; iostat -dx | sed -e \"/^$\\|Linux/d\" ; sleep 1; done";

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


			/*
			String str = null;
			while((str = br.readLine()) != null) {
				System.out.println(str);
			}*/

			insertMetrics(br);


			channel.disconnect();
			session.disconnect();
			System.out.println("Channel and Session is Disconnected correctly");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * メトリクスを挿入する。
	 * @param br
	 */
	public void insertMetrics(BufferedReader br) {
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String line = null;

		try {
			while((line = br.readLine()) != null) {
				String[] array = line.split("\\s+");

				String[] header = null;
				/*
				 * array[0] array[1] が yyyy/MM/dd HH:mm:ssの形態 → 時刻処理へ
				 * array[0] が Device: → メトリクスのヘッダ処理へ
				 * 上記に当てはまらない → 実際のinsert処理へ
				 */

				try {
					Date time = timeformat.parse(array[0]+" "+array[1]);
				} catch (ParseException e) {
					if(array[0].equals("Device:")) {
						header = array;
					} else {
						try {
							collectIOStatData(array, header);
						} catch (IOStatHeaderException e1) {
							// TODO 自動生成された catch ブロック
							e1.printStackTrace();
						}
					}

				}

				/*
				for(String col:array) {
					System.out.println(col);
				}*/
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	private static final String HEADER_EXCEPTION_MESSAGE = "ヘッダの配列長がメトリクスの配列長と一致しません。";

	private void collectIOStatData(String[] array, String[] header) throws IOStatHeaderException {
		// TODO 自動生成されたメソッド・スタブ
		if(array.length != header.length) {
			throw new IOStatHeaderException(HEADER_EXCEPTION_MESSAGE);
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
