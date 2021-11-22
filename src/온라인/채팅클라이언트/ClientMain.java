package 온라인.채팅클라이언트;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ClientMain {
	public static void main(String[] args) {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			String ip_str = ia.toString();
			String ip = ip_str.substring(ip_str.indexOf("/")+1);
			new Client(ip, 5555);
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
