package 서버테스트.화면;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Login extends JFrame implements ActionListener  {
	
	JLabel label = new JLabel("닉네임을 입력하세요");
	JTextField textField = new JTextField(20);
	String str;
	
	public Login() {
		this.setLayout(new BorderLayout());
		this.setSize(400,200);
		this.add("Center", label);
		this.add("South", textField);
		this.setLocationRelativeTo(null); // 창 위치 중앙
		setVisible(true);
		
		textField.setBackground(Color.black);
		textField.setForeground(Color.white);
		label.setOpaque(true); // JLabel 배경색이 기본적으로 투명이라 Opaque값을 미리 설정해줘야 배경색 적용됨
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		textField.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// textField의 문자열을 읽어와서 서버로 전송함
		str = textField.getText();
		this.setVisible(false);
		Online.nick = str;
		GameStart();
	}
	
	public void GameStart() { // 온라인 게임 시작
		Thread online = new Thread(new Online(), "첫번째");
		online.start();
	}
	
//	public static void main(String[] args) {
//		new Offline();
//	}

}
