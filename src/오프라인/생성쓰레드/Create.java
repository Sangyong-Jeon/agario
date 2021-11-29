package ��������.����������;

import ��������.Cell;
import ��������.OfflineGame;
import ��������.Particle;

public class Create implements Runnable {

	@Override
	public void run() {
		while (Cell.cellCount < 150) { // AI ���� ����
			Cell.cells.add(new Cell(("Cell " + Cell.cellCount), (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 2801), false));
			System.out.println("AI���� ������" + Cell.cellCount);
		}

		while (Particle.particleCount < 5000) { // ���̻���
			Particle.particles.add(new Particle((int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 10001), 1, false));
			System.out.println("���̻�����" + Particle.particleCount);
		}

		if (!OfflineGame.playerCreated) { // �÷��̾� ��������
			OfflineGame.playerCreated = true;
			Cell.cells.add(new Cell("Bruce", (int) Math.floor(Math.random() * 10001),
					(int) Math.floor(Math.random() * 2801), true));

		}
	}

}
