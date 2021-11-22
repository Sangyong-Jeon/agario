package �����׽�Ʈ.ȭ��;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Login extends JFrame implements ActionListener  {
	
	JLabel label = new JLabel("�г����� �Է��ϼ���");
	JTextField textField = new JTextField(20);
	String str;
	
	public Login() {
		this.setLayout(new BorderLayout());
		this.setSize(400,200);
		this.add("Center", label);
		this.add("South", textField);
		this.setLocationRelativeTo(null); // â ��ġ �߾�
		setVisible(true);
		
		textField.setBackground(Color.black);
		textField.setForeground(Color.white);
		label.setOpaque(true); // JLabel ������ �⺻������ �����̶� Opaque���� �̸� ��������� ���� �����
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		textField.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// textField�� ���ڿ��� �о�ͼ� ������ ������
		str = textField.getText();
		this.setVisible(false);
		Online.nick = str;
		GameStart();
	}
	
	public void GameStart() { // �¶��� ���� ����
		Thread online = new Thread(new Online(), "ù��°");
		online.start();
	}
	
//	public static void main(String[] args) {
//		new Offline();
//	}

}
