package 온라인;

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
		this.g2 = (Graphics2D) bbg; // 그래픽을 구현하는 클래스로 Graphics 클래스와 이를 상속하는 Graphics2D클래스가 있음.
		// 이것은 JDK 1.2 이후 추가된 것으로 2D 그래픽 환경을 강력히 지원하는 고수준 API 이다.
	}

	public void set() {
		g2.scale(sX, sY); // 좌표를 x축 방향으로 sX, y축 방향으로 sY만큼 확대하거나 축소한다.
		g2.translate(-x - 100, -y); // 좌표를 x축 방향으로 -x, y축 방향으로 -y만큼 평행이동한다.
	}

	public void unset() {
		this.g2.translate(x, y);
	}

	public void Update(Cell cell) {
		double scaleFactor;
		if (cell.mass < 1000) { // 세포의 크기가 1000보다 작다면
			scaleFactor = map(cell.mass, 10, 1000, 1.2, 0.1);
		} else {
			scaleFactor = 0.1;
		}
		sX = sY = scaleFactor;
		x = ((cell.x + cell.mass * 0.5) - Game.width / sX * 0.5);
		y = ((cell.y + cell.mass * 0.5) - Game.height / sY * 0.5);
	}
}