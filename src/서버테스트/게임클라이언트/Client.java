package 서버테스트.게임클라이언트;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JPanel;

public class Client extends JPanel implements Runnable {
	// 통신용
	static Socket socket;
	static PrintWriter out;
	static BufferedReader in;
	String str, userName;

	ClientMain main;

	public Client(String ip, int port, ClientMain main, String name) {
		this.userName = name;
		this.main = main;
		System.out.println("Client의 userName : " + userName);
		init();
		this.setVisible(true);
		initNet(ip, port);
		System.out.println("ip = " + ip);
	}

	public void init() {
		this.setLayout(new BorderLayout());
	}

	public void initNet(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			System.out.println("IP 주소가 다릅니다.");
		} catch (IOException e) {
			System.out.println("접속 실패");
		}
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// 닉네임 보내주기
		out.println(userName);
		System.out.println("Client의 run()에서 userName : " + userName);
		main.cDisplay();

		main.isDB = true;

		while (true) { // 무한 반복
			try {
				System.out.println("클라이언트 입력받기 전");
				str = in.readLine();
				System.out.println("입력받음 : " + str);
				if (str.equals("c")) { // 세포문자 입력받으면 세포 조회
					System.out.println("클라이언트 세포 조회");
					main.cDisplay();
				} else if (str.equals("p")) { // 먹이문자 입력받으면 먹이 조회
					System.out.println("클라이언트 먹이 조회");
					main.pDisplay();
				} else {
					System.out.println("무엇인가 잘못되고 있다.");
				}
			} catch (IOException e) {
				System.out.println("IOException이 클라이언트에서 발생");
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("Null 예외  클라이언트에서 발생!!");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("예외 클라이언트에서 발생!!");
				e.printStackTrace();
			}
		}
	}

}
