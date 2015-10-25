import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class MicPlayer extends JFrame implements ActionListener {

	private static JTextField tfIP;
	private static JTextField tfPort;
	private JButton btnGrabar, btnEnviar, btnReproducir;
	byte tempBuffer[];
	JProgressBar barDo;
	byte bRepro[] = null;


	public MicPlayer() {
		JPanel northPanel = new JPanel(new GridLayout(3, 3));
		JPanel panelBar = new JPanel(new GridLayout(1, 1));
		JPanel serverAndPort = new JPanel(new GridLayout(1, 4));
		JPanel panelButtons = new JPanel(new GridLayout(1, 3));
		tfPort = new JTextField("", JTextField.LEFT);
		tfIP = new JTextField("", JTextField.LEFT);
		btnGrabar = new JButton("Grabar");
		btnEnviar = new JButton("Enviar");
		btnReproducir = new JButton("Reproducir");

		// Progress bar
		barDo = new JProgressBar(0, 100);

		panelBar.add(barDo);

		serverAndPort.add(new JLabel("IP:"));
		serverAndPort.add(tfIP);
		serverAndPort.add(new JLabel("Puerto:"));
		serverAndPort.add(tfPort);

		panelButtons.add(btnGrabar);
		panelButtons.add(btnReproducir);
		panelButtons.add(btnEnviar);

		btnGrabar.addActionListener(this);
		btnEnviar.addActionListener(this);
		btnReproducir.addActionListener(this);

		northPanel.add(panelBar);
		northPanel.add(serverAndPort);
		northPanel.add(panelButtons);

		add(northPanel, BorderLayout.NORTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 200);
		setVisible(true);

		new Thread(new Runnable() {
			public void run() {
				while (true) {
					byte b[] = null;
					b = receiveThruUDP();
					if (b != null)
						bRepro = b;
				}
			}
		}).start();
	}

	
	public static AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	public static void sendThruUDP(byte soundpacket[]) {
		try {
			DatagramSocket sock = new DatagramSocket();
			sock.send(new DatagramPacket(soundpacket, soundpacket.length,
					InetAddress.getByName(tfIP.getText().toString()), Integer
							.valueOf(tfPort.getText().toString())));
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" Unable to senddd soundpacket using UDP ");
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object o = arg0.getSource();

		if (o == btnGrabar) {
			grabar();
		}
		else if (o == btnEnviar) {
			enviar();
		}
		else if (o == btnReproducir) {
			reproducir();
		}

	}

	private void grabar() {

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} 

				catch (InterruptedException err) {
				}
				for (int i = 0; i <= 100; i++) {

					barDo.setValue(i);

					barDo.repaint();

					try {
						Thread.sleep(40);
					} 

					catch (InterruptedException err) {
					}
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
					try {

						DataLine.Info dataLineInfo = new DataLine.Info(
								TargetDataLine.class, getAudioFormat());
						TargetDataLine targetDataLine = (TargetDataLine) AudioSystem
								.getLine(dataLineInfo);
						targetDataLine.open(getAudioFormat());
						targetDataLine.start();
						tempBuffer = new byte[64000];
						targetDataLine.read(tempBuffer, 0, tempBuffer.length);
						//targetDataLine.stop();
						//targetDataLine.close();
						barDo.setValue(0);

						barDo.repaint();
						
					} catch (Exception e) {
						System.out.println(e.getMessage());
						//System.exit(0);
					}
				}
			}
		}).start();

	}

	private void enviar() {
		sendThruUDP(tempBuffer);
		System.out.println("Enviado");
	}

	private void reproducir() {

		toSpeaker(bRepro);

	}

	public static byte[] receiveThruUDP() {
		try {
			DatagramSocket sock = new DatagramSocket(6565);
			byte soundpacket[] = new byte[640000];
			DatagramPacket datagram = new DatagramPacket(soundpacket,
					soundpacket.length);
			sock.receive(datagram);
			sock.close();
			return datagram.getData();
		} catch (Exception e) {
			return null;
		}

	}

	public void toSpeaker(final byte soundbytes[]) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} 

				catch (InterruptedException err) {
				}
				for (int i = 0; i <= 100; i++) {

					barDo.setValue(i);

					barDo.repaint();

					try {
						Thread.sleep(40);
					} 

					catch (InterruptedException err) {
					}
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					DataLine.Info dataLineInfo = new DataLine.Info(
							SourceDataLine.class, getAudioFormat());
					SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem
							.getLine(dataLineInfo);
					sourceDataLine.open(getAudioFormat());
					sourceDataLine.start();
					sourceDataLine.write(soundbytes, 0, soundbytes.length);
					sourceDataLine.drain();
					sourceDataLine.close();
				} catch (Exception e) {
					System.out.println("No hay nada a reproducir");
				}

			}
		}).start();


	}
}