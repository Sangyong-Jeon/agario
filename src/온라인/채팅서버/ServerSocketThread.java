package 온라인.채팅서버;

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
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다."); // 소켓이 연결되어 있는 인터넷 주소 반환
		System.out.println("Thread Name : " + threadName);
	}
	// 클라이언트 메시지 출력
	public void sendMessage(String str) {
		out.println(str);
	}
	// 쓰레드
	@Override
	public void run() {
		try {
			// 서버는 OutputStream을 통해 클라이언트에 정보를 보냄.
			// 클라이언트로부터의 정보는 InputStream을 통해 얻음.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// true : autoFlush 설정
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			
			sendMessage("대화자 이름을 넣으세요");
			name = in.readLine();
			server.broadCasting("["+name+"]님이 입장하셨습니다.");

			while(true) {
				String str_in = in.readLine();
				server.broadCasting("["+name+"]"+str_in);
			}
		} catch (IOException e) {
			System.out.println(threadName + " 퇴장했습니다.");
			server.broadCasting("["+name+"]님이 퇴장했습니다.");
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
