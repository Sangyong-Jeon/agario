package 온라인.게임서버;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import 온라인.Cell;
import 온라인.Particle;

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
		pDisplay("select * from particle");

		// DB 먹이 갯수가 5000개 미만일 때 먹이 생성
		while (particleNo < 5000) {
			int x = (int) Math.floor(Math.random() * 5000);
			int y = (int) Math.floor(Math.random() * 5000);
			insertDB("insert into particle values('" + particleNo++ + "'," + x + "," + y + ", 1)");
		}
		pDisplay("select * from particle");

		// DB안의 세포 조회
		cDisplay("select * from cell");

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

		for (Cell c : Cell.cells) {
			c.Action();
		}
	}

	// 그리기
	public void draw() {
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
//		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black);
		bbg.fillRect(0, 0, width, height);

		Graphics2D g2 = (Graphics2D) bbg;
		g2.scale(0.18, 0.18);
		g2.translate(width + 300, height / 2 - 100);

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
	}

	// ------------------------------------------------DB 관련 메소드

	public static void makeConnection() { // conn과 stmt를 설정해준다.
		// 172.26.1.93
		String url = "jdbc:oracle:thin:@10.30.3.95:1521:orcl";
		String user = "c##2001506";
		String password = "p2001506";

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

	// 세포 조회
	public static void cDisplay(String str) {
		try {
			boolean isTrue = false;
			rs = stmt.executeQuery(str);
			while (rs.next()) {
				isTrue = false;
				String name = rs.getString("name");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int mass = rs.getInt("mass");
				System.out.println("조회 결과 name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// cells가 비어있지 않을 때
				if (!Cell.cells.isEmpty()) {
					// cells 값을 하나씩 꺼내 검색한다.
					for (Cell c : Cell.cells) {
						if (c.name.equals(name)) {
							c.x = x;
							c.y = y;
							c.mass = mass;
							isTrue = true;
							break;
						}
					}
					// DB에서 검색한 세포가 리스트 안에 없을때 생성
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else { // cells가 비어 있을 때 생성
					Cell.cells.add(new Cell(name, x, y, mass));
				}
			}
			System.out.println("세포 DB 조회 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 먹이 조회
	public static void pDisplay(String str) {
		try {
			boolean isTrue = false;
			int sum = 0;
			rs = stmt.executeQuery(str);
			while (rs.next()) {
				sum++;
				isTrue = false;
				String name = rs.getString("name");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int mass = rs.getInt("mass");
				System.out.println("조회 결과 name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// particles가 비어있지 않을 때
				if (!Particle.particles.isEmpty()) {
					// particles 값을 하나씩 꺼내 검색한다.
					for (Particle p : Particle.particles) {
						if (p.pname.equals(name)) {
							isTrue = true;
							break;
						}
					}
					// DB에서 검색한 먹이가 리스트 안에 없을 때 생성
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, mass));
					}
				} else { // particles가 비어 있을 때 생성
					Particle.particles.add(new Particle(name, x, y, mass));
				}
			}
			particleNo = sum;
			System.out.println("먹이 DB 조회 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// DB 삽입
	public static void insertDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// DB 수정
	public static void updateDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// DB에서 삭제
	public static void deleteDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 세포가 움직일 때
	public static void moving(String str) {
		String name = str.substring(str.indexOf("u") + 1, str.indexOf("x"));
		String x = str.substring(str.indexOf("x") + 1, str.indexOf("y"));
		String y = str.substring(str.indexOf("y") + 1);
		for (Cell c : Cell.cells) {
			if (c.name.equals(name)) {
				c.x = Integer.parseInt(x);
				c.y = Integer.parseInt(y);
				break;
			}
		}
	}
}
