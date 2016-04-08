package blogaBet;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import captcha.Captcha2API;

public class GetMailLink {
	
	// Coordinate constants
	private static final int screenX = 0;
	private static final int screenY = 0;
	private static final int screenWidth = 1680;
	private static final int screenHeight = 1050;
	
	// Download folder
	private static final String downloadFolder = "C:\\Users\\Patryk\\Desktop";
	
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

	public static void clickCaptcha(List<Integer> clickIndexes, int width, int height) {
		System.out.println("Clicking Captcha");
		int x = screenX + (int)(745.0 / 1680.0 * screenWidth);
		int y = screenY + (int)(225.0 / 1050.0 * screenHeight);
		
		int numRows = 3;
		int numCols = 3;
		int cellWidth = (int)(width * 1.0 / numCols);
		int cellHeight = (int)(height * 1.0 / numRows);

		for (int i : clickIndexes) {
			int col = (i - 1) % numCols;
			int row = (i - 1) / numRows;
			
			int clickX = x + cellWidth / 2 + col * cellWidth;
			int clickY = y + cellHeight / 2 + row * cellHeight;
			
			robot.mouseMove(clickX, clickY);
			System.out.println("Clicking on: " + clickX + "  " + clickY);
			
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int okX = screenX + (int)(1070.0 / 1680.0 * screenWidth);
		int okY = screenY + (int)(650.0 / 1050.0 * screenHeight);
		
		robot.mouseMove(okX, okY);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
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
	
	public static void activateWebsite(){
		int x = screenX + (int)(100.0 / 1680.0 * screenWidth);
		int y = screenY + (int)(100.0 / 1050.0 * screenHeight);
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);		
	}
	
	private static String getLastDownloadedFilPath(){
	    File uploadDirectory = new File(downloadFolder);
	    File[] downloadedFiles = uploadDirectory.listFiles();

	    Arrays.sort(downloadedFiles, new Comparator<File>() {
	        @Override
	        public int compare(File fileOne, File fileTwo) {
	            return Long.valueOf(fileOne.lastModified()).compareTo(fileTwo.lastModified());
	        }
	    });

	    for (File file : downloadedFiles) {
	        if (file.isFile() && System.currentTimeMillis() - file.lastModified() < 10000) {
	            return file.getAbsolutePath();
	        }
	    }	
	    return "";
	}
	
	public static void getBlogabetTip() {
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
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Upload and crack Captcha if there was one
		String filePath = getLastDownloadedFilPath();
		if (!filePath.isEmpty()) {
			System.out.println("Cracking Captcha");
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}

//			int width = img.getWidth();
//			int height = img.getHeight();
			
			int width = 390;
			int height = 390;

			List<Integer> clickIndexes = null;
			try {
				clickIndexes = Captcha2API.breakCaptcha(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(clickIndexes);
			clickCaptcha(clickIndexes, width, height);
		} else {
			System.out.println("No Captcha");
		}

		// Sleep
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		activateWebsite();
		// Sleep
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Copy the text into the clipboard
		copyText();
		// Sleep
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Save the mail
		try {
			String data = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);
			System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void main(String[] args){
		getBlogabetTip();
	}

	public static String getMail() {
		GMailReader myMailReader = new GMailReader();
		List<ParsedTextMail> myMailList = myMailReader.read("logabet", 20);
		ParsedTextMail myMail = myMailList.get(myMailList.size() - 3);
		int start = myMail.content.indexOf("URL in a new browser window: https") + 29;
		int end = myMail.content.indexOf("</p>", start + 31);
		String html = myMail.content.substring(start, end);
		return html;
	}
}
