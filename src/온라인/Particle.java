package �¶���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

// �ʿ� �������� �����Ǵ� ����
public class Particle {
	// Ŭ���� ���� (static ����, ��������)
	public static ArrayList<Particle> particles = new ArrayList<Particle>(); // Particle Ÿ���� ArrayList ����
	public static int particleCount; // ���� ���� ��������� ���Ҷ� ���� ��
	// �ν��Ͻ� ����
	public int x, y, mass; // �⺻������ �����Ҷ� ���� ��
	public double speed, angle, dx, dy; // ����ڰ� �Ѹ��� ���̰� ���� ����
	

	private boolean cellParticle = false; // �⺻���� �������� ����ڰ� �Ѹ��� �������� Ȯ�ο�
	private boolean die = false;
	public boolean isShot;

	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));
	// Color�� ���� ǥ���ϱ� ���� ���Ǵ� Ŭ����
	// ���ϴ� ���� RGB ���� �˰� ������ �����Ͽ� ��밡��
	// Color(int r, int g, int b) �� r-red, g-green, b-blue ��� 0~255������ �������� ����.
	// Color(float r, float g, float b) �� ��� 0.0 ~ 1.0 ������ �Ǽ����� ����.
	// Color(int r, int g, int b, int a) �� a-alpha������ 0~255 �������� �ְ�, float�̸�
	// 0.0~1.0���� �Ǽ���, alpha�� �������� ����. Ŭ���� ����������.

	// Math Ŭ�������� floor�� ����, ceil�� �ø�, round�� �ݿø�
	// Math Ŭ������ �����޼ҵ��� random() �޼ҵ�� ������ �߻���Ŵ
	// random()�� 0.0�̻� 1.0�̸��� double ���� ������ ������ ������ ��ȯ�Ѵ�. 0~ 9���� ������ ������ ����� ������
	// double�� ������ 10�� ���� �� ������ ĳ�����ϸ� �ȴ�.
	// �� ���� (int)Math.floor(Math.random() * 256) �� 0.0 �̻� 1.0 �̸��� ���� 256���� ���ؼ� ������ ��
	// int�� ������ ĳ�����ϹǷ� 0~255���� ���´�.
	public Particle(int x, int y, int mass, boolean p) { // Particle �������̴�.
		// ���� Game Ŭ�������� particles�� �� Particle ��ü�� 5000�� �̸��϶� Particle( (0~10000)���̰�,
		// (0~10000)���̰� , 1 , false)�� �ڵ�������.
		particleCount++; // Ŭ���� int ����
		this.x = x; // �ν��Ͻ� int ����
		this.y = y; // �ν��Ͻ� int ����
		this.mass = mass; // �ν��Ͻ� int ����
		cellParticle = p; // �ν��Ͻ� boolean ����
	}

	public void Update() {
		for (Cell cell : Cell.cells) { // for-each������ CellŬ���� ���� Ŭ���� ������ public static ArrayList<Cell> cells �ȿ� �ִ� ������
										// �ϳ��� �־� ������.
			// this.checkCollide(cell.x,cell.y,cell.mass)�� cell�� x, y, mass�� ���� Particle�� ����
			// ���ؼ� boolean���� ��ȯ�Ѵ�.
			if (this.checkCollide(cell.x, cell.y, cell.mass) && !cellParticle) { // �⺻������ ���̰� ������ ������ ��
				if (cell.mass <= 200) { // ������ ũ�Ⱑ 200 �����϶�
					cell.addMass(this.mass); // ������ ũ�⿡ ������ ũ�⸦ ���Ѵ�
				} else { // ������ ũ�Ⱑ 200�̻��϶�
					cell.isTarget = false;
					cell.goalReached = true;
					cell.targetType = "c";
				}
				if (cell.targetType.equals("p")) { // �� ������ ��ǥ�ϴ� ����Ÿ���̶� ���Ʊ⿡ isTarget�� false�� goalReached�� true�� �ٲ��ذ���.
													// ����Ÿ���̶� �������� Ÿ���� ����ϸ� �ȵǴϱ� �̷��� �� ��.
					cell.isTarget = false;
					cell.goalReached = true;
				}
				// �������� �����⿡ �����ؾ������� ���������ʰ� ��ġ�� ������ �ؼ� ������ ��ġ�� �Ű��ش�.
				this.x = (int) Math.floor(Math.random() * 10001); // Particle�� x�� (0 ~ 10,000)�� ���� int������ ��������ȯ �� �ִ´�.
																	// (������ 0���� 1�̸�)
				this.y = (int) Math.floor(Math.random() * 10001); // ���� ����. y�� int�� 0~10,000 ���� �ִ´�.
			} else if (this.checkCollide(cell.x, cell.y, cell.mass) && cellParticle && !cell.isPlayer) { // �÷��̾ �Ѹ� ���̴� AI������ ���� �� �ִ�.
				// �÷��̾ �Ѹ� ���̸� AI ������ �Ծ��� �� �����Ѵ�.
				cell.addMass(this.mass);
				this.die = true;
			}
		}
		if (isShot) {
			dx = (speed) * Math.cos(angle);
			dy = (speed) * Math.sin(angle);
			x += dx;
			y += dy;
			speed -= 0.3;
			if (speed <= 0) {
				isShot = false;
				speed = 0;
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

	public void Draw(Graphics bbg) { // x��ǥ�� y��ǥ�� ���׶�̸� �׸���.
		bbg.setColor(color);
		bbg.fillOval(x, y, 10, 10);
		bbg.drawOval(x, y, 10, 10);
	}

	public boolean getHealth() {
		return die;
	}
}