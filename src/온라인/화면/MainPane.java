package �¶���.ȭ��;

import javax.swing.JLayeredPane;

import �¶���.���־��°�.MyButton;
import �¶���.���־��°�.MyLabel;

public class MainPane extends JLayeredPane {
	public MainPane(GameMain frame) {
		MyLabel game_title = new MyLabel(frame.monitorWidth / 2 - frame.gameNameWidth / 2,
				frame.monitorHeight / 2 - 350, "����Ű���", this);

		MyButton mode = new MyButton(frame.monitorWidth / 2 - 175, 300, "��弱��",
				e -> frame.card.show(frame.getContentPane(), "mode"), this);
		MyButton end = new MyButton(frame.monitorWidth / 2 - 175, 500, "���� ������", e -> frame.dispose(), this);
	}

}
