package 온라인.게임클라이언트;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.swing.JPanel;

import 온라인.Cell;

public class Client extends JPanel implements Runnable {
	// 통신용
	static Socket socket;
	static PrintWriter out;
	static BufferedReader in;
	String str, userName;

	ClientMain main;

	public Client(String ip, int port, ClientMain main, String name) {
		this.userName = name;
		this.main = main;
		System.out.println("Client의 userName : " + userName);
		init();
		this.setVisible(true);
		initNet(ip, port);
		System.out.println("ip = " + ip);
	}

	public void init() {
		this.setLayout(new BorderLayout());
	}

	public void initNet(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			System.out.println("IP 주소가 다릅니다.");
		} catch (IOException e) {
			System.out.println("접속 실패");
		}
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// 닉네임 보내주기
		out.println(userName);
		System.out.println("Client의 run()에서 userName : " + userName);

		while (true) { // 무한 반복
			try {
				str = in.readLine();
				System.out.println("입력받음 : " + str);

				// 나의 세포 첫 생성
				if (str.indexOf("첫세포조회") == 0) {
					String name = str.substring(str.indexOf("첫세포조회") + 5);
					ClientMain.cDisplay("select * from cell where name = '" + name + "'");
				}
				// 세포 크기 조회
				else if (str.indexOf("특정세포조회") == 0) {
					String name = str.substring(str.indexOf("특정세포조회") + 6);
					ClientMain.cDisplay("select * from cell where name = '" + name + "'");
				}
				// 사망 세포 조회
				else if (str.indexOf("사망세포조회") == 0) {
					String name = str.substring(str.indexOf("사망세포조회") + 6);
					ClientMain.dDisplay("select * from cell where name = '" + name + "'");
				}
				// 세포 위치 조회
				else if (str.indexOf("u") == 0) {
					String name = str.substring(str.indexOf("u") + 1, str.indexOf("x"));
					// 자신의 세포가 아닐 때 위치 조회
					if (!name.equals(userName)) {
						ClientMain.moving(str);
					}
				}
				// 먹이 위치 조회
				else if (str.indexOf("특정먹이조회") == 0) {
					String name = str.substring(str.indexOf("특정먹이조회") + 6);
					ClientMain.pDisplay("select name,x,y from particle where name = '" + name + "'");
				}
				// 퇴장 세포 삭제
				else if (str.indexOf("퇴장") == 0) {
					String name = str.substring(str.indexOf("퇴장") + 2);
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						if (c.name.equals(name)) {
							Cell.cells.remove(c);
							break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
