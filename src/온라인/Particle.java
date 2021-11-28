package 온라인;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import 온라인.게임서버.GameServer;
import 온라인.게임서버.GameWorld;

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

		for (Iterator<Cell> it = Cell.cells.iterator(); it.hasNext();) {
			Cell c = it.next();
			if (c.mass < 3500) {
				if (checkCollide(c.x, c.y, c.mass)) {
					c.addMass(this.mass);
					// 특정 세포 크기 DB 업데이트
					GameWorld.updateDB("update cell set mass = " + c.mass + " where name = '" + c.name + "'");
					// 특정 세포 조회 신호보내기
					GameServer.broadCasting("특정세포조회" + c.name);
					// 특정 먹이 랜덤 위치에 리스폰 후 DB 업데이트
					this.x = (int) Math.floor(Math.random() * 5001);
					this.y = (int) Math.floor(Math.random() * 5001);
					GameWorld.updateDB("update particle set x = " + x + ", y =" + y + " where name = '" + pname + "'");
					// 특정 먹이 조회 신호보내기
					GameServer.broadCasting("특정먹이조회" + pname);
					break;
				}
			} else {
				break;
			}
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
