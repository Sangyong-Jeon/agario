package �����׽�Ʈ.����Ŭ���̾�Ʈ;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;

public class Client extends JPanel implements Runnable {
	// ��ſ�
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String str, userName;

	ClientMain main;

	// -----------DB ����
	// ���� �ʵ�� ���� �ٸ� �޼ҵ忡���� ���������� �� �� �ְ� �Ͽ���.
	static Connection conn = null; // Connection�� ����� ���õ� ������ ����� �����ִ� Ŭ����
	static Statement stmt = null; // Statement Ŭ������ SQL ������ �����ϴ� ����, �����δ� SQL ���и���(�����ؼ�X), ���޿����� �Ѵ�.
	// SQL ���� O + ���� ���� X
	// Statement��ü�� Connection��ü�� �޼ҵ带 �̿��Ͽ� �����ϵ��� ����Ǿ� �ִ�.
	static ResultSet rs = null; // select ���� ��ȸ �������� ������ �� ���ƿ��� ��ȸ ���� �����ϴ� Ŭ�����̴�.
	// ����� ������ �����ʹ� Table ���¿� ���
	// ResultSet�� next()�� �̿��Ͽ� ���� �ִ��� ������ Ȯ��.
	// next()���� �� get() �޼ҵ带 �̿��Ͽ� ���� ����. ������ ������ �ݺ��� ���
	private static int pnumber;

	public Client(String ip, int port, ClientMain main, String name) {
		this.userName = name;
		this.main = main;
		System.out.println("Client�� userName : " + userName);
		init();
//		start();
		this.setVisible(true);
		initNet(ip, port);
		System.out.println("ip = " + ip);
	}

	public void init() {
		this.setLayout(new BorderLayout());
	}

	public void initNet(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			System.out.println("IP �ּҰ� �ٸ��ϴ�.");
		} catch (IOException e) {
			System.out.println("���� ����");
		}
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		// �г��� �����ֱ�
		out.println(userName);
		System.out.println("Client�� run()���� userName : " + userName);

		// db ����
		try {
			conn = makeConnection();
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// ���� ��ȸ�ؼ� pnumber ����
		display("particle");

		// ���� DB 5000�� �����ϱ�
		while (pnumber < 5000) {
			String x = Integer.toString((int) Math.floor(Math.random() * 10001));
			String y = Integer.toString((int) Math.floor(Math.random() * 2801));
			insertDB("particle", Integer.toString(pnumber), x, y);
			// �����ϰ� ��ȸ�ؼ� pnumber ����
			display("particle");
			System.out.println(pnumber);
		}

		main.isDB = true;

		while (true) { // ���� �ݺ�
			try {
				str = in.readLine(); // ���� �Է¹޾Ƽ� ���� or ���� ��ȸ �����ϱ�
				System.out.println("�Է¹��� : " + str);
				if (str != null) {
					if (str.equals("c")) { // ������ ��
						display("cell"); // db ��ȸ �� �����迭�� ���� �� ����
					} else if (str.equals("p")) { // ������ ��
						display("particle"); // db ��ȸ �� ���̹迭�� ���� �� ����
					}
				} else {
					System.out.println("null�� �Է¹���");
				}
			} catch (NullPointerException e) {
				System.out.println("NullPointerException �߻�");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException �߻�");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// DB ���� �޼ҵ�
	private static Connection makeConnection() { // �����ͺ��̽��� �������ִ� �޼ҵ��̴�.
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
			return conn; // ������ ���������� �ȴٸ� return���� ���� �����ְ� �޼ҵ带 �����Ѵ�.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // ���������� ������� �ʾҴٸ� ��ȯ���� null �̴�.
	}

	// DB��ȸ�ؼ� �� �迭�� DB�����Ͱ� ������ �����ؼ� �־��ش�.
	private static void display(String table) {
		try {
			String sql = "select * from " + table + "";
			rs = stmt.executeQuery(sql);

			if (table.equals("cell")) { // ��ȸ�ϴ°��� ������ ��
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
			} else if (table.equals("particle")) { // ��ȸ�ϴ� ���� ������ ��
				int result = 0; // �˻��Ǵ� ���� ����
				while (rs.next()) {
					boolean isParticle = false; // �˻� �Ҷ����� false�� �ʱ�ȭ�ؼ� pCopy�ȿ� �ִ��� ������ Ȯ���Ͽ� ������ ������Ŵ
					String pname = rs.getString("pname");
					String px = rs.getString("px");
					String py = rs.getString("py");
					result += 1; // �˻� �� �� ���� 1�� ����

					ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
					if (!pCopy.isEmpty()) { // pCopy�� �Ⱥ���� ��
						for (Particle p : pCopy) {
							if (p.pname.equals(pname)) { // pCopy �ȿ� ���� �˻����� ���̿� �̸��� ���� ��, db�� ��ǥ�� �������ش�.
								p.x = Integer.parseInt(px);
								p.y = Integer.parseInt(py);
								isParticle = true; // pCopy �ȿ� �����Ƿ� true�� �ٲ㼭 �������� ����
								break; // pCopy�ȿ� ���� �����Ƿ� ��ȸ ����
							} else { // ����Ʈ �ȿ� ���� �˻����� ���̿� �̸��� ���� ���� ��

							}
						}
						if (!isParticle) { // pCopy �ȿ� �˻��� pname�� ���� �� ����
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
			System.out.println("���̺� : " + table + " x��ǥ : " + x + " y��ǥ : " + y + " ���� �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// �������� �޼ҵ�
	public static void updateDB(String table, String name, String x, String y) {
		try {
			String sql = "update " + table + " set cx = '" + x + "', cy = '" + y + "' where cname='" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println("���� ������Ʈ �Ϸ� : " + name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
