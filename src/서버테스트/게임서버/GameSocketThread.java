package �����׽�Ʈ.���Ӽ���;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;

public class GameSocketThread extends Thread {
	Socket socket;
	GameServer server;
	BufferedReader in;
	PrintWriter out;
	String name, threadName;
	String str;

	public GameSocketThread(GameServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();
		System.out.println(socket.getInetAddress() + "���� �����Ͽ����ϴ�.");
		System.out.println("Thread Name : " + threadName);
	}

	// Ŭ���̾�Ʈ���� �޽��� ���
	public void sendMessage(String str) {
		out.println(str);
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// Ŭ���̾�Ʈ�� ���� �ǰ��� �г��� �ޱ�
			name = in.readLine();
			System.out.println(name);

			// �г��� �ް� DB�� ���� ����
//			Cell.cells.add(new Cell(name, (int) Math.floor(Math.random() * 5000), (int) Math.floor(Math.random() * 5000), 10));
			GameWorld.insertDB("cell", name, 100, 100, 10);
			// ���� �� ���� ��ȸ
			GameWorld.cDisplay();
			GameServer.broadCasting("c");

			// ���� �ݺ�
			while (true) {
				System.out.println("�Է¹ޱ� ��");
				str = in.readLine();
				System.out.println("������ ���ڹ��� : " + str);
				// ��ȸ�ϱ�
				if (str.equals("c")) { // ������ �� ��ȸ
					GameWorld.cDisplay();
					GameServer.broadCasting("c");
				} else if (str.equals("p")) { // ������ �� ��ȸ
					GameWorld.pDisplay();
					GameServer.broadCasting("p");
				} else { // �߸��������� ��
					System.out.println("���� �߸��ǰ� �ִ�.");
				}
			}
		} catch (IOException e) {
			System.out.println("����� ���� �߻�");
			System.out.println(threadName + " �����߽��ϴ�. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " �����߽��ϴ�. ");
					Cell.cells.remove(c);
					break;
				}
			}
			server.removeClient(this);
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null ���� �߻�");
			System.out.println(threadName + " �����߽��ϴ�. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " �����߽��ϴ�. ");
					Cell.cells.remove(c);
					break;
				}
			}
		} finally {
			System.out.println("���� ��� ����");
			GameWorld.deleteDB(name);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
