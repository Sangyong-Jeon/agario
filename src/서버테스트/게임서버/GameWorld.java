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

import javax.swing.JFrame;

import 서버테스트.Cell;
import 서버테스트.Particle;

public class GameWorld extends JFrame implements Runnable {

	public static int width = 1920;
	public static int height = 1000;

	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;

	public static ArrayList<Cell> cCopy;

	// DB
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	private static int pnumber;

	public GameWorld() {
		// initialize();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		this.setTitle("세포키우기");
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.insets = this.getInsets();
		this.setSize(insets.left + width + insets.right, insets.top + height + insets.bottom);
		this.setLocation(size.width / 2 - getSize().width / 2, size.height / 2 - getSize().height / 2);
		this.setVisible(true);
		this.requestFocus();
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// DB 연결
		try {
			conn = makeConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// DB 연결 메소드
	private static Connection makeConnection() {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

		// 1. JDBC 드라이버 적재
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("GameWorld의 드라이버 로딩이 성공했어요!!");

		// 2. 데이터베이스 연결
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn; // 연결이 성공적으로 된다면 return으로 값을 보내주고 메소드를 종료한다.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // 정상적으로 연결되지 않았다면 반환값은 null 이다.
	}

	@Override
	public void run() {
		// 먹이생성
		update();
		
		while (true) {
			draw();
		}
		
		
	}

	public void update() {
		display("particle");
		while (pnumber < 5000) {
			String x = Integer.toString((int) Math.floor(Math.random() * 10001));
			String y = Integer.toString((int) Math.floor(Math.random() * 2801));
			// 먹이생성
			insertDB("particle", Integer.toString(Particle.particleCount), x, y);
			// 생성하고 조회해서 pnumber 갱신
			display("particle");
			System.out.println(pnumber);
		}
	}

	// DB조회해서 각 배열에 DB데이터가 없으면 생성해서 넣어준다.
	private static void display(String table) {
		try {
			String sql = "select * from " + table + "";
			rs = stmt.executeQuery(sql);

			if (table.equals("cell")) {
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
			} else if (table.equals("particle")) {
				int result = 0;
				while (rs.next()) {
					boolean isParticle = false;
					String pname = rs.getString("pname");
					String px = rs.getString("px");
					String py = rs.getString("py");
					result += 1;

					ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
					if (!pCopy.isEmpty()) { // pCopy가 안비었을 때
						for (Particle p : pCopy) {
							if (p.pname.equals(pname)) { // 리스트 안에 현재 검색중인 먹이와 이름이 같을 때
								p.x = Integer.parseInt(px);
								p.y = Integer.parseInt(py);
								isParticle = true;
								break;
							} else { // 리스트 안에 현재 검색중인 먹이와 이름이 같지 않을 때

							}
						}
						if (!isParticle) { // 리스트 안에 검색된 pname이 없을 때 생성
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
			System.out.println("테이블 : " + table + " x좌표 : " + x + " y좌표 : " + y + "삽입 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 그리기
	public void draw() {
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black);
		bbg.fillRect(0, 0, width, height);

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		for (Particle p : pCopy) {
			p.Draw(bbg);
		}

		ArrayList<Cell> cCopy = new ArrayList<Cell>(Cell.cells);
		for (Cell c : cCopy) {
			c.Draw(bbg);
		}

		g.drawImage(backBuffer, insets.left, insets.top, this);
	}

}
