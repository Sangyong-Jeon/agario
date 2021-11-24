package 온라인.자주쓰는거;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

public class MyButton extends JButton {
	public MyButton(int x, int y, String name, ActionListener e, JLayeredPane pane) {
		setText(name);
		setSize(350, 60);
		setLocation(x, y);
		setFont(new Font("D2 Coding", Font.BOLD, 50));
		addActionListener(e);
		pane.add(this);
	}

}