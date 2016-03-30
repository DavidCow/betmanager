package blogaBet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.SocketClient;

public class CaptchaTest {

	public static void main(String[] args) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("C:\\Users\\Patryk\\Desktop\\payload.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "jpg", os);
		    File outputfile = new File("saved.png");
		    ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SocketClient client = new SocketClient("NTAABET", "ntaabet");
		Captcha res = null;
		try {
			res = client.decode(new ByteArrayInputStream(os.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
}
