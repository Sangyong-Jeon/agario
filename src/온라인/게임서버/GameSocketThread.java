package �¶���.���Ӽ���;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import �¶���.Cell;

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
	public void run() { // ������ Ŭ���̾�Ʈ�� ��ȭ(���)�ϱ� ���� �ڵ�
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// Ŭ���̾�Ʈ�� ���� �ǰ��� �г��� �ް� DB ����
			name = in.readLine();
			System.out.println(name + "���� ���� ��ſ� �����Ͽ����ϴ�.");
			int x = (int) Math.floor(Math.random() * 5000);
			int y = (int) Math.floor(Math.random() * 5000);
			GameWorld.insertDB("insert into cell values('" + name + "'," + x + "," + y + ", 20)");

			// ��� ������ ������ ����Ʈ�� �߰��ϱ�
			GameWorld.cDisplay("select * from cell where name = '" + name + "'");

			// ������ �߰������� Ŭ���̾�Ʈ�鿡�Ե� ��ȸ�϶�� ��ȣ������
			GameServer.broadCasting("ù������ȸ" + name);

			// ���� �ݺ�
			while (true) {
				System.out.println("��ӽ����Ͽ� �Է¹ޱ���");
				str = in.readLine();
				System.out.println("������ ���ڹ��� : " + str);

				if (str.indexOf("u") == 0) { // ���� ������ ������ ��
					// �������� ���� �����̱�
					GameWorld.moving(str);
					// ��� Ŭ���̾�Ʈ���� ���� �����̱�
					GameServer.broadCasting(str);
				}
			}
		} catch (IOException e) {
			System.out.println("����� ���� �߻�");
			e.printStackTrace();
			System.out.println("���� ��� ����");
			// cells �迭 ����
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " �����߽��ϴ�. ");
					GameServer.broadCasting("����"+c.name);
					Cell.cells.remove(c);
					break;
				}
			}
			// DB���� ���� ����
			GameWorld.deleteDB("delete from cell where name = '" + name + "'");
			System.out.println(threadName + " �����߽��ϴ�. ");

			// ��� ����
			server.removeClient(this);

			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (NullPointerException e) {
			System.out.println("Null ���� �߻�");
			e.printStackTrace();
		} finally {
			System.out.println("��ӽ����ϳ�???");
		}
	}
}
