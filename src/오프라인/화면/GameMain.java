package 오프라인.화면;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameMain extends JFrame{
	
	public CardLayout card = null;
	public Offline offline = new Offline();
	public static int monitorWidth, monitorHeight;
	public String gameName = "세포키우기";
	public int gameNameWidth;
	
	public GameMain() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // 화면 해상도 가져오기
		monitorWidth = size.width;
		monitorHeight = size.height;
		
		FontMetrics fm = getFontMetrics(new Font("D2 Coding", Font.BOLD, 50)); // 현재 설정된 폰트 정보 가져오기
		gameNameWidth = fm.stringWidth(gameName); // 저 폰트로 설정된 문자열 전체 넓이 반환
		
		setTitle("세포키우기");
		setSize(monitorWidth , monitorHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		card = new CardLayout(0, 0);
		setLayout(card);
		setLocation(monitorWidth/2 - this.getSize().width/2,
				monitorHeight/2 - this.getSize().height/2);
//		this.setLocationRelativeTo(null); // 창 위치 중앙
		
		add("main", new MainPane(this));
		add("mode", new SelectModePane(this));
		add("offline", offline );
		
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new GameMain();
		
			
	}

}
