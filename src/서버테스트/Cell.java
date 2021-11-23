package �����׽�Ʈ;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Cell {

	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;
	public String name;
	public double x, y, mass;
	int speed = 1;
	
	public int goalX, goalY; // ���콺 ��ǥ

	Color cellColor;

	public Cell(String name, int x, int y, int mass) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.mass = mass;
		this.randomColor();
		cellCount++;
	}
	
	public void Update() {
		double dx = (goalX - this.x);
		double dy = (goalY - this.y);
		this.x += dx*1/100;
		this.y += dy*1/100;
	}

	public void randomColor() {
		// r,g,b�� 0~255�� ���� �������� �ְ� Color�� �����Ѵ�.
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		this.cellColor = new Color(r, g, b); // �������� ������ Color�� ������ �ν��Ͻ� ������ �ִ´�.
	}
	
	public void addMass(double mass) {
		this.mass += mass;
	}
	
	public void getMouseX(int mx) {
		goalX = mx;
	}
	
	public void getMouseY(int my) {
		goalY = my;
	}
	
	public void Draw(Graphics bbg) {
		bbg.setColor(cellColor);
		bbg.drawRect( (int)x,  (int)y, (int) mass, (int) mass);
		bbg.fillRect( (int)x,  (int)y, (int) mass, (int) mass);
		bbg.setColor(Color.white);
		bbg.drawString(name, ((int)x+(int)mass/2-name.length()), ((int)y+(int)mass/2+name.length()));
	}
}
