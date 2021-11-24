package 오프라인;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Cell {
	// 클래스 변수 선언
	public static ArrayList<Cell> cells = new ArrayList<Cell>();
	public static int cellCount;
	public static boolean stop = false;
	public String name;
	double x, y;
	double mass = 10; // 세포의 기본 크기는 10
	int speed = 1; // 세포의 기본 스피드는 1
	boolean isPlayer = false;

	Color cellColor; // 세포의 색 지정
	Cell target; // Cell 타입 참조변수 생성
	Particle pTarget; // Particle 타입 참조변수 생성

	boolean isTarget = false;
	String targetType = "p"; // 기본적으로 먹이를 타겟으로 생성

	double goalX, goalY; // 마우스 좌표
	boolean goalReached = true;

	public Cell(String name, int x, int y, boolean isPlayer) { // 세포 생성자(이름, x좌표, y좌표, 플레이어구분)
		this.name = name;
		this.x = x;
		this.y = y;
		this.isPlayer = isPlayer;
		this.randomColor(); // 이 세포의 Color색을 랜덤으로 지정해주는 메소드
		cellCount++; // 세포 count 증가
	}

	public void randomColor() {
		// r,g,b에 0~255의 값을 랜덤으로 넣고 Color로 생성한다.
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		this.cellColor = new Color(r, g, b); // 랜덤으로 생성한 Color를 세포의 인스턴스 변수에 넣는다.
	}

	public void addMass(double mass) { // 매개변수로 받는 크기를 세포의 크기에 더해준다.
		this.mass += mass;
	}

	// 이 세포보다 작고 가까운 세포의 인덱스값을 반환하고, 이 세포보다 작은 세포가 없고 cellCount가 cells 배열의 길이와 같을 경우
	// -1을 반환하는 메소드
	public int returnNearestCell() {
		int x = 0;
		int distance = 15000;
		int min = distance;
		for (Cell cell : cells) { // cells 배열에 있는 값들을 하나씩 가져와서 돌림.
			if (this != cell) { // 배열에서 꺼낸 세포가 이 세포가 아닐 경우
				// Math.sqrt() 메소드는 매개변수인 숫자의 제곱근을 반환한다. 즉, 주어진 숫자에 루트를 씌운다.
				// 이 세포와 cells에서 꺼낸 세포의 거리를 구한다. (x^2 + y^2)의 제곱근으로 대각선의 길이, 즉 피타고라스의 정리로 구한다.
				distance = (int) Math
						.sqrt((this.x - cell.x) * (this.x - cell.x) + (cell.y - this.y) * (cell.y - this.y));
				if (distance < min && this.mass > cell.mass + 10) { // 이 세포와 cells배열에서 꺼낸 세포의 거리가 9999999보다 작고, 이 세포의
																	// 크기가 꺼낸 세포의 크기+10보다 클경우
					min = distance;
					x = cells.indexOf(cell); // cells 배열에 지금 꺼낸 세포의 인덱스값을 꺼내서 x에 넣는다.

					// ArrayList.size() 메소드는 무엇인가?
					// 배열의 크기는 length 속성으로 얻을수 있지만 ArrayList는 고정된 길이를 가지고 있지 않아서
					// size()함수를 사용하여 리스트에 들어있는 원소의 수를 얻는다.
					// 배열에서 꺼낸 세포크기+10이 이 세포보다 크고, 배열에서 꺼낸 세포의 cellCount 수가 cells배열의 원소 수와 같을 때
				} else if (distance < min && this.mass < cell.mass + 10 && cellCount == cells.size()) {
					x = -1;
				}
			}
		}
		return x; // cells 배열안의 세포 인덱스값 or -1을 반환함.
	}

	public int returnNearestP() { // 세포와 겹쳐진 먹이의 인덱스값 반환
		int x = 0;
		int distance = 99999999;
		int min = distance;
		
		for(int i = 0; i < Particle.particles.size(); i++) {
			Particle p = Particle.particles.get(i);
			distance = (int) Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
			if (distance < min && this.mass > p.mass) { // 세포와 먹이의 거리가 99,999,999보다 작고 세포의 크기가 먹이의 크기보다 클 때
				min = distance;
				x = Particle.particles.indexOf(p); // x에 particles배열에서 지금 꺼낸 먹이 p의 인덱스 값을 넣는다.
			}
		}

//		for (Particle p : Particle.particles) { // 먹이의 ArrayList 배열인 particles에서 하나씩 꺼내서 p에 대입하여 for-each문을 돌린다.
//			// (이 세포의 x좌표 - 먹이의 x좌표)^2 + (이 세포의 y좌표 - 먹이의 y좌표)^2 의 값의 제곱근을 distance에 넣는다.
//			// 즉, 세포와 먹이의 직선거리를 구함
//			distance = (int) Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
//			if (distance < min && this.mass > p.mass) { // 세포와 먹이의 거리가 99,999,999보다 작고 세포의 크기가 먹이의 크기보다 클 때
//				min = distance;
//				x = Particle.particles.indexOf(p); // x에 particles배열에서 지금 꺼낸 먹이 p의 인덱스 값을 넣는다.
//			}
//
//		}
		return x; // 반환되는 x 값은 이 세포보다 작고 거리가 가까운 먹이의 particles의 인덱스 값을 반환한다.
	}

	public void Update() {
		if (this.mass > 3500) { // 세포의 크기가 3500보다 크다면
			this.mass = 3500; // 세포의 크기 3500 고정
		}

		for (Cell cell : cells) {
			// 꺼낸 세포와 부딪혔고,꺼낸세포와 이 세포가 같지 않고, 이세포의 크기가 꺼낸세포의 크기+10보다 클때
			if (this.checkCollide(cell.x, cell.y, cell.mass) && this != cell && this.mass > cell.mass + 10) {
				if (1 / (this.mass / cell.mass) >= .4 && this.mass < 4000) { // 자신의 세포크기가 꺼낸 세포의 크기보다 2.5배 이하지만 크고, 이 세포크기가
																				// 4000 미만일 때
					addMass(cell.mass); // 꺼낸 세포의 크기를 자신의 세포 크기에 더한다.
				}
				respawn(cell); // 꺼낸 세포를 무작위 위치에 리스폰 시킨다.
			}
		}

		if (!isPlayer) { // 이 세포가 플레이어가 아닐 때
			if (goalReached) { // true일 때 돌리고 이거 끝나면 마지막에 false로 바꿔줌.
				if (returnNearestCell() > -1) { // 이 세포보다 작고 가까운 세포가 있을 경우
					if (!isTarget) { // 이 AI세포가 타겟이 없을 경우
						target = cells.get(returnNearestCell()); // 나보다 작은 세포를 타겟으로 설정
						isTarget = true; // 설정한 타겟이 있음으로 바꿈
						targetType = "c"; // 세포를 타겟으로 설정했다는 뜻
					} else if (isTarget && targetType.equals("c")) { // 이 AI세포가 세포타겟이 있을 경우
						targetType = "n";
						isTarget = false;
					}
				} else if (returnNearestCell() == -1) { // 이 세포보다 크고, 가깝고, cellCount가 cells 배열길이랑 같을 경우
					if (!isTarget) { // 이 AI세포가 타겟이 없을 경우
						pTarget = Particle.particles.get(returnNearestP()); // 가까운 거리의 먹이를 pTarget으로 설정
						isTarget = true; // 설정한 타겟이 있음으로 바꿈
						targetType = "p"; // 먹이를 타겟으로 설정했다는 뜻
					} else if (isTarget) { // 이 AI세포가 먹이 타겟이 있을 경우
						targetType = "n";
						isTarget = false;
					}
				}
				goalReached = false; // goalReached를 false로 바꿈
			} else { // goalReached가 false일 경우, 이 세포보다 작
				double dx = 0;
				double dy = 0;
				if (targetType.equals("c")) { // 세포타겟일 경우
					if (returnNearestCell() > -1) { // 이 세포보다 작고 가까운 세포가 있을 때
						target = cells.get(returnNearestCell()); // 타겟을 그 세포로 바꾼다.
						dx = (target.x - this.x);
						dy = (target.y - this.y);
					} else { // 이 세포보다 작고 가까운 세포가 없으면
						goalReached = true;
					}
				} else if (targetType.equals("p")) { // 먹이타겟일 경우
					pTarget = Particle.particles.get(returnNearestP()); // 이 세포보다 작고 가까운 먹이를 타겟으로 설정
					dx = (pTarget.x - this.x);
					dy = (pTarget.y - this.y);
				}
				double distance = Math.sqrt((dx) * (dx) + (dy) * (dy));
				if (distance > 1) { // 타겟과 자신의 거리가 1 초과일 시 이동속도 설정
					x += (dx) / distance * 5;
					y += (dy) / distance * 5;
				} else { // 1 이하일 때
					goalReached = true;
				}
			}
		} else { // 세포가 플레이어일 경우
			double dx = (goalX - this.x);
			double dy = (goalY - this.y);
			this.x += dx * 1 / 35;
			this.y += dy * 1 / 35;
		}
	}

	// 마우스 좌표값 설정 메소드
	public void getMouseX(int mx) {
		goalX = mx;
	}

	public void getMouseY(int my) {
		goalY = my;
	}

	public void respawn(Cell cell) { // 세포를 무작위 위치에 리스폰 시킨다.
		cell.x = (int) Math.floor(Math.random() * 10001); // 10000 이하의 랜덤 값
		cell.y = (int) Math.floor(Math.random() * 10001); // 10000 이하의 랜덤 값
		cell.mass = 20; // 세포의 크기는 20으로 초기화시킨다.
	}

	public boolean checkCollide(double x, double y, double mass) { // 세포와 부딪혔을 때
		return x < this.x + this.mass && // x가 이 세포의 x값 + 크기보다 작고,
				y < this.y + this.mass && // y가 이 세포의 y값 + 크기 보다 작고,
				x + mass > this.x && // x + mass 가 이 세포의 x 보다 크고,
				y + mass > this.y; // y + mass 가 이 세포의 y보다 크면 true, 아니면 false를 리턴한다.
	}

	public void shootMass() { // 플레이어 세포가 뿌리는 먹이
		double startX = this.x + this.mass / 2; // 현재 자기위치인 원 중심
		double startY = this.y + this.mass / 2;
		Particle a = new Particle((int) startX, (int) startY, 1, true); // 이 세포의 원 중심으로 먹이를 생성

		Particle.particles.add(a);
		double sum = Math
				.sqrt((this.goalX - startX) * (this.goalX - startX) + (this.goalY - startY) * (this.goalY - startY)); // 이
																														// 세포와
																														// 마우스
																														// 좌표값까지
																														// 거리
		System.out.println(sum);
		if (sum > 500) { // 거리가 멀수록 먹이를 뿌리는 속도는 빨라짐
			a.speed = 30;
		} else if (sum > 400) {
			a.speed = 23;
		} else if (sum > 200) {
			a.speed = 16;
		} else {
			a.speed = 13;
		}
		// Math.atan2(y, x) 는 두 점 사이의 절대각도를 잰다. 역탄젠트라고 불린다.
		// 아크탄젠트(arctangent)는 역탄젠트라고도 하며 탄젠트의 역함수.
		// atan()는 -180 ~ 180의 라디안 값으로 반환
		// atan2()는 -360~360의 라디안 값으로 반환
		// atan2()는 파라미터로 음수 허용이 되며, 데카르트 좌표계에서 사용할때 유용
		a.angle = Math.atan2(goalY - startY, goalX - startX);
		a.isShot = true;
	}

	public void Draw(Graphics bbg) { // 최종적으로 세포 그리기
		bbg.setColor(cellColor);
		bbg.drawRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.fillRect((int) x, (int) y, (int) mass, (int) mass);
		bbg.setColor(Color.WHITE);
		bbg.drawString(name, ((int) x + (int) mass / 2 - name.length() * 3),
				((int) y + (int) mass / 2 + name.length()));
	}
}