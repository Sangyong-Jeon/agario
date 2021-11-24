package �¶���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import �¶���.���Ӽ���.GameWorld;

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
		this.x += dx * 1 / 35;
		this.y += dy * 1 / 35;
		
		// ũ������ ���� ����
		if (this.mass > 3500) {
			this.mass = 3500;
		}
	}

	public void Action() {
		// ũ������ ���� ����
		if (this.mass > 3500) {
			this.mass = 3500;
		}
		
		
		for (Cell c : cells) {
			// ���� ������ �ε�����, ���� ������ �� ������ ���� ��Ȥ, �� ������ ũ�Ⱑ ���� �������� 10���� �� Ŭ ��
			if(checkCollide(c.x, c.y, c.mass) && this != c && this.mass > c.mass + 10) {
				// �� ������ ���� �������� 2.5�� �������� ũ��, �ڽ��� ũ�Ⱑ 4000�̸� �� ��
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
	
	// ���� ��Ȱ
	public void respawn(Cell c) {
		c.x = (int) Math.floor(Math.random() * 2001);
		c.y = (int) Math.floor(Math.random() * 2001);
		c.mass = 20;
	}

	public boolean checkCollide(double x, double y, double mass) { // ������ �ε����� ��
		return x < this.x + this.mass && // x�� �� ������ x�� + ũ�⺸�� �۰�,
				y < this.y + this.mass && // y�� �� ������ y�� + ũ�� ���� �۰�,
				x + mass > this.x && // x + mass �� �� ������ x ���� ũ��,
				y + mass > this.y; // y + mass �� �� ������ y���� ũ�� true, �ƴϸ� false�� �����Ѵ�.
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
		bbg.drawRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.fillRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.setColor(Color.white);
		bbg.drawString(name, ((int) x + (int) mass / 2 - name.length()), ((int) y + (int) mass / 2 + name.length()));
	}
}
