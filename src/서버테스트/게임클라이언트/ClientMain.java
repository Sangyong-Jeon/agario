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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;

import 서버테스트.Cell;
import 서버테스트.Particle;

public class ClientMain extends JFrame implements MouseMotionListener {
	public static ArrayList<Particle> pcopy;
	public static Client client = null;
	public int x, y;
	public String name;

	public static int width = 1920;
	public static int height = 1000;
	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;

	// 전역 필드로 만들어서 다른 메소드에서도 공통적으로 쓸 수 있게 하였다.
	public static Connection conn = null; // Connection은 연결과 관련된 정보와 기능을 갖고있는 클래스
	public static Statement stmt = null; // Statement 클래스는 SQL 구문을 실행하는 역할, 스스로는 SQL 구분못함(구문해석X), 전달역할을 한다.
	// SQL 관리 O + 연결 정보 X
	// Statement객체는 Connection객체의 메소드를 이용하여 생성하도록 설계되어 있다.
	public static ResultSet rs = null; // select 등의 조회 쿼리문을 실행한 후 돌아오는 조회 값을 포함하는 클래스이다.
	// 결과로 가져온 데이터는 Table 형태와 흡사
	// ResultSet의 next()를 이용하여 값이 있는지 없는지 확인.
	// next()실행 후 get() 메소드를 이용하여 값을 얻어옴. 여러행 있을시 반복문 사용

	public boolean isDB = false;

	public ClientMain(String nick) {
		this.name = nick;
		// initialize(); 창만드는 부분
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

		// 서버 접속하는 부분
		connection(this, name);

//		// DB 연결
//		try {
//			conn = makeConnection();
//			stmt = conn.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		// 그리기
		while (true) {
			this.update();
			this.draw();
			this.conMouse();
		}
	}

	public static void connection(ClientMain main, String name) {
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
		for (Cell cell : Cell.cells) {
			cell.Update();
		}
	}

	public void draw() {
		Graphics g = getGraphics();
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
	}

	public void conMouse() {
		for (Cell cell : Cell.cells) {
			if (cell.name.equals(name)) {
				this.x = cell.x;
				this.y = cell.y;
			}
		}
		client.out.println("c" + name + "x" + x + "y" + y);
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
			}
		}
		if (isDB == true) {
			mouseMove();
		}
	}

	public void mouseMove() {
		for (Cell cell : Cell.cells) { // cells 배열 중 내 세포 찾기
			if (cell.name.equals(name)) { // 내 세포를 찾아서 x, y 좌표값 수정
				this.x = cell.goalX;
				this.y = cell.goalY;
				break;
			}
		}
		// 내 세포 x,y좌표값 수정 후 DB에 갱신
		Client.updateDB("cell", name, Integer.toString(x), Integer.toString(y));
		// 갱신 후 서버에 움직였다는 신호를 줌.
		client.out.println("c");

	}
}