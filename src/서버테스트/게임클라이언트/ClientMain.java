package �����׽�Ʈ.����Ŭ���̾�Ʈ;

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

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;
import �¶���.ä��Ŭ���̾�Ʈ.ChatClient;

public class ClientMain extends JFrame implements MouseMotionListener {

	public double x, y, goalX, goalY;
	public String name;

	public int width = 1920;
	public int height = 1000;
	BufferedImage backBuffer;
	Insets insets;
	public CardLayout card = null;
	public boolean isDB = false;

	// DB ����
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB�� �ִ� ���� ����
	int particleNo = 0;

	Client client = null;
	public JPanel pane = null;
	int fps = 60;

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
			ChatClient client = new ChatClient(ip, 5555);
			this.add(client, "West");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public ClientMain(String nick) {
		this.name = nick;
		// �⺻����
		initialize();
		// ä�� ����
		Chat();
		this.setVisible(true);
		// DB ����
		makeConnection();

		// DB���� ���� ��ȸ
		pDisplay();
		// DB���� ���� ��ȸ
		cDisplay();

		// ���� �����ϴ� �κ�
		connection(this, name, client);

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

		for (Cell cell : Cell.cells) {
			cell.Update();
		}

		mouseMove();

		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			Particle p = it.next();
			p.Update();
		}

	}

	public void draw() {
		Graphics g = pane.getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black); // ���
		bbg.fillRect(0, 0, width, height);

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
		}

		g.drawImage(backBuffer, insets.left, insets.top, this);
		System.out.println("�׸���Ϸ�");
	}

//	public void conMouse() {
//		for (Cell cell : Cell.cells) {
//			if (cell.name.equals(name)) {
//				this.x = cell.x;
//				this.y = cell.y;
//				break;
//			}
//		}
//	}

	@Override
	public void mouseDragged(MouseEvent e) { // ���콺 �巡���Ҷ���
	}

	@Override
	public void mouseMoved(MouseEvent e) { // ���콺 �̵��Ҷ���
		for (Cell cell : Cell.cells) {
			if (cell.name.equals(name)) {
				cell.getMouseX(e.getX());
				cell.getMouseY(e.getY());
				break;
			}
		}
	}

	public void mouseMove() {
		for (Cell c : Cell.cells) { // cells �迭 �� �� ���� ã��
			if (c.name.equals(name)) { // �� ������ ã�Ƽ� x, y ��ǥ�� ����
				this.x = c.x;
				this.y = c.y;
				updateDB("cell", name, x, y, c.mass);
				client.out.println("c");
			}
		}

	}

	// ----------------------------------------DB ����
	public void makeConnection() { // conn�� stmt�� �������ش�.
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
				System.out.println("name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// cells ����Ʈ�� ������� ���� ��
				if (!Cell.cells.isEmpty()) {
					// cells ����Ʈ�� ������ �ϳ��� �˻��Ѵ�.
					for (Cell c : Cell.cells) {
						// ���� ���� ��ȸ���� ������ �̸��� ���� ��
						if (c.name.equals(name)) {
							c.x = x;
							c.y = y;
							c.mass = mass;
							// ���� ��ȸ���ΰ��� ����Ʈ�ȿ� �ִٰ� ����
							isTrue = true;
							// for�� �˻��� �����.
							break;
						}
					}
					// ����Ʈ�� �����Ǿ� ������ ���� �˻����� ������ ���� �� ����
					if (!isTrue) {
						Cell.cells.add(new Cell(name, x, y, mass));
					}
				} else { // cells ����Ʈ�� ������� ��
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

	// ���� ��ȸ �� ����,����
	public void pDisplay() {
		try {
			String sql = "select * from particle";
			rs = stmt.executeQuery(sql); // sql ������ ���� �� ���ƿ��� ��ȸ���� ResultSet ��ü�� ����
			int result = 0;// �˻�Ƚ��

			while (rs.next()) {
				result++; // �˻��� ������ 1�� ����
				// �˻��� �� ���� isTrue�� false�� �ʱ�ȭ������
				boolean isTrue = false;
				String name = rs.getString("pname");
				int x = rs.getInt("px");
				int y = rs.getInt("py");
				int mass = rs.getInt("pmass");
				System.out.println("name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// particles ����Ʈ�� ������� ���� ��
				if (!Particle.particles.isEmpty()) {
					// particles ����Ʈ�� ������ �ϳ��� �˻��Ѵ�.
					for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
						if (it != null) {
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
					}
					// ����Ʈ�� �����Ǿ� ������ ���� �˻����� ���̰� ���� �� ����
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, mass));
					}
				} else { // particles ����Ʈ�� ������� ��
					Particle.particles.add(new Particle(name, x, y, mass));
				}
			}
			particleNo = result;
			System.out.println(particleNo + " ���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
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