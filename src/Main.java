import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		try {
			ServidorUDP srv = new ServidorUDP();
			
			MicPlayer mPlayer = new MicPlayer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}