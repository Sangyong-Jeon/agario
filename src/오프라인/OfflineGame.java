package ��������;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ��������.����������.Create;


public class OfflineGame extends JFrame implements MouseMotionListener { // JFrame�̶�� �Ҹ��� Ŭ���� ��ӹް�, MouseMotionListener ������.
	// ������ �ϸ� �θ��� �޼ҵ带 �ݵ�� �������̵�(������)�ؾ���.
	// implements�� interface ��ӿ� ����.
	// MouseMotionListener�� ���콺�� �������� �������̽��� ���� ó����.

	public static boolean playerCreated = false; // Ŭ���� ����(static����, ��������)
	// �ν��Ͻ� ���� �տ� static�� ���� ��, Ŭ���� ������ ��� �ν��Ͻ��� ����� �������(����)�� ����
	// Ŭ���� ������ ����Ǹ� �ν��Ͻ��� ������ �� ��������� ���� �ν��Ͻ� �����̸� ������ ��������� ����.
	public static boolean isRunning = true;
	int fps = 60;
	public static int keyCode;
	public static int width = 1920;
	public static int height = 1000;

	BufferedImage backBuffer; // �޸� ��ȭ�� ����, BufferedImage�� ���� ������ �׸��� �׸�.
	// �̰� ������ �ʺ�� ���� �����ϰ� ���� ���� �����ؾ���. new BufferedImage(�ʺ�,����,�����);
	// ���������� Graphics�� ���� ���⿡ ���� �׸��� �۾��� �ϸ� �ȴ�.
	// ��� ����� Graphics g = backBuffer.getGraphics(); �̰� g�� �׸��� �۾� �� g.dispose(); �ϸ�
	// ��.
	// dispose() �޼���� �׷��� ���ƿ��� ������� �ý��� �ڿ��� �����Ѵ�.
	Insets insets; // dimension Ŭ������ �޸� �� > �� > �� > ���� ������ ������ �� �ִ� Ŭ�����̴�.

	public static Leaderboard lb = new Leaderboard(); // ���� ������ ����
	public static Camera cam = new Camera(0, 0, 1, 1); // �� �þ� ����
	public JPanel pane = null;

	public OfflineGame() {
		initialize();

		Thread cc = new Thread(new Create(), "���̻���");
		cc.run();
		
		while (isRunning) {
			long time = System.currentTimeMillis();
			update();
			draw();
			time = (1000 / fps) - (System.currentTimeMillis() - time);

			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (Exception e) {
					e.getStackTrace();
				}
			}
		}
		setVisible(false);
	}

	public void initialize() {
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // ȭ�� �ػ� ��������
		int monitorWidth = size.width;
		int monitorHeight = size.height;
//		this.setLocationRelativeTo(null); // ȭ�� ���߾� ��ġ
		this.setTitle("����Ű���"); // Ÿ��Ʋ �̸�
		this.setResizable(false); // ����ڰ� ������ ũ�� ���� ���ϰ���.
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.insets = getInsets();
		this.setSize(this.insets.left + width + this.insets.right, this.insets.top + height + this.insets.bottom);
		this.setLocation(monitorWidth / 2 - this.getSize().width / 2, monitorHeight / 2 - this.getSize().height / 2);
		this.setVisible(true);
		
		pane = new JPanel();
		this.requestFocus(); // ���콺 ��Ŀ�� �ֱ�
		this.addMouseMotionListener(this); // ������ ��ü�� ���콺 �̺�Ʈ�� �����ϵ��� ó��
		this.addKeyListener(new KeyInput(this));
		pane.setSize(width, height);
		pane.setVisible(true);
		this.add(pane);

		// BufferedImage�� �����ϴµ� �� ��ȭ���� (�ʺ�� ����, ���� ��) ������.
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public void run() {
		initialize();

		while (isRunning) {
			long time = System.currentTimeMillis(); // ���� �ð���
//			update();
			draw();
			this.setVisible(true);
			time = (1000 / fps) - (System.currentTimeMillis() - time);
			// 16.6666 - update()�� draw() ������ �� �ð��� - ù������
			if (time > 0) {
				try {
					Thread.sleep(time);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		setVisible(false);
		System.exit(0);
	}

	public void update() {
		lb.Update(); // ���� ū ������ ����

		for (int i = 0; i < Cell.cells.size(); i++) { // �÷��̾� ���� ã��
			if (Cell.cells.get(i).name.equals("Bruce")) {
				cam.Update(Cell.cells.get(i)); // �÷��̾� ������ ���缭 �þ� �ø���
			}
		}

		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			if (keyCode != 87) {
				Particle p = it.next();
				if (!p.getHealth()) {
					p.Update();
				} else {
					it.remove(); // �÷��̾ �Ѹ� ���̴� ����
				}
			} else {
				break;
			}
		}

		for (Cell cell : Cell.cells) {
			cell.Update();
		}
	}

	public void draw() {
		Graphics g = pane.getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		Graphics bbg2 = backBuffer.getGraphics();

		bbg.setColor(Color.black); // ���� ������
		bbg.fillRect(0, 0, width, height); // ���ȭ�� �簢�� �׸���

		cam.Graphics(bbg);
		cam.set(); // �� ���� Ȯ�� �� ȭ�� �̵�

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		for (Particle p : pCopy) { // ������ ���̵� ���ΰ�ħ�Ͽ� bbg ȭ�鿡 ��Ÿ����
			p.Draw(bbg);
		}

		for (Cell cell : Cell.cells) {
			cell.Draw(bbg);
		}

		cam.unset();

		for (Cell cell : Cell.cells) { // �� ��ǥ�� ���� ��ܿ� �����ֱ�
			if (cell.name.equals("Bruce")) {
				String pos = ("X: " + (int) cell.x + " Y: " + (int) cell.y);
				bbg2.drawString(pos, (OfflineGame.width - pos.length() * pos.length() - 250), 10);
			}
		}
		lb.Draw(bbg2);
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	
	public void mouseMoved(MouseEvent e) { // ���콺�� Ŭ������ �ʰ� �̵��ϴ� ��� ȣ��
		
		for (Cell cell : Cell.cells) {
			if (cell.name.equals("Bruce")) {
				cell.getMouseX((int) (e.getX() / cam.sX + cam.x));
				cell.getMouseY((int) (e.getY() / cam.sY + cam.y));
			}
		}
	}

	public void mouseDragged(MouseEvent e) { // ���콺 �巡���ϸ� ȣ��
		this.requestFocus();
	}

	public void keyPressed(KeyEvent e) {
		keyCode = e.getKeyCode();
		if (keyCode == 87) { // 87�� wŰ �̴�.
			for (Cell cell : Cell.cells) {
				if (cell.name.equals("Bruce")) {
					cell.shootMass();
				}
			}
		}
		keyCode = 0;
	}

	public void keyReleased(KeyEvent e) {

	}

	public static String print(String x) {
		System.out.println(x);
		return "";
	}
}