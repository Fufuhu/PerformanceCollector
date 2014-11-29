package jp.co.nssol.sysrdc.systech.arc.perf.collector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class IOStatCollector implements Runnable{

	public static void main(String[] args)  {

		IOStatCollector collector = new IOStatCollector("192.168.1.3", 10022, "fujiwara", "fujiwara");
		Thread thread = new Thread(collector);
		thread.start();
	}


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

	private static String COMMAND = "export LANG=C; while : ; do date +\"%Y/%m/%d %k:%M:%S\"; iostat -dx | sed -e \"/^$\\|Linux/d\"; sleep 1; done";

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

			String str = null;
			while((str = br.readLine()) != null) {
				System.out.println(str);
			}

			//insertMetrics(br);


			channel.disconnect();
			session.disconnect();
			System.out.println("Channel and Session is Disconnected correctly");
		} catch (Exception e) {
			e.printStackTrace();
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
