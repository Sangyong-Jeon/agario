package ��������.ȭ��;

import javax.swing.JPanel;

import ��������.Game;



public class Offline extends JPanel implements Runnable {
	
	public Offline() {
		
	}

	public static void start() {
		Thread offline = new Thread(new Offline(), "ù��°");
		offline.start();
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		new Game();
		
	}

}
