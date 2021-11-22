package 서버테스트.게임클라이언트;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;

import 서버테스트.Cell;
import 서버테스트.Particle;

public class Client extends JPanel implements Runnable {
	// 통신용
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String str, userName;

	ClientMain main;

	// -----------DB 관련
	// 전역 필드로 만들어서 다른 메소드에서도 공통적으로 쓸 수 있게 하였다.
	static Connection conn = null; // Connection은 연결과 관련된 정보와 기능을 갖고있는 클래스
	static Statement stmt = null; // Statement 클래스는 SQL 구문을 실행하는 역할, 스스로는 SQL 구분못함(구문해석X), 전달역할을 한다.
	// SQL 관리 O + 연결 정보 X
	// Statement객체는 Connection객체의 메소드를 이용하여 생성하도록 설계되어 있다.
	static ResultSet rs = null; // select 등의 조회 쿼리문을 실행한 후 돌아오는 조회 값을 포함하는 클래스이다.
	// 결과로 가져온 데이터는 Table 형태와 흡사
	// ResultSet의 next()를 이용하여 값이 있는지 없는지 확인.
	// next()실행 후 get() 메소드를 이용하여 값을 얻어옴. 여러행 있을시 반복문 사용
	private static int pnumber;

	public Client(String ip, int port, ClientMain main, String name) {
		this.userName = name;
		this.main = main;
		System.out.println("Client의 userName : " + userName);
		init();
//		start();
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

		// db 연결
		try {
			conn = makeConnection();
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
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
			System.out.println(pnumber);
		}

		main.isDB = true;

		while (true) { // 무한 반복
			try {
				str = in.readLine(); // 문장 입력받아서 세포 or 먹이 조회 구분하기
				System.out.println("입력받음 : " + str);
				if (str != null) {
					if (str.equals("c")) { // 세포일 때
						display("cell"); // db 조회 후 세포배열에 없을 시 생성
					} else if (str.equals("p")) { // 먹이일 때
						display("particle"); // db 조회 후 먹이배열에 없을 시 생성
					}
				} else {
					System.out.println("null을 입력받음");
				}
			} catch (NullPointerException e) {
				System.out.println("NullPointerException 발생");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException 발생");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// DB 관련 메소드
	private static Connection makeConnection() { // 데이터베이스를 연결해주는 메소드이다.
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

		// 1. JDBC 드라이버 적재
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("드라이버 로딩이 성공했어요!!");

		// 2. 데이터베이스 연결
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn; // 연결이 성공적으로 된다면 return으로 값을 보내주고 메소드를 종료한다.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // 정상적으로 연결되지 않았다면 반환값은 null 이다.
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

	// 세포수정 메소드
	public static void updateDB(String table, String name, String x, String y) {
		try {
			String sql = "update " + table + " set cx = '" + x + "', cy = '" + y + "' where cname='" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println("세포 업데이트 완료 : " + name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
