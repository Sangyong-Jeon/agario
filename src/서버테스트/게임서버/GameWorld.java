package 서버테스트.게임서버;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

public class GameWorld extends JFrame {

	public static int width = 1920;
	public static int height = 1000;

	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;

	// DB 관련
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB에 있는 먹이갯수
	static int particleNo = 0;

	public GameWorld() {
		// initialize();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setTitle("세포키우기");
		this.setResizable(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.insets = this.getInsets();
		this.setSize(insets.left + width + insets.right, insets.top + height + insets.bottom);
		this.setLocation(size.width / 2 - getSize().width / 2, size.height / 2 - getSize().height / 2);
		this.setVisible(true);
		this.requestFocus();
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// DB 연결
		makeConnection();

		// DB에서 먹이 조회
		pDisplay();

		// DB 먹이 갯수가 5000개 미만일 때 먹이 생성
		while (particleNo < 5000) {
			insertDB("particle", "p" + particleNo++, (int) Math.floor(Math.random() * 5000),
					(int) Math.floor(Math.random() * 5000), 1);
		}
		pDisplay();

		// DB안의 세포 조회
		cDisplay();

		// 무한 반복
		while (true) {
			long time = System.currentTimeMillis();
			update();
			draw();
			time = (1000 / 60) - (System.currentTimeMillis() - time);
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (Exception e) {
					e.getStackTrace();
				}
			}
		}
	}

	public void update() {
		// 먹이 동작
		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			Particle p = it.next();
			p.Update();
		}
		System.out.println("동작 완료");
	}

	// 그리기
	public void draw() {
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black);
		bbg.fillRect(0, 0, width, height);

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		if (!pCopy.isEmpty()) { // 안 비어있을 때
			for (Particle p : pCopy) {
				p.Draw(bbg);
			}
		}

		ArrayList<Cell> cCopy = new ArrayList<Cell>(Cell.cells);
		if (!cCopy.isEmpty()) {
			for (Cell c : cCopy) {
				c.Draw(bbg);
			}
		}

		g.drawImage(backBuffer, insets.left, insets.top, this);
		System.out.println("그리기 완료");
	}

	// ------------------------------------------------DB 관련 메소드

	public static void makeConnection() { // conn과 stmt를 설정해준다.
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
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 세포 조회 후 생성,갱신
	public static void cDisplay() {
		try {
			boolean isTrue = false;
			String sql = "select * from cell";
			rs = stmt.executeQuery(sql); // sql 쿼리문 실행 후 돌아오는 조회값을 ResultSet 객체에 넣음
			while (rs.next()) {
				// 검색할 때 마다 isTrue를 false로 초기화시켜줌
				isTrue = false;
				String name = rs.getString("cname");
				int x = (int) rs.getInt("cx");
				int y = (int) rs.getInt("cy");
				int mass = (int) rs.getInt("cmass");
				System.out.println("검색 결과 name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// cells가 비어있지 않을 때
				if (!Cell.cells.isEmpty()) {
					// cells 리스트의 값들을 하나씩 검색한다.
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						// 현재 조회중인 세포의 이름과 같을 때
						if (c.name.equals(name)) {
							c.x = x;
							c.y = y;
							c.mass = mass;
							// 현재 조회중인 세포는 리스트안에 있다고 설정
							isTrue = true;
							// for문을 나옴
							break;
						}
					}
					// 리스트안에 DB에서 검색중인 세포가 없을 때
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else {
					Cell.cells.add(new Cell(name, x, y, mass));
				}
			}
			System.out.println("세포 DB 조회 완료");
		} catch (SQLException e) {
			System.out.println("GameWorld에서 SQLException 발생");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("GameWorld에서 Null 예외발생");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("GameWorld에서 예외 발생");
			e.printStackTrace();
		}
	}

	// 먹이 조회 후 생성,갱신
	public static void pDisplay() {
		try {
			String sql = "select * from particle";
			rs = stmt.executeQuery(sql); // sql 쿼리문 실행 후 돌아오는 조회값을 ResultSet 객체에 넣음
			int result = 0;// 검색횟수

			while (rs.next()) {
				result++; // 검색할 때마다 1씩 증가
				// 검색할 때 마다 isTrue를 false로 초기화시켜줌
				boolean isTrue = false;
				String name = rs.getString("pname");
				int x = rs.getInt("px");
				int y = rs.getInt("py");
				int mass = rs.getInt("pmass");
				System.out.println("name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// particles 리스트가 비어있지 않을 때
				if (!Particle.particles.isEmpty()) {
					// particles 리스트의 값들을 하나씩 검색한다.
					for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
						Particle p = it.next();
						if (p.pname.equals(name)) {
							p.x = x;
							p.y = y;
							// 현재 조회중인 것은 리스트 안에 있다고 설정
							isTrue = true;
							// for문을 멈춘다.
							break;
						}
					}
					// 리스트가 생성되어 있지만 현재 검색중인 먹이가 없을 때 생성
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, mass));
					}
				} else { // particles 리스트가 비어있을 때
					Particle.particles.add(new Particle(name, x, y, mass));
				}
			}
			particleNo = result;
			System.out.println(particleNo + " 먹이 DB 조회 완료");
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null 예외 발생");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("예외 발생");
			e.printStackTrace();
		}
	}

	// DB 삽입
	public static void insertDB(String table, String name, int x, int y, int mass) {
		try {
			String sql = "insert into " + table + " values('" + name + "', " + x + " , " + y + ", " + mass + ")";
			stmt.executeUpdate(sql);
			System.out.println("테이블 : " + table + " 이름 : " + name + " 업데이트 완료");
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// 세포 DB 수정
	public static void cUpdateDB(String table, String name, double x, double y, double mass) {
		try {
			String sql = "update cell set cx = " + x + ", cy = " + y + ", cmass = " + mass + " where cname = '" + name
					+ "'";
			stmt.executeUpdate(sql);
			System.out.println("테이블 : " + table + " 이름 : " + name + " 업데이트 완료");
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// 먹이 DB 수정
	public static void pUpdateDB(String table, String name, double x, double y, double mass) {
		try {
			String sql = "update particle set px = " + x + ", py = " + y + ", pmass = " + mass + " where pname = '"
					+ name + "'";
			stmt.executeUpdate(sql);
			System.out.println("테이블 : " + table + " 이름 : " + name + " 업데이트 완료");
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// DB에서 삭제
	public static void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname = '" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "님이 DB에서 삭제되었습니다.");
		} catch (SQLException e) {

		}
	}

}
