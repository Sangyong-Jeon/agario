package 오프라인;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	OfflineGame game;

	public KeyInput(OfflineGame game) {
		this.game = game;
	}

	public void keyPressed(KeyEvent e) {
		game.keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		game.keyReleased(e);
	}

}