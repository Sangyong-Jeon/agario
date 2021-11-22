package 서버테스트;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Particle {
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public static int particleCount; // 먹이 갯수 세기
	public int x, y, mass;
	public String pname;
	
	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));
	
	public Particle(int x, int y, int mass) {
		particleCount++;
		this.pname = Integer.toString(particleCount);
		this.x = x;
		this.y = y;
		this.mass = mass;
	}
	
	public Particle(String pname, int x, int y, int mass) {
		particleCount++;
		this.pname = pname;
		this.x = x;
		this.y = y;
		this.mass = mass;
	}
	
	public void Draw(Graphics bbg) {
		bbg.setColor(color);
		bbg.fillOval(this.x, this.y, 10, 10);
		bbg.drawOval(this.x, this.y, 10, 10);
	}

}
