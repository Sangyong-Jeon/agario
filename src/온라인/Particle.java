package 온라인;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

// 맵에 무작위로 생성되는 먹이
public class Particle {
	// 클래스 변수 (static 변수, 공유변수)
	public static ArrayList<Particle> particles = new ArrayList<Particle>(); // Particle 타입의 ArrayList 생성
	public static int particleCount; // 먹이 생성 몇개까지할지 정할때 쓰는 값
	// 인스턴스 변수
	public int x, y, mass; // 기본적으로 생성할때 쓰는 값
	public double speed, angle, dx, dy; // 사용자가 뿌리는 먹이가 쓰는 값들
	

	private boolean cellParticle = false; // 기본생성 먹이인지 사용자가 뿌리는 먹이인지 확인용
	private boolean die = false;
	public boolean isShot;

	private Color color = new Color((int) Math.floor(Math.random() * 256), (int) Math.floor(Math.random() * 256),
			(int) Math.floor(Math.random() * 256));
	// Color는 색을 표현하기 위해 사용되는 클래스
	// 원하는 색의 RGB 값만 알고 있으면 생성하여 사용가능
	// Color(int r, int g, int b) 는 r-red, g-green, b-blue 모두 0~255사이의 정수값을 가짐.
	// Color(float r, float g, float b) 는 모두 0.0 ~ 1.0 사이의 실수값을 가짐.
	// Color(int r, int g, int b, int a) 는 a-alpha값으로 0~255 정수값이 있고, float이면
	// 0.0~1.0사이 실수값, alpha는 불투명도를 뜻함. 클수록 불투명해짐.

	// Math 클래스에서 floor는 내림, ceil는 올림, round는 반올림
	// Math 클래스의 정적메소드인 random() 메소드는 난수를 발생시킴
	// random()은 0.0이상 1.0미만의 double 값의 난수를 균일한 분포로 반환한다. 0~ 9사이 정수형 난수를 만들고 싶으면
	// double형 난수에 10을 곱한 후 정수로 캐스팅하면 된다.
	// 즉 위의 (int)Math.floor(Math.random() * 256) 은 0.0 이상 1.0 미만의 값을 256으로 곱해서 내림한 후
	// int형 정수로 캐스팅하므로 0~255값이 나온다.
	public Particle(int x, int y, int mass, boolean p) { // Particle 생성자이다.
		// 현재 Game 클래스에서 particles에 이 Particle 객체가 5000개 미만일때 Particle( (0~10000)사이값,
		// (0~10000)사이값 , 1 , false)로 자동생성중.
		particleCount++; // 클래스 int 변수
		this.x = x; // 인스턴스 int 변수
		this.y = y; // 인스턴스 int 변수
		this.mass = mass; // 인스턴스 int 변수
		cellParticle = p; // 인스턴스 boolean 변수
	}

	public void Update() {
		for (Cell cell : Cell.cells) { // for-each문으로 Cell클래스 안의 클래스 변수인 public static ArrayList<Cell> cells 안에 있는 값들을
										// 하나씩 넣어 돌린다.
			// this.checkCollide(cell.x,cell.y,cell.mass)는 cell의 x, y, mass의 값과 Particle의 값과
			// 비교해서 boolean값을 반환한다.
			if (this.checkCollide(cell.x, cell.y, cell.mass) && !cellParticle) { // 기본생성된 먹이가 세포와 겹쳤을 때
				if (cell.mass <= 200) { // 세포의 크기가 200 이하일때
					cell.addMass(this.mass); // 세포의 크기에 먹이의 크기를 더한다
				} else { // 세포의 크기가 200이상일때
					cell.isTarget = false;
					cell.goalReached = true;
					cell.targetType = "c";
				}
				if (cell.targetType.equals("p")) { // 이 세포가 목표하는 먹이타겟이랑 겹쳤기에 isTarget을 false로 goalReached를 true로 바꿔준거임.
													// 세포타겟이랑 겹쳤을때 타겟을 취소하면 안되니까 이렇게 한 것.
					cell.isTarget = false;
					cell.goalReached = true;
				}
				// 세포에게 먹혔기에 삭제해야하지만 삭제하지않고 위치를 난수로 해서 무작위 위치로 옮겨준다.
				this.x = (int) Math.floor(Math.random() * 10001); // Particle의 x에 (0 ~ 10,000)의 값을 int형으로 강제형변환 후 넣는다.
																	// (난수는 0에서 1미만)
				this.y = (int) Math.floor(Math.random() * 10001); // 위와 같다. y에 int형 0~10,000 값을 넣는다.
			} else if (this.checkCollide(cell.x, cell.y, cell.mass) && cellParticle && !cell.isPlayer) { // 플레이어가 뿌린 먹이는 AI세포만 먹을 수 있다.
				// 플레이어가 뿌린 먹이를 AI 세포가 먹었을 시 제거한다.
				cell.addMass(this.mass);
				this.die = true;
			}
		}
		if (isShot) {
			dx = (speed) * Math.cos(angle);
			dy = (speed) * Math.sin(angle);
			x += dx;
			y += dy;
			speed -= 0.3;
			if (speed <= 0) {
				isShot = false;
				speed = 0;
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

	public void Draw(Graphics bbg) { // x좌표와 y좌표에 동그라미를 그린다.
		bbg.setColor(color);
		bbg.fillOval(x, y, 10, 10);
		bbg.drawOval(x, y, 10, 10);
	}

	public boolean getHealth() {
		return die;
	}
}