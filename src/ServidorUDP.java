import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

public class ServidorUDP {
	protected DatagramSocket socket = null;
	protected DatagramSocket socket2 = null;
	protected BufferedReader in = null;
	protected boolean moreQuotes = true;

	public ServidorUDP() throws IOException {		
		socket = new DatagramSocket(6565);
		socket2 = new DatagramSocket(6566);
		new Thread(new Runnable() {
			public void run() {
				while(true)
				iniciar();
			}
		}).start();
		
	}

	public void iniciar() {

		try {
				// Recibir del cliente 1
				byte[] buf = new byte[64000];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				System.out.println("Recibido packet 1");
				
				// Recibir del cliente 2
				byte[] buf2 = new byte[64000];
				DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length);
				socket2.receive(packet2);
				
				System.out.println("Recibido packet 2");
				
				if (packet.getLength() > 0) {
					buf = packet.getData();
					//DatagramSocket sock = new DatagramSocket();
					InetAddress address = packet2.getAddress();
					int port = packet2.getPort();
					socket2.send(new DatagramPacket(packet.getData(), packet.getLength(),
							address, 6566));
					
					System.out.println("Enviado Packet 1");
				}

				if (packet2.getLength() > 0) {					
					buf2 = packet2.getData();
					InetAddress address2 = packet.getAddress();
				
					
					socket.send(new DatagramPacket(packet2.getData(), packet2.getLength(),
							address2, 6565));
					System.out.println("Enviado Packet 2");
				}

			} catch (IOException e) {
				e.printStackTrace();

			}
	}
		
		
	
}
