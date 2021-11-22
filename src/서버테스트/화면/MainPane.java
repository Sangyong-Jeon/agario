package 서버테스트.화면;

import javax.swing.JLayeredPane;

import 서버테스트.자주쓰는거.MyButton;
import 서버테스트.자주쓰는거.MyLabel;

public class MainPane extends JLayeredPane{
	public MainPane(GameMain frame) {
		MyLabel game_title = new MyLabel(frame.mWidth/2 - frame.gameNameWidth/2,
				frame.mHeight/2 - 350, "세포키우기", this);
		
		MyButton mode = new MyButton(frame.mWidth / 2 - 175, 300, "모드선택",
				e -> frame.card.show(frame.getContentPane(), "mode"), this);
		
		MyButton end = new MyButton(frame.mWidth / 2 - 175, 500, "게임 끝내기", e -> frame.dispose(), this);
		
	}
}
