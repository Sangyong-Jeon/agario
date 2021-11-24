package 온라인.게임서버;

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
		System.out.println("게임서버가 시작되었습니다.");
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(5556);
			serverSocket.setReuseAddress(true);

			while (true) {
				System.out.println("클라이언트 접속 대기!!!");
				socket = serverSocket.accept(); // 클라이언트 접속 대기
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
		System.out.println("Client 1명 입장. 총 " + list.size() + "명");
	}

	public synchronized void removeClient(Thread thread) {
		list.remove(thread);
		System.out.println("Client 1명 퇴장. 총 " + list.size() + "명");
	}

	public static synchronized void broadCasting(String str) {
		for (int i = 0; i < list.size(); i++) {
			GameSocketThread thread = (GameSocketThread) list.get(i);
			thread.sendMessage(str);
		}
	}

}
