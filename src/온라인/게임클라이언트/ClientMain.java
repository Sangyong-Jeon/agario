package 온라인.게임클라이언트;

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

import 온라인.Camera;
import 온라인.Cell;
import 온라인.Leaderboard;
import 온라인.Particle;
import 온라인.채팅클라이언트.ChatClient;

public class ClientMain extends JFrame implements MouseMotionListener {
	// 세포
	public double x, y;
	public String name;

	// 프레임 관련
	public static int width = 1920;
	public static int height = 1000;
	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;
	public JPanel pane = null;
	int fps = 60;

	// 카메라 화면
	public static Camera cam = new Camera(0, 0, 1, 1);
	// 공용 점수판 생성
	public static Leaderboard lb = new Leaderboard();

	// DB 관련
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB 먹이 갯수
	static int particleNo = 0;

	// 통신
	Client client = null;
	boolean isRunning = true;

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

		// DB에서 세포 조회
		cDisplay("select * from cell");
		System.out.println("DB에서 세포 조회");

		// DB에서 먹이 조회
		pDisplay("select * from particle");
		System.out.println("DB에서 먹이 조회");

		// 서버 접속하는 부분
		connection(this, name, client);
		System.out.println("서버 접속");

		// 게임 동작
		start();

	}

	public void start() {
		while (isRunning) {
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
		}

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

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
			ChatClient chat = new ChatClient(ip, 5555, name);
			this.add(chat, "West");
		} catch (UnknownHostException e) {
			e.printStackTrace();
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

		// 플레이어 세포찾아서 화면 비추기
		for (int i = 0; i < Cell.cells.size(); i++) {
			if (Cell.cells.get(i).name.equals(this.name)) {
				cam.Update(Cell.cells.get(i));
				break;
			}
		}

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
		lb.Update();

		Graphics g = pane.getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black); // 배경
		bbg.fillRect(0, 0, width, height); // 배경화면 사각형 그리기

		cam.Graphics(bbg);
		cam.set();

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
			// 내 좌표값 보여주기
			for (Cell c : cCopy) {
				if (c.name.equals(name)) {
					String pos = ("X: " + (int) c.x + " Y: " + (int) c.y);
					bbg2.drawString(pos, (ClientMain.width - pos.length() * pos.length() - 250), 10);
				}
			}
		}

		cam.unset();

		lb.Draw(bbg2);
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
				int i = (int) cell.mass / 2;
				cell.getMouseX((int) (e.getX() / cam.sX + cam.x) - i);
				cell.getMouseY((int) (e.getY() / cam.sY + cam.y) - i);
				break;
			}
		}
	}

	public void mouseMove() {
		for (Cell c : Cell.cells) { // cells 배열 중 내 세포 찾기
			if (c.name.equals(name)) { // 내 세포를 찾아서 x, y 좌표값 수정
				Client.out.println("u" + c.name + "x" + (int) c.x + "y" + (int) c.y);
			}
		}

	}

	// ----------------------------------------DB 관련
	public static void makeConnection() { // conn과 stmt를 설정해준다.
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
				// cells가 비어있지 않을 때
				if (!Cell.cells.isEmpty()) {
					// cells 값을 하나씩 꺼내 검색한다.
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						if (c.name.equals(name)) {
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

	// 사망 세포 조회
	public static void dDisplay(String str) {
		try {
			boolean isTrue = false;
			rs = stmt.executeQuery(str);
			while (rs.next()) {
				isTrue = false;
				String name = rs.getString("name");
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int mass = rs.getInt("mass");
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
				System.out.println("조회 결과 name : " + name + " x : " + x + " y : " + y);
				// particles가 비어있지 않을 때
				if (!Particle.particles.isEmpty()) {
					// particles 값을 하나씩 꺼내 검색한다.
					for (Particle p : Particle.particles) {
						if (p.pname.equals(name)) {
							p.x = x;
							p.y = y;
							isTrue = true;
							break;
						}
					}
					// DB에서 검색한 먹이가 리스트 안에 없을 때 생성
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, 1));
					}
				} else { // particles가 비어 있을 때 생성
					Particle.particles.add(new Particle(name, x, y, 1));
				}
			}
			particleNo = sum;
			System.out.println("먹이 DB 조회 완료");
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