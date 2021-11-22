package �¶���.ä�ü���;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSocketThread extends Thread{
	Socket socket;
	Server server;
	BufferedReader in;
	PrintWriter out;
	String name, threadName;
	
	public ServerSocketThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();
		System.out.println(socket.getInetAddress() + "���� �����Ͽ����ϴ�."); // ������ ����Ǿ� �ִ� ���ͳ� �ּ� ��ȯ
		System.out.println("Thread Name : " + threadName);
	}
	// Ŭ���̾�Ʈ �޽��� ���
	public void sendMessage(String str) {
		out.println(str);
	}
	// ������
	@Override
	public void run() {
		try {
			// ������ OutputStream�� ���� Ŭ���̾�Ʈ�� ������ ����.
			// Ŭ���̾�Ʈ�κ����� ������ InputStream�� ���� ����.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// true : autoFlush ����
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			
			sendMessage("��ȭ�� �̸��� ��������");
			name = in.readLine();
			server.broadCasting("["+name+"]���� �����ϼ̽��ϴ�.");

			while(true) {
				String str_in = in.readLine();
				server.broadCasting("["+name+"]"+str_in);
			}
		} catch (IOException e) {
			System.out.println(threadName + " �����߽��ϴ�.");
			server.broadCasting("["+name+"]���� �����߽��ϴ�.");
			server.removeClient(this);
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
