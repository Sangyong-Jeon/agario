package �����׽�Ʈ.ȭ��;

import javax.swing.JPanel;

import �����׽�Ʈ.����Ŭ���̾�Ʈ.ClientMain;

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
		System.out.println("Online�� run()�� nick : " + nick);
	}

}
