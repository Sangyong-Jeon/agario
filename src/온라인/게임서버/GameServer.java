package �¶���.���Ӽ���;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements Runnable {
	ServerSocket serverSocket;
	Socket socket;
	static List<Thread> list;

	public GameServer() {
		list = new ArrayList<Thread>();
		System.out.println("���Ӽ����� ���۵Ǿ����ϴ�.");
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(5556);
			serverSocket.setReuseAddress(true);

			while (true) {
				System.out.println("Ŭ���̾�Ʈ ���� ���!!!");
				socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ���
				GameSocketThread thread = new GameSocketThread(this, socket);
				addClient(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void addClient(GameSocketThread thread) {
		list.add(thread);
		System.out.println("Client 1�� ����. �� " + list.size() + "��");
	}

	public synchronized void removeClient(Thread thread) {
		list.remove(thread);
		System.out.println("Client 1�� ����. �� " + list.size() + "��");
	}

	public static synchronized void broadCasting(String str) {
		for (int i = 0; i < list.size(); i++) {
			GameSocketThread thread = (GameSocketThread) list.get(i);
			thread.sendMessage(str);
		}
	}

}
