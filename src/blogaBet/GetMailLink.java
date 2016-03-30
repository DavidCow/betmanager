package blogaBet;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class GetMailLink {
	
	// Coordinate constants
	private static final int screenX = 0;
	private static final int screenY = 0;
	private static final int screenWidth = 1680;
	private static final int screenHeight = 1050;
	
	// Robot
	private static Robot robot;
	static{
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void clickCaptcha(int[] areas, int numberOfAreas) {
		int x = 865;
		int y = 225;

		for (int i : areas) {
			robot.mouseMove(x + 400/numberOfAreas*(i%numberOfAreas), y+ 400/numberOfAreas*(i/numberOfAreas));
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		robot.mouseMove(1190, 650);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static void copyText(){	
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}
	
	public static void clickIAmNotARobot(){
		int x = screenX + (int)(715.0 / 1680.0 * screenWidth);
		int y = screenY + (int)(265.0 / 1050.0 * screenHeight);
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void downloadImage(){
		int x0 = screenX + (int)(1120.0 / 1680.0 * screenWidth);
		int y0 = screenY + (int)(410.0 / 1050.0 * screenHeight);
		robot.mouseMove(x0, y0);
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		int x1 = screenX + (int)(1140.0 / 1680.0 * screenWidth);
		int y1 = screenY + (int)(455.0 / 1050.0 * screenHeight);
		robot.mouseMove(x1, y1);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);		
	}
	
	public static void main(String[] args){

		// Open blogabet site
		String url = getMail();
		try {
			openWebpage(new URL(url));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Sleep and wait untill the site opens
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Click "I'm not a Robot"
		clickIAmNotARobot();
		
		// Sleep
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// download Image
		downloadImage();
		
		// Sleep
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		// Sleep
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Copy the text into the clipboard
		copyText();
		
		// Save the mail
		try {
			String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} 
	}

	public static String getMail() {
		GMailReader myMailReader = new GMailReader();
		List<ParsedTextMail> myMailList = myMailReader.read("logabet", 20);
		ParsedTextMail myMail = myMailList.get(myMailList.size() - 1);
		int start = myMail.content.indexOf("URL in a new browser window: https") + 29;
		int end = myMail.content.indexOf("</p>", start + 31);
		String html = myMail.content.substring(start, end);
		return html;
	}
}
