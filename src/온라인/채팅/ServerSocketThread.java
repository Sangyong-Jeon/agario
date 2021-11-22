package 온라인.채팅;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerSocketThread extends Thread {
	Socket socket;
	ChatServer server;
	BufferedReader in;
	PrintWriter out;
	String name;
	String threadName;
	
	public ServerSocketThread(ChatServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName(); // Thread 이름을 얻어옴
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다."); // IP 주소 얻어옴
		System.out.println("Thread Name : " + threadName);
	}
	// 클라이언트로 메시지 출력
	public void sendMessage(String str) {
		out.println(str);
	}
	// 쓰레드
	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// true : autoFlush 설정
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			
			sendMessage("대화자 이름을 넣으세요");
			name = in.readLine();
			server.broadCasting("["+name+"]님이 입장하셨습니다.");
			
			while(true) {
				String str_in = in.readLine();
				server.broadCasting("["+name+"]"+str_in);
			}
		} catch (IOException e) {
			System.out.println(threadName + " 퇴장했습니다.");
			server.broadCasting("["+name+"]님이 퇴장했습니다");
			server.removeClient(this);
			// e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
