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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;

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

	// ���� �ʵ�� ���� �ٸ� �޼ҵ忡���� ���������� �� �� �ְ� �Ͽ���.
	public static Connection conn = null; // Connection�� ����� ���õ� ������ ����� �����ִ� Ŭ����
	public static Statement stmt = null; // Statement Ŭ������ SQL ������ �����ϴ� ����, �����δ� SQL ���и���(�����ؼ�X), ���޿����� �Ѵ�.
	// SQL ���� O + ���� ���� X
	// Statement��ü�� Connection��ü�� �޼ҵ带 �̿��Ͽ� �����ϵ��� ����Ǿ� �ִ�.
	public static ResultSet rs = null; // select ���� ��ȸ �������� ������ �� ���ƿ��� ��ȸ ���� �����ϴ� Ŭ�����̴�.
	// ����� ������ �����ʹ� Table ���¿� ���
	// ResultSet�� next()�� �̿��Ͽ� ���� �ִ��� ������ Ȯ��.
	// next()���� �� get() �޼ҵ带 �̿��Ͽ� ���� ����. ������ ������ �ݺ��� ���

	public boolean isDB = false;

	public ClientMain(String nick) {
		this.name = nick;
		// initialize(); â����� �κ�
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

		// ���� �����ϴ� �κ�
		connection(this, name);

//		// DB ����
//		try {
//			conn = makeConnection();
//			stmt = conn.createStatement();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		// �׸���
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
	public void mouseDragged(MouseEvent e) { // ���콺 �巡���Ҷ���
	}

	@Override
	public void mouseMoved(MouseEvent e) { // ���콺 �̵��Ҷ���
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
		for (Cell cell : Cell.cells) { // cells �迭 �� �� ���� ã��
			if (cell.name.equals(name)) { // �� ������ ã�Ƽ� x, y ��ǥ�� ����
				this.x = cell.goalX;
				this.y = cell.goalY;
				break;
			}
		}
		// �� ���� x,y��ǥ�� ���� �� DB�� ����
		Client.updateDB("cell", name, Integer.toString(x), Integer.toString(y));
		// ���� �� ������ �������ٴ� ��ȣ�� ��.
		client.out.println("c");

	}
}