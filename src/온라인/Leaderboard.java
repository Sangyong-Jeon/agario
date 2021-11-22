package 온라인;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard { // 점수판 만들기

	private int x;
	private int y;
	private int z = 10; // 점수판 갯수
	private int currentY = 0;

	Color color = new Color(50, 50, 50, 128); // 점수판이라 화면가리니까 마지막 alpha값 줘서 불투명도 만듬. 값이 클수록 불투명해짐
	int spots[] = new int[z]; // 점수판 z개까지 배열만듬

	public Leaderboard() { // 기본생성자
		for (int i = 0; i < z; i++) {
			spots[i] = currentY; // 0 30 60 90 120 150 180 210 240 270 
			currentY += 30;
		}
	}

	public void Update() { // cellsCopy 정렬 메소드
		// 세포배열을 정렬하는데 밑에 내부클래스로 Comparator를 구현한 leaderComparator를 기준으로 큰것부터 정렬한다.
		Collections.sort(Cell.cells, new leaderComparator());
	}

	public void Draw(Graphics bbg) {
		for (int i = 0; i < z; i++) { // 10순위를 매기기 위해서 10번 돌린다.
			bbg.setColor(color);
			bbg.drawRect(x, y + spots[i], 125, 30);
			bbg.fillRect(x, y + spots[i], 125, 30);
			bbg.setColor(Color.WHITE);
			if (Cell.cells.size() >= z) { // cells 길이고 10 이상일 때
				bbg.drawString("#" + (i + 1) + ": " + Cell.cells.get(i).name + " : " + (int) Cell.cells.get(i).mass, x, y + spots[i] + 25); //내림차순으로 정렬했기에 처음꺼부터 꺼낸다.
			}
		}
	}

	private class leaderComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell c1, Cell c2) { // Collections.sort 정렬에 쓸 기준점 설정, 내림차순으로 함
			if (c1.mass > c2.mass) {
				return -1;
			} else if (c1.mass < c2.mass) {
				return 1;
			}
			return 0;
		}
	}
}