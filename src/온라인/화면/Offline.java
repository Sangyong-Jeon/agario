package �¶���.ȭ��;

import javax.swing.JPanel;
import ��������.OfflineGame;

public class Offline extends JPanel implements Runnable{
	public Offline() {}
	
	public static void start() {
		Thread offline = new Thread(new Offline(), "�������θ��");
		offline.start();
	}
	
	@Override
	public void run() {
		new OfflineGame();
	}

}
