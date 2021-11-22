package 온라인.자주쓰는거;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class MyLabel extends JLabel {
	public MyLabel(int x, int y, String text ,JLayeredPane pane) {
		setLocation(x, y);
		setSize(300, 50);
		setFont(new Font("D2 Coding", Font.BOLD, 50));
		setText(text);
		pane.add(this);
	}

}
