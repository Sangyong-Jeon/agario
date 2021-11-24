package 온라인.화면;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameMain extends JFrame {
	public CardLayout card;
	public Online online = new Online();
	public Offline offline = new Offline();
	public static int mWidth, mHeight;
	public int gameNameWidth;
	
	
	public GameMain() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // 모니터 사이즈
		mWidth = size.width;
		mHeight = size.height;
		FontMetrics fm = getFontMetrics(new Font("D2 Coding", Font.BOLD, 50)); // 설정한 폰트 정보 가져오기
		gameNameWidth = fm.stringWidth("세포키우기"); // 위 폰트로 설정된 문자열 전체 넓이 반환
		
		this.setTitle("세포키우기");
		this.setSize(mWidth, mHeight);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		card = new CardLayout(0, 0);
		this.setLayout(card);
		this.setLocation(mWidth/2 - this.getSize().width/2, mHeight/2 - this.getSize().height/2);
		
		this.add("main", new MainPane(this));
		this.add("mode", new SelectModePane(this));
		this.add("online", online);
		this.add("offline", offline);
		
		this.setVisible(true);
		
		
	}
	
	public static void main(String[] args) {
		new GameMain();
	}

}
