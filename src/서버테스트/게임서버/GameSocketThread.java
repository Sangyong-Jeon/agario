package 서버테스트.게임서버;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import 서버테스트.Cell;
import 서버테스트.Particle;

public class GameSocketThread extends Thread {
	Socket socket;
	GameServer server;
	BufferedReader in;
	PrintWriter out;
	String name, threadName;
	String str;
	static int pnumber;

	// DB 연결
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;

	public GameSocketThread(GameServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();
		System.out.println(socket.getInetAddress() + "님이 입장하였습니다.");
		System.out.println("Thread Name : " + threadName);
	}

	// DB 연결 메소드
	private static Connection makeConnection() {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

		// JDBC 드라이버 적재
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("GameSocketThread의 드라이버 로딩 성공");

		// DB 연결
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // 연결 안되면 null
	}

	// DB조회해서 각 배열에 DB데이터가 없으면 생성해서 넣어준다.
	private static void display(String table) {
		try {
			String sql = "select * from " + table + "";
			rs = stmt.executeQuery(sql);

			if (table.equals("cell")) { // 조회하는것이 세포일 때
				while (rs.next()) {
					boolean isPlayer = false;
					String cname = rs.getString("cname");
					String cx = rs.getString("cx");
					String cy = rs.getString("cy");
					System.out.println("결과값은 " + cname + " , " + cx + " , " + cy);
					if (!Cell.cells.isEmpty()) { // cells가 안비었을 때
						for (Cell c : Cell.cells) {
							if (c.name.equals(cname)) { // 리스트안에 현재 검색중인 세포와 이름이 같을 때
								c.x = Integer.parseInt(cx);
								c.y = Integer.parseInt(cy);
								isPlayer = true;
								break;
							} else { // 리스트 안에 현재 검색중인 세포와 이름이 같지 않을 때

							}
						}
						if (!isPlayer) { // 리스트 안에 검색된 name이 없을 때 생성
							Cell.cells.add(new Cell(cname, Integer.parseInt(cx), Integer.parseInt(cy)));
						}
					} else { // cells가 비었을 때
						Cell.cells.add(new Cell(cname, Integer.parseInt(cx), Integer.parseInt(cy)));
					}
				}
			} else if (table.equals("particle")) { // 조회하는 것이 먹이일 때
				int result = 0; // 검색되는 갯수 세기
				while (rs.next()) {
					boolean isParticle = false; // 검색 할때마다 false로 초기화해서 pCopy안에 있는지 없는지 확인하여 없으면 생성시킴
					String pname = rs.getString("pname");
					String px = rs.getString("px");
					String py = rs.getString("py");
					result += 1; // 검색 될 때 마다 1씩 증가
					System.out.println("particle 검색 : " + pname + " / " + px + " / " + py);

					ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
					if (!pCopy.isEmpty()) { // pCopy가 안비었을 때
						for (Particle p : pCopy) {
							if (p.pname.equals(pname)) { // pCopy 안에 현재 검색중인 먹이와 이름이 같을 때, db의 좌표로 수정해준다.
								p.x = Integer.parseInt(px);
								p.y = Integer.parseInt(py);
								isParticle = true; // pCopy 안에 있으므로 true로 바꿔서 생성하지 않음
								break; // pCopy안에 값이 있으므로 조회 중지
							} else { // 리스트 안에 현재 검색중인 먹이와 이름이 같지 않을 때

							}
						}
						if (!isParticle) { // pCopy 안에 검색된 pname이 없을 때 생성
							Particle.particles.add(new Particle(pname, Integer.parseInt(px), Integer.parseInt(py), 1));
						}
					} else { // pCopy가 비었을 때
						Particle.particles.add(new Particle(pname, Integer.parseInt(px), Integer.parseInt(py), 1));
					}
				}
				pnumber = result;
			}
			System.out.println("검색된 수 " + pnumber);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 삽입 메소드
	private static void insertDB(String table, String name, String x, String y) {
		try {
			String sql = "insert into " + table + " values ('" + name + "', '" + x + "', '" + y + "')";
			stmt.executeUpdate(sql);
			System.out.println("테이블 : " + table + " x좌표 : " + x + " y좌표 : " + y + " 삽입 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// db 삭제 메소드
	private static void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname='" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "를 DB에서 삭제 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void allCellDelete() {
		try {
			String sql = "delete from cell";
			stmt.executeUpdate(sql);
			System.out.println("세포 전부 DB에서 삭제 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

			// db 연결
			try {
				conn = makeConnection();
				stmt = conn.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 먹이 조회해서 pnumber 갱신
			display("particle");

			// 먹이 DB 5000개 생성하기
			while (pnumber < 5000) {
				String x = Integer.toString((int) Math.floor(Math.random() * 10001));
				String y = Integer.toString((int) Math.floor(Math.random() * 2801));
				insertDB("particle", Integer.toString(pnumber), x, y);
				// 생성하고 조회해서 pnumber 갱신
				display("particle");
				System.out.println(pnumber++);
			}

			// 닉네임 받기
			name = in.readLine();
			System.out.println(name);

			// 세포 DB에 삽입하기
			insertDB("cell", name, Integer.toString((int) Math.floor(Math.random() * 10001)),
					Integer.toString((int) Math.floor(Math.random() * 2801)));

			// 새로운 세포 생성했으니 모든 클라이언트에게 cell 테이블 조회하라고 보내기
			server.broadCasting("c");

			// 조회해서 DB에 있는 값이 cells 배열에 없을 때 넣어주는 메소드
			display("cell");

			while (true) {
				try {
					System.out.println("입력받기 전");
					// 세포 및 먹이가 이동했다는 문자를 받음.
					str = in.readLine();
					System.out.println("서버가 문자받음 : " + str);

					if (str != null) {
						if (str.indexOf("c") == 0) { // 세포일 때
							display("cell");
							System.out.println("세포조회함");
							server.broadCasting("c");
						} else if (str.indexOf("p") == 0) { // 먹이일 때
							display("particle");
							System.out.println("먹이조회함");
							server.broadCasting("p");
						}
					} else {
						System.out.println("서버가 null을 입력받음");
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("NullPointerException 발생");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("IOException 발생");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println(threadName + " 퇴장했습니다. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " 퇴장했습니다. ");
					Cell.cells.remove(c);
				}
			}
			server.removeClient(this);
			e.printStackTrace();
		} finally {
			deleteDB(name);
			if (rs != null) { // rs, stmt, conn의 값이 있다면 닫아주고, 없다면 넘어간다.
				try {
					rs.close();
					System.out.println("rs닫아짐");
				} // rs에 값이 있어서 try문이 실행 된다. rs.close();로 시스템 자원을 반환해준다. 그리고 닫았다는걸 출력해줌.
				catch (SQLException e) {
					e.printStackTrace();
				} // 아니라면 오류가 난것이므로 catch문으로 감.
			}
			if (stmt != null) {
				try {
					stmt.close();
					System.out.println("stmt 닫아짐");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
					System.out.println("conn 닫아짐");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
