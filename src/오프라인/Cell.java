package ��������;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Cell {
	// Ŭ���� ���� ����
	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;
	public static boolean stop = false;
	public String name;
	double x, y;
	double mass = 10; // ������ �⺻ ũ��� 10
	int speed = 1; // ������ �⺻ ���ǵ�� 1
	boolean isPlayer = false;

	Color cellColor; // ������ �� ����
	Cell target; // Cell Ÿ�� �������� ����
	Particle pTarget; // Particle Ÿ�� �������� ����

	boolean isTarget = false;
	String targetType = "p"; // �⺻������ ���̸� Ÿ������ ����

	double goalX, goalY; // ���콺 ��ǥ
	boolean goalReached = true;

	public Cell(String name, int x, int y, boolean isPlayer) { // ���� ������(�̸�, x��ǥ, y��ǥ, �÷��̾��)
		this.name = name;
		this.x = x;
		this.y = y;
		this.isPlayer = isPlayer;
		this.randomColor(); // �� ������ Color���� �������� �������ִ� �޼ҵ�
		cellCount++; // ���� count ����
	}

	public void randomColor() {
		// r,g,b�� 0~255�� ���� �������� �ְ� Color�� �����Ѵ�.
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		this.cellColor = new Color(r, g, b); // �������� ������ Color�� ������ �ν��Ͻ� ������ �ִ´�.
	}

	public void addMass(double mass) { // �Ű������� �޴� ũ�⸦ ������ ũ�⿡ �����ش�.
		this.mass += mass;
	}

	// �� �������� �۰� ����� ������ �ε������� ��ȯ�ϰ�, �� �������� ���� ������ ���� cellCount�� cells �迭�� ���̿� ���� ���
	// -1�� ��ȯ�ϴ� �޼ҵ�
	public int returnNearestCell() {
		int x = 0;
		int distance = 15000;
		int min = distance;
		for (Cell cell : cells) { // cells �迭�� �ִ� ������ �ϳ��� �����ͼ� ����.
			if (this != cell) { // �迭���� ���� ������ �� ������ �ƴ� ���
				// Math.sqrt() �޼ҵ�� �Ű������� ������ �������� ��ȯ�Ѵ�. ��, �־��� ���ڿ� ��Ʈ�� �����.
				// �� ������ cells���� ���� ������ �Ÿ��� ���Ѵ�. (x^2 + y^2)�� ���������� �밢���� ����, �� ��Ÿ����� ������ ���Ѵ�.
				distance = (int) Math
						.sqrt((this.x - cell.x) * (this.x - cell.x) + (cell.y - this.y) * (cell.y - this.y));
				if (distance < min && this.mass > cell.mass + 10) { // �� ������ cells�迭���� ���� ������ �Ÿ��� 9999999���� �۰�, �� ������
																	// ũ�Ⱑ ���� ������ ũ��+10���� Ŭ���
					min = distance;
					x = cells.indexOf(cell); // cells �迭�� ���� ���� ������ �ε������� ������ x�� �ִ´�.

					// ArrayList.size() �޼ҵ�� �����ΰ�?
					// �迭�� ũ��� length �Ӽ����� ������ ������ ArrayList�� ������ ���̸� ������ ���� �ʾƼ�
					// size()�Լ��� ����Ͽ� ����Ʈ�� ����ִ� ������ ���� ��´�.
					// �迭���� ���� ����ũ��+10�� �� �������� ũ��, �迭���� ���� ������ cellCount ���� cells�迭�� ���� ���� ���� ��
				} else if (distance < min && this.mass < cell.mass + 10 && cellCount == cells.size()) {
					x = -1;
				}
			}
		}
		return x; // cells �迭���� ���� �ε����� or -1�� ��ȯ��.
	}

	public int returnNearestP() { // ������ ������ ������ �ε����� ��ȯ
		int x = 0;
		int distance = 99999999;
		int min = distance;
		
		for(int i = 0; i < Particle.particles.size(); i++) {
			Particle p = Particle.particles.get(i);
			distance = (int) Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
			if (distance < min && this.mass > p.mass) { // ������ ������ �Ÿ��� 99,999,999���� �۰� ������ ũ�Ⱑ ������ ũ�⺸�� Ŭ ��
				min = distance;
				x = Particle.particles.indexOf(p); // x�� particles�迭���� ���� ���� ���� p�� �ε��� ���� �ִ´�.
			}
		}

//		for (Particle p : Particle.particles) { // ������ ArrayList �迭�� particles���� �ϳ��� ������ p�� �����Ͽ� for-each���� ������.
//			// (�� ������ x��ǥ - ������ x��ǥ)^2 + (�� ������ y��ǥ - ������ y��ǥ)^2 �� ���� �������� distance�� �ִ´�.
//			// ��, ������ ������ �����Ÿ��� ����
//			distance = (int) Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
//			if (distance < min && this.mass > p.mass) { // ������ ������ �Ÿ��� 99,999,999���� �۰� ������ ũ�Ⱑ ������ ũ�⺸�� Ŭ ��
//				min = distance;
//				x = Particle.particles.indexOf(p); // x�� particles�迭���� ���� ���� ���� p�� �ε��� ���� �ִ´�.
//			}
//
//		}
		return x; // ��ȯ�Ǵ� x ���� �� �������� �۰� �Ÿ��� ����� ������ particles�� �ε��� ���� ��ȯ�Ѵ�.
	}

	public void Update() {
		if (this.mass > 3500) { // ������ ũ�Ⱑ 3500���� ũ�ٸ�
			this.mass = 3500; // ������ ũ�� 3500 ����
		}

		for (Cell cell : cells) {
			// ���� ������ �ε�����,���������� �� ������ ���� �ʰ�, �̼����� ũ�Ⱑ ���������� ũ��+10���� Ŭ��
			if (this.checkCollide(cell.x, cell.y, cell.mass) && this != cell && this.mass > cell.mass + 10) {
				if (1 / (this.mass / cell.mass) >= .4 && this.mass < 4000) { // �ڽ��� ����ũ�Ⱑ ���� ������ ũ�⺸�� 2.5�� �������� ũ��, �� ����ũ�Ⱑ
																				// 4000 �̸��� ��
					addMass(cell.mass); // ���� ������ ũ�⸦ �ڽ��� ���� ũ�⿡ ���Ѵ�.
				}
				respawn(cell); // ���� ������ ������ ��ġ�� ������ ��Ų��.
			}
		}

		if (!isPlayer) { // �� ������ �÷��̾ �ƴ� ��
			if (goalReached) { // true�� �� ������ �̰� ������ �������� false�� �ٲ���.
				if (returnNearestCell() > -1) { // �� �������� �۰� ����� ������ ���� ���
					if (!isTarget) { // �� AI������ Ÿ���� ���� ���
						target = cells.get(returnNearestCell()); // ������ ���� ������ Ÿ������ ����
						isTarget = true; // ������ Ÿ���� �������� �ٲ�
						targetType = "c"; // ������ Ÿ������ �����ߴٴ� ��
					} else if (isTarget && targetType.equals("c")) { // �� AI������ ����Ÿ���� ���� ���
						targetType = "n";
						isTarget = false;
					}
				} else if (returnNearestCell() == -1) { // �� �������� ũ��, ������, cellCount�� cells �迭���̶� ���� ���
					if (!isTarget) { // �� AI������ Ÿ���� ���� ���
						pTarget = Particle.particles.get(returnNearestP()); // ����� �Ÿ��� ���̸� pTarget���� ����
						isTarget = true; // ������ Ÿ���� �������� �ٲ�
						targetType = "p"; // ���̸� Ÿ������ �����ߴٴ� ��
					} else if (isTarget) { // �� AI������ ���� Ÿ���� ���� ���
						targetType = "n";
						isTarget = false;
					}
				}
				goalReached = false; // goalReached�� false�� �ٲ�
			} else { // goalReached�� false�� ���, �� �������� ��
				double dx = 0;
				double dy = 0;
				if (targetType.equals("c")) { // ����Ÿ���� ���
					if (returnNearestCell() > -1) { // �� �������� �۰� ����� ������ ���� ��
						target = cells.get(returnNearestCell()); // Ÿ���� �� ������ �ٲ۴�.
						dx = (target.x - this.x);
						dy = (target.y - this.y);
					} else { // �� �������� �۰� ����� ������ ������
						goalReached = true;
					}
				} else if (targetType.equals("p")) { // ����Ÿ���� ���
					pTarget = Particle.particles.get(returnNearestP()); // �� �������� �۰� ����� ���̸� Ÿ������ ����
					dx = (pTarget.x - this.x);
					dy = (pTarget.y - this.y);
				}
				double distance = Math.sqrt((dx) * (dx) + (dy) * (dy));
				if (distance > 1) { // Ÿ�ٰ� �ڽ��� �Ÿ��� 1 �ʰ��� �� �̵��ӵ� ����
					x += (dx) / distance * 5;
					y += (dy) / distance * 5;
				} else { // 1 ������ ��
					goalReached = true;
				}
			}
		} else { // ������ �÷��̾��� ���
			double dx = (goalX - this.x);
			double dy = (goalY - this.y);
			this.x += dx * 1 / 35;
			this.y += dy * 1 / 35;
		}
	}

	// ���콺 ��ǥ�� ���� �޼ҵ�
	public void getMouseX(int mx) {
		goalX = mx;
	}

	public void getMouseY(int my) {
		goalY = my;
	}

	public void respawn(Cell cell) { // ������ ������ ��ġ�� ������ ��Ų��.
		cell.x = (int) Math.floor(Math.random() * 10001); // 10000 ������ ���� ��
		cell.y = (int) Math.floor(Math.random() * 10001); // 10000 ������ ���� ��
		cell.mass = 20; // ������ ũ��� 20���� �ʱ�ȭ��Ų��.
	}

	public boolean checkCollide(double x, double y, double mass) { // ������ �ε����� ��
		return x < this.x + this.mass && // x�� �� ������ x�� + ũ�⺸�� �۰�,
				y < this.y + this.mass && // y�� �� ������ y�� + ũ�� ���� �۰�,
				x + mass > this.x && // x + mass �� �� ������ x ���� ũ��,
				y + mass > this.y; // y + mass �� �� ������ y���� ũ�� true, �ƴϸ� false�� �����Ѵ�.
	}

	public void shootMass() { // �÷��̾� ������ �Ѹ��� ����
		double startX = this.x + this.mass / 2; // ���� �ڱ���ġ�� �� �߽�
		double startY = this.y + this.mass / 2;
		Particle a = new Particle((int) startX, (int) startY, 1, true); // �� ������ �� �߽����� ���̸� ����

		Particle.particles.add(a);
		double sum = Math
				.sqrt((this.goalX - startX) * (this.goalX - startX) + (this.goalY - startY) * (this.goalY - startY)); // ��
																														// ������
																														// ���콺
																														// ��ǥ������
																														// �Ÿ�
		System.out.println(sum);
		if (sum > 500) { // �Ÿ��� �ּ��� ���̸� �Ѹ��� �ӵ��� ������
			a.speed = 30;
		} else if (sum > 400) {
			a.speed = 23;
		} else if (sum > 200) {
			a.speed = 16;
		} else {
			a.speed = 13;
		}
		// Math.atan2(y, x) �� �� �� ������ ���밢���� ���. ��ź��Ʈ��� �Ҹ���.
		// ��ũź��Ʈ(arctangent)�� ��ź��Ʈ��� �ϸ� ź��Ʈ�� ���Լ�.
		// atan()�� -180 ~ 180�� ���� ������ ��ȯ
		// atan2()�� -360~360�� ���� ������ ��ȯ
		// atan2()�� �Ķ���ͷ� ���� ����� �Ǹ�, ��ī��Ʈ ��ǥ�迡�� ����Ҷ� ����
		a.angle = Math.atan2(goalY - startY, goalX - startX);
		a.isShot = true;
	}

	public void Draw(Graphics bbg) { // ���������� ���� �׸���
		bbg.setColor(cellColor);
		bbg.drawRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.fillRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.setColor(Color.WHITE);
		bbg.drawString(name, ((int) x + (int) mass / 2 - name.length() * 3),
				((int) y + (int) mass / 2 + name.length()));
	}
}