package �¶���;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard { // ������ �����

	private int x;
	private int y;
	private int z = 10; // ������ ����
	private int currentY = 0;

	Color color = new Color(50, 50, 50, 128); // �������̶� ȭ�鰡���ϱ� ������ alpha�� �༭ ������ ����. ���� Ŭ���� ����������
	int spots[] = new int[z]; // ������ z������ �迭����

	public Leaderboard() { // �⺻������
		for (int i = 0; i < z; i++) {
			spots[i] = currentY; // 0 30 60 90 120 150 180 210 240 270 
			currentY += 30;
		}
	}

	public void Update() { // cellsCopy ���� �޼ҵ�
		// �����迭�� �����ϴµ� �ؿ� ����Ŭ������ Comparator�� ������ leaderComparator�� �������� ū�ͺ��� �����Ѵ�.
		Collections.sort(Cell.cells, new leaderComparator());
	}

	public void Draw(Graphics bbg) {
		for (int i = 0; i < z; i++) { // 10������ �ű�� ���ؼ� 10�� ������.
			bbg.setColor(color);
			bbg.drawRect(x, y + spots[i], 125, 30);
			bbg.fillRect(x, y + spots[i], 125, 30);
			bbg.setColor(Color.WHITE);
			if (Cell.cells.size() >= z) { // cells ���̰� 10 �̻��� ��
				bbg.drawString("#" + (i + 1) + ": " + Cell.cells.get(i).name + " : " + (int) Cell.cells.get(i).mass, x, y + spots[i] + 25); //������������ �����߱⿡ ó�������� ������.
			}
		}
	}

	private class leaderComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell c1, Cell c2) { // Collections.sort ���Ŀ� �� ������ ����, ������������ ��
			if (c1.mass > c2.mass) {
				return -1;
			} else if (c1.mass < c2.mass) {
				return 1;
			}
			return 0;
		}
	}
}