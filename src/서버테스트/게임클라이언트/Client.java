package �����׽�Ʈ.����Ŭ���̾�Ʈ;

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
	// ��ſ�
	static Socket socket;
	static PrintWriter out;
	static BufferedReader in;
	String str, userName;

	ClientMain main;

	public Client(String ip, int port, ClientMain main, String name) {
		this.userName = name;
		this.main = main;
		System.out.println("Client�� userName : " + userName);
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
			System.out.println("IP �ּҰ� �ٸ��ϴ�.");
		} catch (IOException e) {
			System.out.println("���� ����");
		}
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// �г��� �����ֱ�
		out.println(userName);
		System.out.println("Client�� run()���� userName : " + userName);
		main.cDisplay();

		main.isDB = true;

		while (true) { // ���� �ݺ�
			try {
				System.out.println("Ŭ���̾�Ʈ �Է¹ޱ� ��");
				str = in.readLine();
				System.out.println("�Է¹��� : " + str);
				if (str.equals("c")) { // �������� �Է¹����� ���� ��ȸ
					System.out.println("Ŭ���̾�Ʈ ���� ��ȸ");
					main.cDisplay();
				} else if (str.equals("p")) { // ���̹��� �Է¹����� ���� ��ȸ
					System.out.println("Ŭ���̾�Ʈ ���� ��ȸ");
					main.pDisplay();
				} else {
					System.out.println("�����ΰ� �߸��ǰ� �ִ�.");
				}
			} catch (IOException e) {
				System.out.println("IOException�� Ŭ���̾�Ʈ���� �߻�");
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("Null ����  Ŭ���̾�Ʈ���� �߻�!!");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("���� Ŭ���̾�Ʈ���� �߻�!!");
				e.printStackTrace();
			}
		}
	}

}
