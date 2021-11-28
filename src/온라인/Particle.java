package �¶���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import �¶���.���Ӽ���.GameServer;
import �¶���.���Ӽ���.GameWorld;

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

		for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
			Cell c = it.next();
			if (c.mass < 3500) {
				if (checkCollide(c.x, c.y, c.mass)) {
					c.addMass(this.mass);
					// Ư�� ���� ũ�� DB ������Ʈ
					GameWorld.updateDB("update cell set mass = " + c.mass + " where name = '" + c.name + "'");
					// Ư�� ���� ��ȸ ��ȣ������
					GameServer.broadCasting("Ư��������ȸ" + c.name);
					// Ư�� ���� ���� ��ġ�� ������ �� DB ������Ʈ
					this.x = (int) Math.floor(Math.random() * 5001);
					this.y = (int) Math.floor(Math.random() * 5001);
					GameWorld.updateDB("update particle set x = " + x + ", y =" + y + " where name = '" + pname + "'");
					// Ư�� ���� ��ȸ ��ȣ������
					GameServer.broadCasting("Ư��������ȸ" + pname);
					break;
				}
			} else {
				break;
			}
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
