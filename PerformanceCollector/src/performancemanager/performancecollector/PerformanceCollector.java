package performancemanager.performancecollector;


import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class PerformanceCollector {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		try {
			JSch jsch = new JSch();
			String host = "192.168.1.4";
			int port = 22;
			String user = "fujiwara";

			Session session = jsch.getSession(user, host, port);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			Channel channel = session.openChannel("exec");

			String command = "export LANG=C; mpstat -P ALL";
			((ChannelExec)channel).setCommand(command);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while(true) {
				while(in.available() > 0) {
					int i = in.read(tmp,0,1024);
					if(i <=  0) break;
					System.out.print(new String(tmp, 0, i));
				}
				if(channel.isClosed()) {
					if(in.available() <= 0) {
						System.out.println("exit-status: " + channel.getExitStatus());
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("Channel and Session is Disconnected correctly");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {

		@Override
		public String[] promptKeyboardInteractive(String arg0, String arg1,
				String arg2, String[] arg3, boolean[] arg4) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public String getPassphrase() {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public String getPassword() {
			// TODO 自動生成されたメソッド・スタブ
			return "fujiwara";
		}

		@Override
		public boolean promptPassphrase(String arg0) {
			// TODO 自動生成されたメソッド・スタブ
			return true;
		}

		@Override
		public boolean promptPassword(String arg0) {
			// TODO 自動生成されたメソッド・スタブ
			return true;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			// TODO 自動生成されたメソッド・スタブ
			return true;
		}

		@Override
		public void showMessage(String arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

	}

}
