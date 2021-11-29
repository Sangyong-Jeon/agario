package �¶���;

import java.awt.Graphics;
import java.awt.Graphics2D;

import �¶���.����Ŭ���̾�Ʈ.ClientMain;

public class Camera {
	public double x, y;
	public double sX, sY;
	Graphics2D g2;
	int noX = 0;
	int noY = 0;
	
	public Camera(double x, double y, double sX, double sY) {
		this.x = x;
		this.y = y;
		this.sX = sX;
		this.sY = sY;
	}
	// map(����ũ��, ���� �� ���� ũ��, 1000, 1.2, 0.1)
	public double map(double x, double min1, double max1, double min2, double max2) {
		// (����ũ�� - 10) * 1.1 / 991.2
		// ����ũ�� 10 ~ 999 ����
		// ���ϰ� 0.0011097659402744 ~ 1.097558514931396 ����
		return (x - min1) * (max2 - min2) / (max1 - min1) + min2;
	}
	
	public void Graphics(Graphics bbg) {
		this.g2 = (Graphics2D) bbg;
	}
	
	public void set() {
		
		// ��ǥ�� x�� �������� sX, y�� �������� sY��ŭ Ȯ���ϰų� ���
		// �Ҽ����� ���, 1�谡 �������
		g2.scale(sX, sY); 
		g2.translate(-x - 100 ,  -y); // ��ǥ�� x�� �������� -x, y�� �������� -y��ŭ �����̵��Ѵ�.
	}
	
	public void unset() {
		g2.translate(x, y);
	}
	
	public void Update(Cell cell) {
		double scaleFactor;
		if (cell.mass < 1000 ) {
			scaleFactor = map(cell.mass, 10, 1000, 1.2, 0.1);
			noX = 100;
		} else {
			scaleFactor = 0.1;
			noX = 2600;
			noY = 2400;
		}
		sX = sY = scaleFactor;
		x = ((cell.x + cell.mass * 0.5) - ClientMain.width / sX * 0.5);
		y = ((cell.y + cell.mass * 0.5) - ClientMain.height / sY * 0.5);
	}
}
