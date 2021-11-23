package 서버테스트.게임클라이언트;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import 서버테스트.게임서버.Cell;
import 서버테스트.게임서버.Particle;
import 온라인.채팅클라이언트.ChatClient;

public class ClientMain extends JFrame implements MouseMotionListener {

	public double x, y, goalX, goalY;
	public String name;

	public int width = 1920;
	public int height = 1000;
	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;
	public boolean isDB = false;

	// DB 관련
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB에 있는 먹이 갯수
	int particleNo = 0;

	Client client = null;
	public JPanel pane = null;
	int fps = 60;

	public void initialize() {
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
		this.addMouseMotionListener(this);
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		pane = new JPanel();
		pane.setSize(1600, height);
		pane.setVisible(true);

		this.add(pane, "Center");
	}

	public void Chat() {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			String ip_str = ia.toString();
			String ip = ip_str.substring(ip_str.indexOf("/") + 1);
			ChatClient client = new ChatClient(ip, 5555);
			this.add(client, "West");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public ClientMain(String nick) {
		this.name = nick;
		// 기본설정
		initialize();
		System.out.println("기본설정");
		// 채팅 연결
		Chat();
		System.out.println("채팅설정");
		this.setVisible(true);
		// DB 연결
		makeConnection();
		System.out.println("DB 연결");

		// 플레이어 세포 DB 생성
		insertDB("cell", nick, 100, 100, 10);
		System.out.println("플레이어 세포 DB생성");
		Cell.cells.add(new Cell(nick, 100, 100, 10));

		// DB에서 세포 조회
		cDisplay();
		System.out.println("DB에서 세포 조회");

		// DB에서 먹이 조회
		pDisplay();
		System.out.println("DB에서 먹이 조회");

		// 서버 접속하는 부분
		connection(this, name, client);
		System.out.println("서버 접속하는 부분");

		while (true) {
			long time = System.currentTimeMillis();
			this.update();
			this.draw();
			time = (1000 / fps) - (System.currentTimeMillis() - time);
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (Exception e) {
					e.getStackTrace();
				}
			}

//					this.conMouse();
		}
	}

	// 서버 연결 메소드
	public static void connection(ClientMain main, String name, Client client) {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			String ip_str = ia.toString();
			String ip = ip_str.substring(ip_str.indexOf("/") + 1);
			System.out.println("ClientMain : " + name);
			client = new Client(ip, 5556, main, name);
			main.setVisible(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void update() {

		for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
			Cell c = it.next();
			if (c.name.equals(name)) {
				c.Update();
				break;
			}
		}

		mouseMove();

//		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
//			Particle p = it.next();
//			p.Update();
//		}

	}

	public void draw() {
		Graphics g = pane.getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black); // 배경
		bbg.fillRect(0, 0, width, height);

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		if (!pCopy.isEmpty()) { // pCopy가 안비어있을 때
			for (Particle p : pCopy) { // 생성된 먹이들 새로고침하여 bbg 화면에 나타내기
				p.Draw(bbg);
			}
		}

		ArrayList<Cell> cCopy = new ArrayList<Cell>(Cell.cells);
		if (!cCopy.isEmpty()) { // cCopy가 안비어있을 때
			for (Cell c : cCopy) {
				c.Draw(bbg);
			}
		}

		g.drawImage(backBuffer, insets.left, insets.top, this);
		System.out.println("그리기완료");
	}

	@Override
	public void mouseDragged(MouseEvent e) { // 마우스 드래그할때만
	}

	@Override
	public void mouseMoved(MouseEvent e) { // 마우스 이동할때만
		for (Cell cell : Cell.cells) {
			if (cell.name.equals(name)) {
				cell.getMouseX(e.getX());
				cell.getMouseY(e.getY());
				break;
			}
		}
	}

	public void mouseMove() {
		for (Cell c : Cell.cells) { // cells 배열 중 내 세포 찾기
			if (c.name.equals(name)) { // 내 세포를 찾아서 x, y 좌표값 수정
				updateDB("cell", name, c.x, c.y, c.mass);
				client.out.println("c");
			}
		}

	}

	// ----------------------------------------DB 관련
	public void makeConnection() {
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
			System.out.println("---DB 연결 완료---");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 세포 조회 후 생성,갱신
	public void cDisplay() {
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
				if (this.name.equals(name)) {
					continue;
				}
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
			System.out.println("SQLException 발생");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null 예외발생");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("예외 발생");
			e.printStackTrace();
		}
	}

	// 특정 세포 조회 갱신
	public void cDisplay(String str) {
		String name = str.substring(str.indexOf("u") + 1);
		System.out.println("name : " + name);
		try {
			String sql = "select cmass from cell where cname = '" + name + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			int mass = rs.getInt("cmass");
			for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
				Cell c = it.next();
				if (c.name.equals(name)) {
					c.mass = mass;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 먹이 조회 후 생성,갱신
	public void pDisplay() {
		try {
			String sql = "select pname,px,py from particle";
			rs = stmt.executeQuery(sql); // sql 쿼리문 실행 후 돌아오는 조회값을 ResultSet 객체에 넣음
			int result = 0;// 검색횟수

			while (rs.next()) {
				result++; // 검색할 때마다 1씩 증가
				// 검색할 때 마다 isTrue를 false로 초기화시켜줌
				boolean isTrue = false;
				String name = rs.getString("pname");
				int x = rs.getInt("px");
				int y = rs.getInt("py");
				System.out.println("name : " + name + " x : " + x + " y : " + y + " mass : ");
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
						Particle.particles.add(new Particle(name, x, y, 1));
					}
				} else { // particles 리스트가 비어있을 때
					Particle.particles.add(new Particle(name, x, y, 1));
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

	// 특정 먹이 조회 갱신
	public void pDisplay(String str) {
		String name = str.substring(str.indexOf("s") + 1);
		System.out.println("name : " + name);
		try {
			String sql = "select pname,px,py from particle where pname = '" + name + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			int x = rs.getInt("px");
			int y = rs.getInt("py");
			for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
				Particle p = it.next();
				if (p.pname.equals(name)) {
					p.x = x;
					p.y = y;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB 삽입
	public void insertDB(String table, String name, int x, int y, int mass) {
		try {
			String sql = "insert into " + table + " values('" + name + "', " + x + " , " + y + ", " + mass + ")";
			stmt.executeUpdate(sql);
			System.out.println("테이블 : " + table + " 이름 : " + name + " 삽입 완료");
		} catch (SQLException e) {
			System.out.println("SQLException 발생");
			e.printStackTrace();
		}
	}

	// 세포 DB 수정
	public void updateDB(String table, String name, double x, double y, double mass) {
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

	// DB에서 삭제
	public void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname = '" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "님이 DB에서 삭제되었습니다.");
		} catch (SQLException e) {

		}
	}
}