package �¶���.����Ŭ���̾�Ʈ;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.swing.JPanel;

import �¶���.Cell;

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

		while (true) { // ���� �ݺ�
			try {
				str = in.readLine();
				System.out.println("�Է¹��� : " + str);

				// ���� ���� ù ����
				if (str.indexOf("ù������ȸ") == 0) {
					String name = str.substring(str.indexOf("ù������ȸ") + 5);
					ClientMain.cDisplay("select * from cell where name = '" + name + "'");
				}
				// ���� ũ�� ��ȸ
				else if (str.indexOf("Ư��������ȸ") == 0) {
					String name = str.substring(str.indexOf("Ư��������ȸ") + 6);
					ClientMain.cDisplay("select * from cell where name = '" + name + "'");
				}
				// ��� ���� ��ȸ
				else if (str.indexOf("���������ȸ") == 0) {
					String name = str.substring(str.indexOf("���������ȸ") + 6);
					ClientMain.dDisplay("select * from cell where name = '" + name + "'");
				}
				// ���� ��ġ ��ȸ
				else if (str.indexOf("u") == 0) {
					String name = str.substring(str.indexOf("u") + 1, str.indexOf("x"));
					// �ڽ��� ������ �ƴ� �� ��ġ ��ȸ
					if (!name.equals(userName)) {
						ClientMain.moving(str);
					}
				}
				// ���� ��ġ ��ȸ
				else if (str.indexOf("Ư��������ȸ") == 0) {
					String name = str.substring(str.indexOf("Ư��������ȸ") + 6);
					ClientMain.pDisplay("select name,x,y from particle where name = '" + name + "'");
				}
				// ���� ���� ����
				else if (str.indexOf("����") == 0) {
					String name = str.substring(str.indexOf("����") + 2);
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						if (c.name.equals(name)) {
							Cell.cells.remove(c);
							break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
