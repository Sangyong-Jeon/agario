package 오프라인.화면;

import javax.swing.JPanel;

import 오프라인.Game;



public class Offline extends JPanel implements Runnable {
	
	public Offline() {
		
	}

	public static void start() {
		Thread offline = new Thread(new Offline(), "첫번째");
		offline.start();
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Game();
		
	}

}
