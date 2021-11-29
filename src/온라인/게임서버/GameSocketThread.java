package 온라인.게임서버;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import 온라인.Cell;

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
	public void run() { // 서버가 클라이언트와 대화(통신)하기 위한 코드
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// 클라이언트와 연결 되고나서 닉네임 받고 DB 생성
			name = in.readLine();
			System.out.println(name + "님이 서버 통신에 접속하였습니다.");
			int x = (int) Math.floor(Math.random() * 5000);
			int y = (int) Math.floor(Math.random() * 5000);
			GameWorld.insertDB("insert into cell values('" + name + "'," + x + "," + y + ", 20)");

			// 방금 생성한 세포를 리스트에 추가하기
			GameWorld.cDisplay("select * from cell where name = '" + name + "'");

			// 세포를 추가했으니 클라이언트들에게도 조회하라고 신호보내기
			GameServer.broadCasting("첫세포조회" + name);

			// 무한 반복
			while (true) {
				System.out.println("계속실행하여 입력받기전");
				str = in.readLine();
				System.out.println("서버가 문자받음 : " + str);

				if (str.indexOf("u") == 0) { // 유저 세포가 움직일 때
					// 서버에서 세포 움직이기
					GameWorld.moving(str);
					// 모든 클라이언트에서 세포 움직이기
					GameServer.broadCasting(str);
				}
			}
		} catch (IOException e) {
			System.out.println("입출력 예외 발생");
			e.printStackTrace();
			System.out.println("서버 통신 끝남");
			// cells 배열 삭제
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " 퇴장했습니다. ");
					GameServer.broadCasting("퇴장"+c.name);
					Cell.cells.remove(c);
					break;
				}
			}
			// DB에서 세포 삭제
			GameWorld.deleteDB("delete from cell where name = '" + name + "'");
			System.out.println(threadName + " 퇴장했습니다. ");

			// 통신 끊기
			server.removeClient(this);

			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (NullPointerException e) {
			System.out.println("Null 예외 발생");
			e.printStackTrace();
		} finally {
			System.out.println("계속실행하나???");
		}
	}
}
