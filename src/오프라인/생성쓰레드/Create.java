package 오프라인.생성쓰레드;

import 오프라인.Cell;
import 오프라인.OfflineGame;
import 오프라인.Particle;

public class Create implements Runnable {

	@Override
	public void run() {
		while (Cell.cellCount < 150) { // AI 세포 생성
			Cell.cells.add(new Cell(("Cell " + Cell.cellCount), (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 2801), false));
			System.out.println("AI세포 생성중" + Cell.cellCount);
		}

		while (Particle.particleCount < 5000) { // 먹이생성
			Particle.particles.add(new Particle((int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 10001), 1, false));
			System.out.println("먹이생성중" + Particle.particleCount);
		}

		if (!OfflineGame.playerCreated) { // 플레이어 세포생성
			OfflineGame.playerCreated = true;
			Cell.cells.add(new Cell("Bruce", (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 2801), true));

		}
	}

}
