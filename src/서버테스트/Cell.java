package 서버테스트;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Cell {

	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;
	public String name;
	public int x, y;
	double mass = 10;
	int speed = 1;
	
	public int goalX, goalY; // 마우스 좌표

	Color cellColor;

	public Cell(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.randomColor();
		cellCount++;
	}
	
	public void Update() {
		int dx = (goalX - this.x);
		int dy = (goalY - this.y);
		this.x += dx*1/35;
		this.y += dy*1/35;
	}

	public void randomColor() {
		// r,g,b에 0~255의 값을 랜덤으로 넣고 Color로 생성한다.
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		this.cellColor = new Color(r, g, b); // 랜덤으로 생성한 Color를 세포의 인스턴스 변수에 넣는다.
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
		bbg.drawRect( x,  y, (int) mass, (int) mass);
		bbg.fillRect( x,  y, (int) mass, (int) mass);
		bbg.setColor(Color.white);
		bbg.drawString(name, (x+(int)mass/2-name.length()), (y+(int)mass/2+name.length()));
	}
}
