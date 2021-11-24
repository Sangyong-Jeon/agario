package �¶���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;

// ������ �����
public class Leaderboard {
	private int x;
	private int y;
	// ������ ����
	private int z = 3;
	private int currentY = 0;

	// �������̶� ȭ�鰡���ϱ� ������ alpha�� �༭ ������ ����. ���� Ŭ���� ����������
	Color color = new Color(50, 50, 50, 128);
	// ������ z������ �迭����
	int spots[] = new int[z];

	public Leaderboard() {
		for (int i = 0; i < z; i++) {
			spots[i] = currentY;
			// 0 30 60 90 120 150 180 210 240 270
			currentY += 30;
		}
	}

	// cells ���� �޼ҵ�
	public void Update() {
		// �����迭�� �����ϴµ� �ؿ� ����Ŭ������ Comparator�� ������ leaderComparator�� �������� ū�ͺ��� �����Ѵ�.
		Collections.sort(Cell.cells, new leaderComparator());
	}
	
	public void Draw(Graphics bbg) {
		System.out.println("�������� �׸���");
		// 10������ �ű�� ���ؼ� 10�� ������.
		for (int i = 0; i < z; i++) { 
			bbg.setColor(color);
			bbg.drawRect(x, y + spots[i], 125, 30);
			bbg.fillRect(x, y + spots[i], 125, 30);
			bbg.setColor(Color.WHITE);
			System.out.println(Cell.cells.size());
			// cells ���̰� 10 �̻��� ��
			if (Cell.cells.size() >= z) { 
				//������������ �����߱⿡ ó�������� ������.
				bbg.drawString("#" + (i + 1) + ": " + Cell.cells.get(i).name + " : " + (int) Cell.cells.get(i).mass, x, y + spots[i] + 25); 
			}
		}
	}

	private class leaderComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell c1, Cell c2) {
			// Collections.sort ���Ŀ� �� ������ ����
			// ������������ �����ϰ���.
			if (c1.mass > c2.mass) {
				return -1;
			} else if (c1.mass < c2.mass) {
				return 1;
			}
			return 0;
		}

	}

}
