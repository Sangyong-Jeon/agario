package �¶���.����Ŭ���̾�Ʈ;

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

import �¶���.Camera;
import �¶���.Cell;
import �¶���.Leaderboard;
import �¶���.Particle;
import �¶���.ä��Ŭ���̾�Ʈ.ChatClient;

public class ClientMain extends JFrame implements MouseMotionListener {
	// ����
	public double x, y;
	public String name;

	// ������ ����
	public static int width = 1920;
	public static int height = 1000;
	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;
	public JPanel pane = null;
	int fps = 60;

	// ī�޶� ȭ��
	public static Camera cam = new Camera(0, 0, 1, 1);
	// ���� ������ ����
	public static Leaderboard lb = new Leaderboard();

	// DB ����
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB ���� ����
	static int particleNo = 0;

	// ���
	Client client = null;
	boolean isRunning = true;

	public ClientMain(String nick) {
		this.name = nick;
		// �⺻����
		initialize();
		System.out.println("�⺻����");
		// ä�� ����
		Chat();
		System.out.println("ä�ü���");
		this.setVisible(true);
		// DB ����
		makeConnection();
		System.out.println("DB ����");

		// DB���� ���� ��ȸ
		cDisplay("select * from cell");
		System.out.println("DB���� ���� ��ȸ");

		// DB���� ���� ��ȸ
		pDisplay("select * from particle");
		System.out.println("DB���� ���� ��ȸ");

		// ���� �����ϴ� �κ�
		connection(this, name, client);
		System.out.println("���� ����");

		// ���� ����
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
		this.setTitle("����Ű���");
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

	// ���� ���� �޼ҵ�
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

		// �÷��̾� ����ã�Ƽ� ȭ�� ���߱�
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

		bbg.setColor(Color.black); // ���
		bbg.fillRect(0, 0, width, height); // ���ȭ�� �簢�� �׸���

		cam.Graphics(bbg);
		cam.set();

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		if (!pCopy.isEmpty()) { // pCopy�� �Ⱥ������ ��
			for (Particle p : pCopy) { // ������ ���̵� ���ΰ�ħ�Ͽ� bbg ȭ�鿡 ��Ÿ����
				p.Draw(bbg);
			}
		}

		ArrayList<Cell> cCopy = new ArrayList<Cell>(Cell.cells);
		if (!cCopy.isEmpty()) { // cCopy�� �Ⱥ������ ��
			for (Cell c : cCopy) {
				c.Draw(bbg);
			}
			// �� ��ǥ�� �����ֱ�
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
		System.out.println("�׸���Ϸ�");
	}

	@Override
	public void mouseDragged(MouseEvent e) { // ���콺 �巡���Ҷ���
	}

	@Override
	public void mouseMoved(MouseEvent e) { // ���콺 �̵��Ҷ���
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
		for (Cell c : Cell.cells) { // cells �迭 �� �� ���� ã��
			if (c.name.equals(name)) { // �� ������ ã�Ƽ� x, y ��ǥ�� ����
				Client.out.println("u" + c.name + "x" + (int) c.x + "y" + (int) c.y);
			}
		}

	}

	// ----------------------------------------DB ����
	public static void makeConnection() { // conn�� stmt�� �������ش�.
		String url = "jdbc:oracle:thin:@10.30.3.95:1521:orcl";
		String user = "c##2001506";
		String password = "p2001506";

		// 1. JDBC ����̹� ����
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("����̹� �ε��� �����߾��!!");

		// 2. �����ͺ��̽� ����
		try {
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ���� ��ȸ
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
				// cells�� ������� ���� ��
				if (!Cell.cells.isEmpty()) {
					// cells ���� �ϳ��� ���� �˻��Ѵ�.
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						if (c.name.equals(name)) {
							c.mass = mass;
							isTrue = true;
							break;
						}
					}
					// DB���� �˻��� ������ ����Ʈ �ȿ� ������ ����
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else { // cells�� ��� ���� �� ����
					Cell.cells.add(new Cell(name, x, y, mass));
				}
			}
			System.out.println("���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ��� ���� ��ȸ
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
				// cells�� ������� ���� ��
				if (!Cell.cells.isEmpty()) {
					// cells ���� �ϳ��� ���� �˻��Ѵ�.
					for (Cell c : Cell.cells) {
						if (c.name.equals(name)) {
							c.x = x;
							c.y = y;
							c.mass = mass;
							isTrue = true;
							break;
						}
					}
					// DB���� �˻��� ������ ����Ʈ �ȿ� ������ ����
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else { // cells�� ��� ���� �� ����
					Cell.cells.add(new Cell(name, x, y, mass));
				}
			}
			System.out.println("���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ���� ��ȸ
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
				System.out.println("��ȸ ��� name : " + name + " x : " + x + " y : " + y);
				// particles�� ������� ���� ��
				if (!Particle.particles.isEmpty()) {
					// particles ���� �ϳ��� ���� �˻��Ѵ�.
					for (Particle p : Particle.particles) {
						if (p.pname.equals(name)) {
							p.x = x;
							p.y = y;
							isTrue = true;
							break;
						}
					}
					// DB���� �˻��� ���̰� ����Ʈ �ȿ� ���� �� ����
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, 1));
					}
				} else { // particles�� ��� ���� �� ����
					Particle.particles.add(new Particle(name, x, y, 1));
				}
			}
			particleNo = sum;
			System.out.println("���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ������ ������ ��
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