package �����׽�Ʈ.���Ӽ���;

public class GameServerMain {
	public static void main(String[] args) {
		// ���Ӽ��� ������ ����.
		Thread server = new Thread(new GameServer(), "���Ӽ�������");
		server.start();
		// ���Ӽ����� �����Ѵ�.
		Thread world = new Thread(new GameWorld(), "���Ӽ���");
		world.start();
		
		
	}
}
