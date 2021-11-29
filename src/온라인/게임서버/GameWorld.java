package �¶���.���Ӽ���;

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
import �¶���.Cell;
import �¶���.Particle;

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
		pDisplay("select * from particle");

		// DB ���� ������ 5000�� �̸��� �� ���� ����
		while (particleNo < 5000) {
			int x = (int) Math.floor(Math.random() * 5000);
			int y = (int) Math.floor(Math.random() * 5000);
			insertDB("insert into particle values('" + particleNo++ + "'," + x + "," + y + ", 1)");
		}
		pDisplay("select * from particle");

		// DB���� ���� ��ȸ
		cDisplay("select * from cell");

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

		for (Cell c : Cell.cells) {
			c.Action();
		}
	}

	// �׸���
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
	}

	// ------------------------------------------------DB ���� �޼ҵ�

	public static void makeConnection() { // conn�� stmt�� �������ش�.
		// 172.26.1.93
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
				System.out.println("��ȸ ��� name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
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
				int mass = rs.getInt("mass");
				System.out.println("��ȸ ��� name : " + name + " x : " + x + " y : " + y + " mass : " + mass);
				// particles�� ������� ���� ��
				if (!Particle.particles.isEmpty()) {
					// particles ���� �ϳ��� ���� �˻��Ѵ�.
					for (Particle p : Particle.particles) {
						if (p.pname.equals(name)) {
							isTrue = true;
							break;
						}
					}
					// DB���� �˻��� ���̰� ����Ʈ �ȿ� ���� �� ����
					if (!isTrue) {
						Particle.particles.add(new Particle(name, x, y, mass));
					}
				} else { // particles�� ��� ���� �� ����
					Particle.particles.add(new Particle(name, x, y, mass));
				}
			}
			particleNo = sum;
			System.out.println("���� DB ��ȸ �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// DB ����
	public static void insertDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// DB ����
	public static void updateDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
		} catch (SQLException e) {
			System.out.println("SQLException �߻�");
			e.printStackTrace();
		}
	}

	// DB���� ����
	public static void deleteDB(String str) {
		try {
			stmt.executeUpdate(str);
			System.out.println(str);
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
