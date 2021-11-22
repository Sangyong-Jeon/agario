package 온라인.화면;

import javax.swing.JLayeredPane;

import 온라인.자주쓰는거.MyButton;
import 온라인.자주쓰는거.MyLabel;

public class SelectModePane extends JLayeredPane {
	public SelectModePane(GameMain frame) {
		MyLabel game_title = new MyLabel(frame.monitorWidth / 2 - frame.gameNameWidth / 2,
				frame.monitorHeight / 2 - 350, "세포키우기", this);
		
		MyButton onlineMode = new MyButton(frame.monitorWidth / 2 - 175, 300, "온라인", e -> {}, this);

		MyButton offlineMode = new MyButton(frame.monitorWidth / 2 - 175, 450, "오프라인", e -> {
			frame.card.show(frame.getContentPane(), "offline");
			frame.setVisible(false);
			frame.offline.start();
		}, this);

		MyButton main = new MyButton(frame.monitorWidth / 2 - 175, 600, "메인으로", e -> {
			frame.card.show(frame.getContentPane(), "main");
		}, this);
	}
}
