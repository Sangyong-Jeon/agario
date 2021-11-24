package 서버테스트.게임서버;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Particle {
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public static int particleCount; // 먹이 갯수 세기
	public int x, y, mass;
	public String pname;
	public boolean isTrue = false;

	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));

	public Particle(String pname, int x, int y, int mass) {
		particleCount++;
		this.pname = pname;
		this.x = x;
		this.y = y;
		this.mass = mass;
	}

	public void Update() {
		boolean isSend = false;
		for (Cell c : Cell.cells) {
			if (checkCollide(c.x, c.y, c.mass)) {
				c.addMass(this.mass);
				GameWorld.cmassUpdateDB(c.name, c.mass);
				GameServer.broadCasting("u"+c.name);
				// 먹혔으니 랜덤위치에 리스폰
				this.x = (int) Math.floor(Math.random() * 1000);
				this.y = (int) Math.floor(Math.random() * 1000);
				// DB에 먹이 좌표 수정
				GameWorld.pUpdateDB(pname, x, y, mass);
				// 특정 먹이를 수정했다고 모든 클라이언트에게 날림
				GameServer.broadCasting("s"+pname);
				isSend = true;
			}
		}
		// 먹이 좌표 수정되었으니 모든 클라이언트에게 먹이좌표 갱신하라고 보냄
		if(isSend ) {
			GameServer.broadCasting("p");
		}
	}

	private boolean checkCollide(double x, double y, double mass) { // boolean 값을 반환한다.
		// 여기서 왜 10을 더해주는가? 그것은 먹이의 가로와 세로 길이가 10이기 때문이다.
		return x < this.x + 10 && // 세포의 x좌표가 먹이의 x좌표+10 보다 작고
				x + mass > this.x && // 세포의 x좌표 + 크기가 먹이의 x좌표보다 클때
				y < this.y + 10 && // 세포의 y좌표가 먹이의 y좌표+10 보다 작고
				y + mass > this.y; // 세포의 y좌표 + 크기가 먹이의 y좌표보다 클때
	} // 즉 먹이의 크기안에 세포가 들어왔을때 true를 반환한다.

	public void Draw(Graphics bbg) {
		bbg.setColor(color);
		bbg.fillOval(this.x, this.y, 10, 10);
		bbg.drawOval(this.x, this.y, 10, 10);
	}

}
