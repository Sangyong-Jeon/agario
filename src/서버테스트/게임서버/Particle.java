package �����׽�Ʈ.���Ӽ���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Particle {
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public static int particleCount; // ���� ���� ����
	public int x, y, mass;
	public String pname;
	public boolean isTrue = false;

	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));

	public Particle(String pname, int x, int y, int mass) {
		particleCount++;
		this.pname = pname;
		this.x = x;
		this.y = y;
		this.mass = mass;
	}

	public void Update() {
		boolean isSend = false;
		for (Cell c : Cell.cells) {
			if (checkCollide(c.x, c.y, c.mass)) {
				c.addMass(this.mass);
				GameWorld.cmassUpdateDB(c.name, c.mass);
				GameServer.broadCasting("u"+c.name);
				// �������� ������ġ�� ������
				this.x = (int) Math.floor(Math.random() * 1000);
				this.y = (int) Math.floor(Math.random() * 1000);
				// DB�� ���� ��ǥ ����
				GameWorld.pUpdateDB(pname, x, y, mass);
				// Ư�� ���̸� �����ߴٰ� ��� Ŭ���̾�Ʈ���� ����
				GameServer.broadCasting("s"+pname);
				isSend = true;
			}
		}
		// ���� ��ǥ �����Ǿ����� ��� Ŭ���̾�Ʈ���� ������ǥ �����϶�� ����
		if(isSend ) {
			GameServer.broadCasting("p");
		}
	}

	private boolean checkCollide(double x, double y, double mass) { // boolean ���� ��ȯ�Ѵ�.
		// ���⼭ �� 10�� �����ִ°�? �װ��� ������ ���ο� ���� ���̰� 10�̱� �����̴�.
		return x < this.x + 10 && // ������ x��ǥ�� ������ x��ǥ+10 ���� �۰�
				x + mass > this.x && // ������ x��ǥ + ũ�Ⱑ ������ x��ǥ���� Ŭ��
				y < this.y + 10 && // ������ y��ǥ�� ������ y��ǥ+10 ���� �۰�
				y + mass > this.y; // ������ y��ǥ + ũ�Ⱑ ������ y��ǥ���� Ŭ��
	} // �� ������ ũ��ȿ� ������ �������� true�� ��ȯ�Ѵ�.

	public void Draw(Graphics bbg) {
		bbg.setColor(color);
		bbg.fillOval(this.x, this.y, 10, 10);
		bbg.drawOval(this.x, this.y, 10, 10);
	}

}
