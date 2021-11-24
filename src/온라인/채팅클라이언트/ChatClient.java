package �¶���.ä��Ŭ���̾�Ʈ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JPanel implements ActionListener, Runnable {
	// Ŭ���̾�Ʈ ȭ���
//	Container container = getContentPane();
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JTextField textField = new JTextField(20);
	// ��ſ�
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String str;
	String name;

	public ChatClient(String ip, int port, String name) {
		this.name = name;
		// Panel �⺻ ����
//		setBounds(0, 0, 300, 900);
//		setSize(200,200);
		init();
		start();
		setVisible(true);
		// ��� �ʱ�ȭ
		initNet(ip, port);
		System.out.println("ip = " + ip);
		
	}

	// ��� �ʱ�ȭ
	private void initNet(String ip, int port) {
		try {
			// ������ ���� �õ�
			socket = new Socket(ip, port);
			// ��ſ� input, output Ŭ���� ����
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// true�� auto flush ����
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
		} catch (UnknownHostException e) {
			System.out.println("IP �ּҰ� �ٸ��ϴ�.");
		} catch (IOException e) {
			System.out.println("���� ����");
		}
		// ������ ����
		Thread thread = new Thread(this);
		thread.start();
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.add("Center", scrollPane);
		this.add("South", textField);
		
	}

	private void start() {
		textField.setBackground(Color.black);
		textField.setForeground(Color.white);
		textArea.setBackground(Color.black);
		textArea.setForeground(Color.white);
		textField.addActionListener(this);
	}
	
	// ���� ���
	// �����κ��� ����Ǿ� ���޵� ���ڿ��� �о textArea�� ����ϱ�
	@Override
	public void run() {
		// �г��� ������ �����ش�.
		out.println(name);
		while(true) {
			try {
				str = in.readLine();
				textArea.append(str + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// textField�� ���ڿ��� �о�ͼ� ������ ������
		str = textField.getText();
		out.println(str);
		// textField �ʱ�ȭ
		textField.setText("");
	}

}
