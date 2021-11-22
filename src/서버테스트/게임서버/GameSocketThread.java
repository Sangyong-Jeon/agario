package �����׽�Ʈ.���Ӽ���;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import �����׽�Ʈ.Cell;
import �����׽�Ʈ.Particle;

public class GameSocketThread extends Thread {
	Socket socket;
	GameServer server;
	BufferedReader in;
	PrintWriter out;
	String name, threadName;
	String str;
	static int pnumber;

	// DB ����
	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;

	public GameSocketThread(GameServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		threadName = super.getName();
		System.out.println(socket.getInetAddress() + "���� �����Ͽ����ϴ�.");
		System.out.println("Thread Name : " + threadName);
	}

	// DB ���� �޼ҵ�
	private static Connection makeConnection() {
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
		String user = "test";
		String password = "1234";

		// JDBC ����̹� ����
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("GameSocketThread�� ����̹� �ε� ����");

		// DB ����
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // ���� �ȵǸ� null
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
					System.out.println("particle �˻� : " + pname + " / " + px + " / " + py);

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

	// db ���� �޼ҵ�
	private static void deleteDB(String name) {
		try {
			String sql = "delete from cell where cname='" + name + "'";
			stmt.executeUpdate(sql);
			System.out.println(name + "�� DB���� ���� �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void allCellDelete() {
		try {
			String sql = "delete from cell";
			stmt.executeUpdate(sql);
			System.out.println("���� ���� DB���� ���� �Ϸ�");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Ŭ���̾�Ʈ���� �޽��� ���
	public void sendMessage(String str) {
		out.println(str);
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// db ����
			try {
				conn = makeConnection();
				stmt = conn.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				System.out.println(pnumber++);
			}

			// �г��� �ޱ�
			name = in.readLine();
			System.out.println(name);

			// ���� DB�� �����ϱ�
			insertDB("cell", name, Integer.toString((int) Math.floor(Math.random() * 10001)),
					Integer.toString((int) Math.floor(Math.random() * 2801)));

			// ���ο� ���� ���������� ��� Ŭ���̾�Ʈ���� cell ���̺� ��ȸ�϶�� ������
			server.broadCasting("c");

			// ��ȸ�ؼ� DB�� �ִ� ���� cells �迭�� ���� �� �־��ִ� �޼ҵ�
			display("cell");

			while (true) {
				try {
					System.out.println("�Է¹ޱ� ��");
					// ���� �� ���̰� �̵��ߴٴ� ���ڸ� ����.
					str = in.readLine();
					System.out.println("������ ���ڹ��� : " + str);

					if (str != null) {
						if (str.indexOf("c") == 0) { // ������ ��
							display("cell");
							System.out.println("������ȸ��");
							server.broadCasting("c");
						} else if (str.indexOf("p") == 0) { // ������ ��
							display("particle");
							System.out.println("������ȸ��");
							server.broadCasting("p");
						}
					} else {
						System.out.println("������ null�� �Է¹���");
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("NullPointerException �߻�");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("IOException �߻�");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println(threadName + " �����߽��ϴ�. ");
			for (Cell c : Cell.cells) {
				if (c.name.equals(name)) {
					System.out.println(c.name + " �����߽��ϴ�. ");
					Cell.cells.remove(c);
				}
			}
			server.removeClient(this);
			e.printStackTrace();
		} finally {
			deleteDB(name);
			if (rs != null) { // rs, stmt, conn�� ���� �ִٸ� �ݾ��ְ�, ���ٸ� �Ѿ��.
				try {
					rs.close();
					System.out.println("rs�ݾ���");
				} // rs�� ���� �־ try���� ���� �ȴ�. rs.close();�� �ý��� �ڿ��� ��ȯ���ش�. �׸��� �ݾҴٴ°� �������.
				catch (SQLException e) {
					e.printStackTrace();
				} // �ƴ϶�� ������ �����̹Ƿ� catch������ ��.
			}
			if (stmt != null) {
				try {
					stmt.close();
					System.out.println("stmt �ݾ���");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
					System.out.println("conn �ݾ���");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
