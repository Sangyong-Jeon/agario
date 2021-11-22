package �¶���.ä�ü���;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	ServerSocket serverSocket; // ���� ����, ������ Ŭ���̾�Ʈ�� ���� ������ ��ٸ��� ���� �� �ִ� ����
	Socket socket; // Ŭ���̾�Ʈ ����
	List<Thread> list;
	
	public Server() {
		list = new ArrayList<Thread>();
		System.out.println("������ ���۵Ǿ����ϴ�.");
	}
	
	public void giveAndTake() {
		try {
			serverSocket = new ServerSocket(5555); // ��Ʈ��ȣ 5555���� ���� ServerSocket�� ���ο� �ν��Ͻ��� ����.
			serverSocket.setReuseAddress(true); // ServerSocket�� port�� �ٷ� �ٽ� ����ϴ� ����(port�� �������)
			
			while(true) {
				socket = serverSocket.accept(); // public Socket accept()�� ���� ��û�� �޴´ٴ� ��. 
												// Ŭ���̾�Ʈ�� ������ �Ǹ� ���ο� Socket ��ü�� ��ȯ��. ��Socket ��ü�� �̿��� ������ Ŭ���̾�Ʈ�� ��ȣ��ȭ ����.
												// ��, Ŭ���̾�Ʈ�� ���۵Ǿ� ȣ��Ʈ�� ���Ͽ� ��û�� ������ ��ٸ��ٴ� �ǹ�.
				ServerSocketThread thread = new ServerSocketThread(this, socket); // this -> Server �ڽ�
				addClient(thread);
				thread.start();
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// synchronize : ��������� ���� �����͸� �Բ� ������� ���ϵ��� �ϴ� ��
	// Ŭ���̾�Ʈ ���� �� ȣ��, list�� Ŭ���̾�Ʈ ��� ������ ����
	private synchronized void addClient(ServerSocketThread thread) {
		// ����Ʈ�� ServerSocketThread ��ü ����
		list.add(thread);
		System.out.println("Client 1�� ����. �� " + list.size() + "��");
	}
	
	// Ŭ���̾�Ʈ�� ����� ȣ��, list�� Ŭ���̾�Ʈ ��� ������ ����
	public synchronized void removeClient(Thread thread) {
		list.remove(thread);
		System.out.println("Client 1�� ����. �� " + list.size() + "��");
	}
	
	// ��� Ŭ���̾�Ʈ���� ä�� ���� ����
	public synchronized void broadCasting(String str) {
		for (int i = 0; i < list.size(); i++) {
			ServerSocketThread thread = (ServerSocketThread) list.get(i);
			thread.sendMessage(str);
		}
	}

	
}
