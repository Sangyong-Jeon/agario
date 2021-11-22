package 온라인.채팅서버;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	ServerSocket serverSocket; // 서버 소켓, 서버가 클라이언트에 대한 연결을 기다리고 받을 수 있는 소켓
	Socket socket; // 클라이언트 소켓
	List<Thread> list;
	
	public Server() {
		list = new ArrayList<Thread>();
		System.out.println("서버가 시작되었습니다.");
	}
	
	public void giveAndTake() {
		try {
			serverSocket = new ServerSocket(5555); // 포트번호 5555번에 대해 ServerSocket의 새로운 인스턴스를 만듬.
			serverSocket.setReuseAddress(true); // ServerSocket이 port를 바로 다시 사용하는 설정(port를 잡고있음)
			
			while(true) {
				socket = serverSocket.accept(); // public Socket accept()는 접속 요청을 받는다는 뜻. 
												// 클라이언트와 연결이 되면 새로운 Socket 객체를 반환함. 이Socket 객체를 이용해 서버는 클라이언트와 상호대화 가능.
												// 즉, 클라이언트가 시작되어 호스트에 대하여 요청할 때까지 기다린다는 의미.
				ServerSocketThread thread = new ServerSocketThread(this, socket); // this -> Server 자신
				addClient(thread);
				thread.start();
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// synchronize : 쓰레드들이 공유 데이터를 함께 사용하지 못하도록 하는 것
	// 클라이언트 입장 시 호출, list에 클라이언트 담당 쓰레드 저장
	private synchronized void addClient(ServerSocketThread thread) {
		// 리스트에 ServerSocketThread 객체 저장
		list.add(thread);
		System.out.println("Client 1명 입장. 총 " + list.size() + "명");
	}
	
	// 클라이언트가 퇴장시 호출, list에 클라이언트 담당 쓰레드 제거
	public synchronized void removeClient(Thread thread) {
		list.remove(thread);
		System.out.println("Client 1명 퇴장. 총 " + list.size() + "명");
	}
	
	// 모든 클라이언트에게 채팅 내용 전달
	public synchronized void broadCasting(String str) {
		for (int i = 0; i < list.size(); i++) {
			ServerSocketThread thread = (ServerSocketThread) list.get(i);
			thread.sendMessage(str);
		}
	}

	
}
