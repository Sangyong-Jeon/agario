package 오프라인;

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

import 오프라인.생성쓰레드.Create;


public class OfflineGame extends JFrame implements MouseMotionListener { // JFrame이라고 불리는 클래스 상속받고, MouseMotionListener 구현함.
	// 구현을 하면 부모의 메소드를 반드시 오버라이딩(재정의)해야함.
	// implements는 interface 상속에 사용됨.
	// MouseMotionListener는 마우스의 움직임을 인터페이스를 통해 처리함.

	public static boolean playerCreated = false; // 클래스 변수(static변수, 공유변수)
	// 인스턴스 변수 앞에 static을 붙인 것, 클래스 변수는 모든 인스턴스가 공통된 저장공간(변수)을 공유
	// 클래스 영역에 선언되며 인스턴스를 생성할 때 만들어지는 것이 인스턴스 변수이며 별도의 저장공간을 가짐.
	public static boolean isRunning = true;
	int fps = 60;
	public static int keyCode;
	public static int width = 1920;
	public static int height = 1000;

	BufferedImage backBuffer; // 메모리 도화지 느낌, BufferedImage를 상대로 도형과 그림을 그림.
	// 이걸 쓰려면 너비와 높이 지정하고 색상 모델을 지정해야함. new BufferedImage(너비,높이,색상모델);
	// 생성했으면 Graphics를 얻어내서 여기에 직접 그리기 작업을 하면 된다.
	// 얻는 방법은 Graphics g = backBuffer.getGraphics(); 이고 g로 그리기 작업 후 g.dispose(); 하면
	// 됨.
	// dispose() 메서드는 그래픽 문맥에서 사용중인 시스템 자원을 해제한다.
	Insets insets; // dimension 클래스와 달리 상 > 좌 > 하 > 우의 여백을 관리할 수 있는 클래스이다.

	public static Leaderboard lb = new Leaderboard(); // 공용 점수판 생성
	public static Camera cam = new Camera(0, 0, 1, 1); // 내 시야 생성
	public JPanel pane = null;

	public OfflineGame() {
		initialize();

		Thread cc = new Thread(new Create(), "먹이생성");
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
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); // 화면 해상도 가져오기
		int monitorWidth = size.width;
		int monitorHeight = size.height;
//		this.setLocationRelativeTo(null); // 화면 정중앙 배치
		this.setTitle("세포키우기"); // 타이틀 이름
		this.setResizable(false); // 사용자가 프레임 크기 조절 못하게함.
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setFocusable(true);
		this.insets = getInsets();
		this.setSize(this.insets.left + width + this.insets.right, this.insets.top + height + this.insets.bottom);
		this.setLocation(monitorWidth / 2 - this.getSize().width / 2, monitorHeight / 2 - this.getSize().height / 2);
		this.setVisible(true);
		
		pane = new JPanel();
		this.requestFocus(); // 마우스 포커스 주기
		this.addMouseMotionListener(this); // 프레임 자체가 마우스 이벤트를 감지하도록 처리
		this.addKeyListener(new KeyInput(this));
		pane.setSize(width, height);
		pane.setVisible(true);
		this.add(pane);

		// BufferedImage를 생성하는데 이 도화지의 (너비와 높이, 색상 모델) 지정함.
		this.backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public void run() {
		initialize();

		while (isRunning) {
			long time = System.currentTimeMillis(); // 현재 시간초
//			update();
			draw();
			this.setVisible(true);
			time = (1000 / fps) - (System.currentTimeMillis() - time);
			// 16.6666 - update()와 draw() 돌리고 난 시간초 - 첫시작초
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
		lb.Update(); // 세포 큰 순으로 정렬

		for (int i = 0; i < Cell.cells.size(); i++) { // 플레이어 세포 찾기
			if (Cell.cells.get(i).name.equals("Bruce")) {
				cam.Update(Cell.cells.get(i)); // 플레이어 세포에 맞춰서 시야 늘리기
			}
		}

		for (Iterator<Particle> it = Particle.particles.iterator(); it.hasNext();) {
			if (keyCode != 87) {
				Particle p = it.next();
				if (!p.getHealth()) {
					p.Update();
				} else {
					it.remove(); // 플레이어가 뿌린 먹이는 삭제
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

		bbg.setColor(Color.black); // 배경색 검정색
		bbg.fillRect(0, 0, width, height); // 배경화면 사각형 그리기

		cam.Graphics(bbg);
		cam.set(); // 내 세포 확대 및 화면 이동

		ArrayList<Particle> pCopy = new ArrayList<Particle>(Particle.particles);
		for (Particle p : pCopy) { // 생성된 먹이들 새로고침하여 bbg 화면에 나타내기
			p.Draw(bbg);
		}

		for (Cell cell : Cell.cells) {
			cell.Draw(bbg);
		}

		cam.unset();

		for (Cell cell : Cell.cells) { // 내 좌표값 우측 상단에 보여주기
			if (cell.name.equals("Bruce")) {
				String pos = ("X: " + (int) cell.x + " Y: " + (int) cell.y);
				bbg2.drawString(pos, (OfflineGame.width - pos.length() * pos.length() - 250), 10);
			}
		}
		lb.Draw(bbg2);
		g.drawImage(backBuffer, insets.left, insets.top, this);
	}
	
	public void mouseMoved(MouseEvent e) { // 마우스가 클릭되지 않고 이동하는 경우 호출
		
		for (Cell cell : Cell.cells) {
			if (cell.name.equals("Bruce")) {
				cell.getMouseX((int) (e.getX() / cam.sX + cam.x));
				cell.getMouseY((int) (e.getY() / cam.sY + cam.y));
			}
		}
	}

	public void mouseDragged(MouseEvent e) { // 마우스 드래그하면 호출
		this.requestFocus();
	}

	public void keyPressed(KeyEvent e) {
		keyCode = e.getKeyCode();
		if (keyCode == 87) { // 87은 w키 이다.
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