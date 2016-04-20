package captcha;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class CaptchaCracking {
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

	public static void clickCaptcha(List<Integer> clickIndexes, int width, int height) {
		System.out.println("Clicking Captcha");
		Point p = ScreenScraping.getCaptchaUpperLeftCorner();
		int x = p.x;
		int y = p.y;
		
		int numCells = ScreenScraping.getNumberOfCells();
		int numRows = numCells;
		int numCols = numCells;
		
		int cellWidth = width / numCols;
		int cellHeight = height / numRows;

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
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Point okP = ScreenScraping.getOkCorrdinates();
		int okX = okP.x;
		int okY = okP.y;
		
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
		Point coordinates = ScreenScraping.getImNotRobotBoxCoordinates();
		robot.mouseMove(coordinates.x, coordinates.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void saveImage(){
		Point p = ScreenScraping.getCaptchaClickPoint();
		int x0 = p.x;
		int y0 = p.y;
		
		robot.mouseMove(x0, y0);
		robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		robot.keyPress(KeyEvent.VK_O);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			String urlString = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			BufferedImage image = getBufferedImageFromUrl(urlString);
			if(image != null){
				File f = new File("payload.jpg");
				try {
					ImageIO.write(image, "jpg", f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		} 
	}
	
	public static void activateWebsite(){
		int x = screenX + (int)(100.0 / 1680.0 * screenWidth);
		int y = screenY + (int)(100.0 / 1050.0 * screenHeight);
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);		
	}
	
	public static void getBlogabetTips() {
		
		// Get Sets to see which Mails were already cracked
		Set<String> crackedCaptchas = null;
		File crackedCaptchaSetFile = new File("crackedCaptchas.dat");
		if(crackedCaptchaSetFile.exists()){
			try{
	            FileInputStream fileInput = new FileInputStream(crackedCaptchaSetFile);
	            BufferedInputStream br = new BufferedInputStream(fileInput);
	            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
	            try {
	            	crackedCaptchas = (Set<String>)objectInputStream.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.exit(-1);
				}
	            objectInputStream.close();		
			}catch(Exception e){
				System.out.println("UNABLE TO LOAD CRACKD CAPTCHA SET\nPROGRAM WILL EXIT NOW!");
				System.exit(-1);
			}
		}
		else{
			crackedCaptchas = new HashSet<String>();
		}
		
		// GMailReader for sending the tips to our mail account
		GMailReader mailReader = new GMailReader();

		while(true){
			// Open blogabet site
			List<ParsedTextMail> mails = MailFetching.getBlogaBetTips(30);

			for(int i = 0; i < mails.size(); i++){
				ParsedTextMail mailToCheck = mails.get(i);
				String key = mailToCheck.content + mailToCheck.subject + mailToCheck.receivedDate;
				if(crackedCaptchas.contains(key)){
					System.out.println("Mail with subject: " + mailToCheck.subject + " already processed");
					continue;
				}

				String url = MailFetching.parseTipLinkFromMail(mailToCheck);
				try {
					openWebpage(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
		
				// Sleep and wait until the site opens
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-1);
				}
				
				int numberOfTries = 0;
				while(numberOfTries < 3){
					numberOfTries++;
					if(ScreenScraping.isImNotARobotWindow()){
						System.out.println("I am not a Robot");
						
						// Click "I'm not a Robot"
						clickIAmNotARobot();
			
						// Sleep
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.exit(-1);
						}
					}
						
					if(ScreenScraping.isCaptchaWindow()){
						System.out.println("Captcha");
						
						// download Image
						saveImage();
		
						// Sleep
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
						// Upload and crack Captcha if there was one
						String filePath = "payload.jpg";
						
						// Get Captcha task
						String captchaTask = ScreenScraping.getCaptchaTaskString();
		
						List<Integer> clickIndexes = null;
						try {
							for(int j = 0; j < 3; j++){
								clickIndexes = Captcha2API.breakCaptcha(filePath, captchaTask);
								if(clickIndexes != null)
									break;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println(clickIndexes);
						
						int width = ScreenScraping.getCaptchaWidth();
						int height = ScreenScraping.getCaptchaWidth();
						clickCaptcha(clickIndexes, width, height);
						numberOfTries++;
					} 
					else{
							System.out.println("No Captcha");
						}
					// Sleep
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();		
					}
				}		
			
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				activateWebsite();
				// Sleep
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
				if(ScreenScraping.isTipWindow()){
					// Copy the text into the clipboard
					copyText();
					// Sleep
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
					// Save the mail
					try {
						String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
						System.out.println(data);
						mailReader.sendMail("patrykhopner@gmail.com", "BlogaBetTips", data);
						
						// Everything went ok, we save this mail as cracked
						crackedCaptchas.add(key);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
					}		
				}			
			}
			
			// Save Set of cracked Captchas
			try{
				FileOutputStream fileOutput = new FileOutputStream(crackedCaptchaSetFile);
	            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
	            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
	            objectOutputStream.writeObject(crackedCaptchas);
	            objectOutputStream.close();
			}
            catch(Exception e){
            	e.printStackTrace();
            }
			
			// Close chrome
			Runtime rt = Runtime.getRuntime();

			try {
				rt.exec("taskkill /F /IM chrome.exe");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
			// Sleep at end of infinite Loop
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static BufferedImage getBufferedImageFromUrl(String urlString){
		BufferedImage image = null;
		try {
		    URL url = new URL(urlString);
		    image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void main(String[] args) {
		getBlogabetTips();
	}
}
