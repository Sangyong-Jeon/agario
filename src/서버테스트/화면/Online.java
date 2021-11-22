package 서버테스트.화면;

import javax.swing.JPanel;

import 서버테스트.게임클라이언트.ClientMain;

public class Online extends JPanel implements Runnable {
	public static String nick;
	public ClientMain client;

	public Online() {
	}

	public static void start() {
		Login login = new Login();
	}

	@Override
	public void run() {
		client = new ClientMain(nick);
		System.out.println("Online의 run()의 nick : " + nick);
	}

}
