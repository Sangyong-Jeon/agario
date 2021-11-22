package �¶���;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Camera {

	public double x, y;
	public double sX, sY;
	Graphics2D g2;

	public Camera(double x, double y, double sX, double sY) {
		this.x = x;
		this.y = y;
		this.sX = sX;
		this.sY = sY;
	}

	public double map(double x, double min1, double max1, double min2, double max2) {
		return (x - min1) * (max2 - min2) / (max1 - min1) + min2;
	}
	
	public void Graphics(Graphics bbg) {
		this.g2 = (Graphics2D) bbg; // �׷����� �����ϴ� Ŭ������ Graphics Ŭ������ �̸� ����ϴ� Graphics2DŬ������ ����.
		// �̰��� JDK 1.2 ���� �߰��� ������ 2D �׷��� ȯ���� ������ �����ϴ� ����� API �̴�.
	}

	public void set() {
		g2.scale(sX, sY); // ��ǥ�� x�� �������� sX, y�� �������� sY��ŭ Ȯ���ϰų� ����Ѵ�.
		g2.translate(-x - 100, -y); // ��ǥ�� x�� �������� -x, y�� �������� -y��ŭ �����̵��Ѵ�.
	}

	public void unset() {
		this.g2.translate(x, y);
	}

	public void Update(Cell cell) {
		double scaleFactor;
		if (cell.mass < 1000) { // ������ ũ�Ⱑ 1000���� �۴ٸ�
			scaleFactor = map(cell.mass, 10, 1000, 1.2, 0.1);
		} else {
			scaleFactor = 0.1;
		}
		sX = sY = scaleFactor;
		x = ((cell.x + cell.mass * 0.5) - Game.width / sX * 0.5);
		y = ((cell.y + cell.mass * 0.5) - Game.height / sY * 0.5);
	}
}