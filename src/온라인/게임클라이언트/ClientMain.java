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
	int particleNo = 0;

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

		// �÷��̾� ���� DB ����
		insertDB("cell", nick, 100, 100, 10);
		System.out.println("�÷��̾� ���� DB����");
		Cell.cells.add(new Cell(nick, 100, 100, 10));

		// DB���� ���� ��ȸ
		cDisplay();
		System.out.println("DB���� ���� ��ȸ");

		// DB���� ���� ��ȸ
		pDisplay();
		System.out.println("DB���� ���� ��ȸ");

		// ���� �����ϴ� �κ�
		connection(this, name, client);
		System.out.println("���� �����ϴ� �κ�");

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
				updateDB("cell", name, c.x, c.y, c.mass);
				client.out.println("c");
			}
		}

	}

	// ----------------------------------------DB ����
	public void makeConnection() {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

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
			System.out.println("---DB ���� �Ϸ�---");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ���� ��ȸ �� ����,����
	public void cDisplay() {
		try {
			boolean isTrue = false;
			String sql = "select * from cell";
			rs = stmt.executeQuery(sql); // sql ������ ���� �� ���ƿ��� ��ȸ���� ResultSet ��ü�� ����
			while (rs.next()) {
				// �˻��� �� ���� isTrue�� false�� �ʱ�ȭ������
				isTrue = false;
				String name = rs.getString("cname");
				int x = (int) rs.getInt("cx");
				int y = (int) rs.getInt("cy");
				int mass = (int) rs.getInt("cmass");
				System.out.println("�˻� ��� name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				if (this.name.equals(name)) {
					continue;
				}
				// cells�� ������� ���� ��
				if (!Cell.cells.isEmpty()) {
					// cells ����Ʈ�� ������ �ϳ��� �˻��Ѵ�.
					for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
						Cell c = it.next();
						// ���� ��ȸ���� ������ �̸��� ���� ��
						if (c.name.equals(name)) {
							c.x = x;
							c.y = y;
							c.mass = mass;
							// ���� ��ȸ���� ������ ����Ʈ�ȿ� �ִٰ� ����
							isTrue = true;
							// for���� ����
							break;
						}
					}
					// ����Ʈ�ȿ� DB���� �˻����� ������ ���� ��
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else {
					Cell.cells.add(new Cell(name, x, y, mass));
				}
			}
			System.out.println("���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null ���ܹ߻�");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("���� �߻�");
			e.printStackTrace();
		}
	}

	// Ư�� ���� ��ȸ ����
	public void cDisplay(String str) {
		String name = str.substring(str.indexOf("u") + 1);
		System.out.println("name : " + name);
		try {
			String sql = "select * from cell where cname = '" + name + "'";
			rs = stmt.executeQuery(sql);
			rs.next();
			int x = rs.getInt("cx");
			int y = rs.getInt("cy");
			int mass = rs.getInt("cmass");
			for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
				Cell c = it.next();
				if (c.name.equals(name)) {
					c.x = x;
					c.y = y;
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

	// ���� ��ȸ �� ����,����
	public void pDisplay() {
		try {
			String sql = "select pname,px,py from particle";
			rs = stmt.executeQuery(sql); // sql ������ ���� �� ���ƿ��� ��ȸ���� ResultSet ��ü�� ����
			int result = 0;// �˻�Ƚ��

			while (rs.next()) {
				result++; // �˻��� ������ 1�� ����
				// �˻��� �� ���� isTrue�� false�� �ʱ�ȭ������
				boolean isTrue = false;
				String name = rs.getString("pname");
				int x = rs.getInt("px");
				int y = rs.getInt("py");
				System.out.println("name : " + name + " x : " + x + " y : " + y);
				// particles ����Ʈ�� ������� ���� ��
				if (!Particle.particles.isEmpty()) {
					// particles ����Ʈ�� ������ �ϳ��� �˻��Ѵ�.
					for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
						Particle p = it.next();
						if (p.pname.equals(name)) {
							p.x = x;
							p.y = y;
							// ���� ��ȸ���� ���� ����Ʈ �ȿ� �ִٰ� ����
							isTrue = true;
							// for���� �����.
							break;
						}
					}
					// ����Ʈ�� �����Ǿ� ������ ���� �˻����� ���̰� ���� �� ����
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, 1));
					}
				} else { // particles ����Ʈ�� ������� ��
					Particle.particles.add(new Particle(name, x, y, 1));
				}
			}
			particleNo = result;
			System.out.println(particleNo + " ���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Null ���� �߻�");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("���� �߻�");
			e.printStackTrace();
		}
	}

	// Ư�� ���� ��ȸ ����
	public void pDisplay(String str) {
		String name = str.substring(str.indexOf("s") + 1);
		System.out.println("name : " + name);
		try {
			String sql = "select px,py from particle where pname = '" + name + "'";
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

	// DB ����
	public void insertDB(String table, String name, int x, int y, int mass) {
		try {
			String sql = "insert into " + table + " values('" + name + "', " + x + " , " + y + ", " + mass + ")";
			stmt.executeUpdate(sql);
			System.out.println("���̺� : " + table + " �̸� : " + name + " ���� �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// ���� DB ����
	public void updateDB(String table, String name, double x, double y, double mass) {
		try {
			String sql = "update cell set cx = " + x + ", cy = " + y + ", cmass = " + mass + " where cname = '" + name
					+ "'";
			stmt.executeUpdate(sql);
			System.out.println("���̺� : " + table + " �̸� : " + name + " ������Ʈ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// DB���� ����
	public void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname = '" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "���� DB���� �����Ǿ����ϴ�.");
		} catch (SQLException e) {

		}
	}
}