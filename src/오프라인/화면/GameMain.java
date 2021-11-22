package ��������.ȭ��;

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
	public String gameName = "����Ű���";
	public int gameNameWidth;
	
	public GameMain() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // ȭ�� �ػ� ��������
		monitorWidth = size.width;
		monitorHeight = size.height;
		
		FontMetrics fm = getFontMetrics(new Font("D2 Coding", Font.BOLD, 50)); // ���� ������ ��Ʈ ���� ��������
		gameNameWidth = fm.stringWidth(gameName); // �� ��Ʈ�� ������ ���ڿ� ��ü ���� ��ȯ
		
		setTitle("����Ű���");
		setSize(monitorWidth , monitorHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		card = new CardLayout(0, 0);
		setLayout(card);
		setLocation(monitorWidth/2 - this.getSize().width/2,
				monitorHeight/2 - this.getSize().height/2);
//		this.setLocationRelativeTo(null); // â ��ġ �߾�
		
		add("main", new MainPane(this));
		add("mode", new SelectModePane(this));
		add("offline", offline );
		
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new GameMain();
		
			
	}

}
