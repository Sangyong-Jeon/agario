package 서버테스트.게임서버;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import 서버테스트.Cell;
import 서버테스트.Particle;

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
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다.");
		System.out.println("Thread Name : " + threadName);
	}

	// 클라이언트에게 메시지 출력
	public void sendMessage(String str) {
		out.println(str);
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// 클라이언트와 연결 되고나서 닉네임 받기
			name = in.readLine();
			System.out.println(name);

			// 닉네임 받고 DB에 세포 생성
//			Cell.cells.add(new Cell(name, (int) Math.floor(Math.random() * 5000), (int) Math.floor(Math.random() * 5000), 10));
			GameWorld.insertDB("cell", name, 100, 100, 10);
			// 생성 후 세포 조회
			GameWorld.cDisplay();
			GameServer.broadCasting("c");

			// 무한 반복
			while (true) {
				System.out.println("입력받기 전");
				str = in.readLine();
				System.out.println("서버가 문자받음 : " + str);
				// 조회하기
				if (str.equals("c")) { // 세포일 때 조회
					GameWorld.cDisplay();
					GameServer.broadCasting("c");
				} else if (str.equals("p")) { // 먹이일 때 조회
					GameWorld.pDisplay();
					GameServer.broadCasting("p");
				} else { // 잘못보내졌을 때
					System.out.println("무언가 잘못되고 있다.");
				}
			}
		} catch (IOException e) {
			System.out.println("입출력 예외 발생");
			System.out.println(threadName + " 퇴장했습니다. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " 퇴장했습니다. ");
					Cell.cells.remove(c);
					break;
				}
			}
			server.removeClient(this);
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null 예외 발생");
			System.out.println(threadName + " 퇴장했습니다. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " 퇴장했습니다. ");
					Cell.cells.remove(c);
					break;
				}
			}
		} finally {
			System.out.println("서버 통신 끝남");
			GameWorld.deleteDB(name);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
