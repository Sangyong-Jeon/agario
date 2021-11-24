package 온라인.채팅클라이언트;

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
	// 클라이언트 화면용
//	Container container = getContentPane();
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JTextField textField = new JTextField(20);
	// 통신용
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String str;
	String name;

	public ChatClient(String ip, int port, String name) {
		this.name = name;
		// Panel 기본 설정
//		setBounds(0, 0, 300, 900);
//		setSize(200,200);
		init();
		start();
		setVisible(true);
		// 통신 초기화
		initNet(ip, port);
		System.out.println("ip = " + ip);
		
	}

	// 통신 초기화
	private void initNet(String ip, int port) {
		try {
			// 서버에 접속 시도
			socket = new Socket(ip, port);
			// 통신용 input, output 클래스 설정
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// true는 auto flush 설정
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
		} catch (UnknownHostException e) {
			System.out.println("IP 주소가 다릅니다.");
		} catch (IOException e) {
			System.out.println("접속 실패");
		}
		// 쓰레드 구동
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
	
	// 응답 대기
	// 서버로부터 응답되어 전달된 문자열을 읽어서 textArea에 출력하기
	@Override
	public void run() {
		// 닉네임 서버에 보내준다.
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
		// textField의 문자열을 읽어와서 서버로 전송함
		str = textField.getText();
		out.println(str);
		// textField 초기화
		textField.setText("");
	}

}
