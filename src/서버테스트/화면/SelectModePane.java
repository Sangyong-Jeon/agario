package �����׽�Ʈ.ȭ��;

import javax.swing.JLayeredPane;

import �¶���.���־��°�.MyButton;
import �¶���.���־��°�.MyLabel;

public class SelectModePane extends JLayeredPane {
	public SelectModePane(GameMain frame) {
		MyLabel game_title = new MyLabel(frame.mWidth / 2 - frame.gameNameWidth / 2,
				frame.mHeight / 2 - 350, "����Ű���", this);
		
		MyButton onlineMode = new MyButton(frame.mWidth / 2 - 175, 300, "�¶���", e -> {
			frame.setVisible(false);
			frame.online.start();
		}, this);

		MyButton offlineMode = new MyButton(frame.mWidth / 2 - 175, 450, "��������", e -> {
//			frame.card.show(frame.getContentPane(), "offline");
//			frame.setVisible(false);
//			frame.offline.start();
		}, this);

		MyButton main = new MyButton(frame.mWidth / 2 - 175, 600, "��������", e -> {
			frame.card.show(frame.getContentPane(), "main");
		}, this);
	}

}
