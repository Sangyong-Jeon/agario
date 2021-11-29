package 온라인.화면;

import javax.swing.JPanel;
import 오프라인.OfflineGame;

public class Offline extends JPanel implements Runnable{
	public Offline() {}
	
	public static void start() {
		Thread offline = new Thread(new Offline(), "오프라인모드");
		offline.start();
	}
	
	@Override
	public void run() {
		new OfflineGame();
	}

}
