package 서버테스트.게임서버;

public class GameServerMain {
	public static void main(String[] args) {
		// 게임서버 소켓을 연다.
		Thread server = new Thread(new GameServer(), "게임서버소켓");
		server.start();
		// 게임서버를 실행한다.
		Thread world = new Thread(new GameWorld(), "게임서버");
		world.start();
		
		
	}
}
