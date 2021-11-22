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

import javax.swing.JFrame;

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;

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
		this.setTitle("����Ű���");
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.insets = this.getInsets();
		this.setSize(insets.left + width + insets.right, insets.top + height + insets.bottom);
		this.setLocation(size.width / 2 - getSize().width / 2, size.height / 2 - getSize().height / 2);
		this.setVisible(true);
		this.requestFocus();
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// DB ����
		try {
			conn = makeConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// DB ���� �޼ҵ�
	private static Connection makeConnection() {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

		// 1. JDBC ����̹� ����
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("GameWorld�� ����̹� �ε��� �����߾��!!");

		// 2. �����ͺ��̽� ����
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn; // ������ ���������� �ȴٸ� return���� ���� �����ְ� �޼ҵ带 �����Ѵ�.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // ���������� ������� �ʾҴٸ� ��ȯ���� null �̴�.
	}

	@Override
	public void run() {
		// ���̻���
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
			// ���̻���
			insertDB("particle", Integer.toString(Particle.particleCount), x, y);
			// �����ϰ� ��ȸ�ؼ� pnumber ����
			display("particle");
			System.out.println(pnumber);
		}
	}

	// DB��ȸ�ؼ� �� �迭�� DB�����Ͱ� ������ �����ؼ� �־��ش�.
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
					System.out.println("������� " + cname + " , " + cx + " , " + cy);
					if (!Cell.cells.isEmpty()) { // cells�� �Ⱥ���� ��
						for (Cell c : Cell.cells) {
							if (c.name.equals(cname)) { // ����Ʈ�ȿ� ���� �˻����� ������ �̸��� ���� ��
								c.x = Integer.parseInt(cx);
								c.y = Integer.parseInt(cy);
								isPlayer = true;
								break;
							} else { // ����Ʈ �ȿ� ���� �˻����� ������ �̸��� ���� ���� ��

							}
						}
						if (!isPlayer) { // ����Ʈ �ȿ� �˻��� name�� ���� �� ����
							Cell.cells.add(new Cell(cname, Integer.parseInt(cx), Integer.parseInt(cy)));
						}
					} else { // cells�� ����� ��
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
					if (!pCopy.isEmpty()) { // pCopy�� �Ⱥ���� ��
						for (Particle p : pCopy) {
							if (p.pname.equals(pname)) { // ����Ʈ �ȿ� ���� �˻����� ���̿� �̸��� ���� ��
								p.x = Integer.parseInt(px);
								p.y = Integer.parseInt(py);
								isParticle = true;
								break;
							} else { // ����Ʈ �ȿ� ���� �˻����� ���̿� �̸��� ���� ���� ��

							}
						}
						if (!isParticle) { // ����Ʈ �ȿ� �˻��� pname�� ���� �� ����
							Particle.particles.add(new Particle(pname, Integer.parseInt(px), Integer.parseInt(py), 1));
						}
					} else { // pCopy�� ����� ��
						Particle.particles.add(new Particle(pname, Integer.parseInt(px), Integer.parseInt(py), 1));
					}
				}
				pnumber = result;
			}
			System.out.println("�˻��� �� " + pnumber);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ���� �޼ҵ�
	private static void insertDB(String table, String name, String x, String y) {
		try {
			String sql = "insert into " + table + " values ('" + name + "', '" + x + "', '" + y + "')";
			stmt.executeUpdate(sql);
			System.out.println("���̺� : " + table + " x��ǥ : " + x + " y��ǥ : " + y + "���� �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// �׸���
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
