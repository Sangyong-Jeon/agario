package 온라인;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import 온라인.게임서버.GameWorld;

public class Cell {

	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;
	public String name;
	public double x, y, mass;
	int speed = 1;

	public int goalX, goalY; // 마우스 좌표

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
		this.x += dx * 1 / 35;
		this.y += dy * 1 / 35;
		
		// 크기제한 설정 가능
		if (this.mass > 3500) {
			this.mass = 3500;
		}
	}

	public void Action() {
		// 크기제한 설정 가능
		if (this.mass > 3500) {
			this.mass = 3500;
		}
		
		
		for (Cell c : cells) {
			// 꺼낸 세포와 부딪혔고, 꺼낸 세포와 이 세포가 같지 안혹, 이 세포의 크기가 꺼낸 세포보다 10보다 더 클 때
			if(checkCollide(c.x, c.y, c.mass) && this != c && this.mass > c.mass + 10) {
				// 현 세포가 꺼낸 세포보다 2.5배 이하지만 크고, 자신의 크기가 4000미만 일 때
				if (1/ (this.mass/c.mass)>= 0.4 && this.mass < 4000 ) {
					addMass(c.mass);
					GameWorld.cmassUpdateDB(name, mass);
				}
				respawn(c);
				GameWorld.cUpdateDB(c.name, c.x, c.y, c.mass);
				GameWorld.cellDisplay(c.name);
			}
		}
	}
	
	// 세포 부활
	public void respawn(Cell c) {
		c.x = (int) Math.floor(Math.random() * 2001);
		c.y = (int) Math.floor(Math.random() * 2001);
		c.mass = 20;
	}

	public boolean checkCollide(double x, double y, double mass) { // 세포와 부딪혔을 때
		return x < this.x + this.mass && // x가 이 세포의 x값 + 크기보다 작고,
				y < this.y + this.mass && // y가 이 세포의 y값 + 크기 보다 작고,
				x + mass > this.x && // x + mass 가 이 세포의 x 보다 크고,
				y + mass > this.y; // y + mass 가 이 세포의 y보다 크면 true, 아니면 false를 리턴한다.
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
		bbg.drawRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.fillRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.setColor(Color.white);
		bbg.drawString(name, ((int) x + (int) mass / 2 - name.length()), ((int) y + (int) mass / 2 + name.length()));
	}
}
