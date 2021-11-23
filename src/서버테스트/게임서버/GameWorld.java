package �����׽�Ʈ.���Ӽ���;

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

	// DB ����
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	// DB�� �ִ� ���̰���
	static int particleNo = 0;

	public GameWorld() {
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
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// DB ����
		makeConnection();

		// DB���� ���� ��ȸ
		pDisplay();

		// DB ���� ������ 5000�� �̸��� �� ���� ����
		while (particleNo < 5000) {
			insertDB("particle", "p" + particleNo++, (int) Math.floor(Math.random() * 5000),
					(int) Math.floor(Math.random() * 5000), 1);
		}
		pDisplay();

		// DB���� ���� ��ȸ
		cDisplay();

		// ���� �ݺ�
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
		// ���� ����
		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			Particle p = it.next();
			p.Update();
		}
		System.out.println("���� �Ϸ�");
	}

	// �׸���
	public void draw() {
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black);
		bbg.fillRect(0, 0, width, height);

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		if (!pCopy.isEmpty()) { // �� ������� ��
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
		System.out.println("�׸��� �Ϸ�");
	}

	// ------------------------------------------------DB ���� �޼ҵ�

	public static void makeConnection() { // conn�� stmt�� �������ش�.
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
	public static void cDisplay() {
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
			System.out.println("GameWorld���� SQLException �߻�");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("GameWorld���� Null ���ܹ߻�");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("GameWorld���� ���� �߻�");
			e.printStackTrace();
		}
	}

	// ���� ��ȸ �� ����,����
	public static void pDisplay() {
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
		} catch (NullPointerException e) {
			System.out.println("Null ���� �߻�");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("���� �߻�");
			e.printStackTrace();
		}
	}

	// DB ����
	public static void insertDB(String table, String name, int x, int y, int mass) {
		try {
			String sql = "insert into " + table + " values('" + name + "', " + x + " , " + y + ", " + mass + ")";
			stmt.executeUpdate(sql);
			System.out.println("���̺� : " + table + " �̸� : " + name + " ������Ʈ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// ���� DB ����
	public static void cUpdateDB(String table, String name, double x, double y, double mass) {
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

	// ���� DB ����
	public static void pUpdateDB(String table, String name, double x, double y, double mass) {
		try {
			String sql = "update particle set px = " + x + ", py = " + y + ", pmass = " + mass + " where pname = '"
					+ name + "'";
			stmt.executeUpdate(sql);
			System.out.println("���̺� : " + table + " �̸� : " + name + " ������Ʈ �Ϸ�");
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// DB���� ����
	public static void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname = '" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "���� DB���� �����Ǿ����ϴ�.");
		} catch (SQLException e) {

		}
	}

}
