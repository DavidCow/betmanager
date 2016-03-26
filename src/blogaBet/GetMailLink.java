package blogaBet;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class GetMailLink {

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

	public static void clickButton(int[] areas, int numberOfAreas) {
		int x = 865;
		int y = 225;
		Robot myRobot = null;
		try {
			myRobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i : areas) {
			myRobot.mouseMove(x + 400/numberOfAreas*(i%numberOfAreas), y+ 400/numberOfAreas*(i/numberOfAreas));
			myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		myRobot.mouseMove(1190, 650);
		myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static void clickDetails(){
		Robot myRobot = null;
		try {
			myRobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	myRobot.mouseMove(665, 237);
	//	myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	//	myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		
		myRobot.keyPress(KeyEvent.VK_CONTROL);
		myRobot.keyPress(KeyEvent.VK_A);
		myRobot.keyRelease(KeyEvent.VK_A);
		myRobot.keyPress(KeyEvent.VK_C);
		myRobot.keyRelease(KeyEvent.VK_C);
		myRobot.keyRelease(KeyEvent.VK_CONTROL);
	}
	
	public static void main(String[] args) {

		/**
		 * System.setProperty("webdriver.chrome.driver",
		 * "D:\\chromedriver.exe"); ChromeOptions options = new ChromeOptions();
		 * // if you like to specify another profile
		 * options.addArguments("user-data-dir=/root/Downloads/aaa");
		 * options.addArguments("start-maximized"); DesiredCapabilities
		 * capabilities = DesiredCapabilities.chrome();
		 * capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		 * WebDriver driver = new ChromeDriver(capabilities);
		 */
		String url = getMail();
		// driver.get(url);
		Robot myRobot = null;
		try {
			openWebpage(new URI(url));
			myRobot = new Robot();
			Thread.sleep(2000);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myRobot.mouseMove(830, 265);
		myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		myRobot.mouseMove(1120, 410);
		myRobot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		myRobot.mouseMove(1140, 455);
		myRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String scriptToExecute = "var performance = window.performance ||
		// window.mozPerformance || window.msPerformance ||
		// window.webkitPerformance || {}; var network =
		// performance.getEntries() || {}; return network;";
		// String scriptToExecute = " return performance.getEntries()";
		// String netData =
		// ((JavascriptExecutor)driver).executeScript(scriptToExecute).toString();
		// System.out.println(netData);

		myRobot.keyPress(KeyEvent.VK_ENTER);
		myRobot.keyRelease(KeyEvent.VK_ENTER);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clickDetails();

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

	public static void getWebpage(String html) {
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL(html);
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is));

			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
	}

}
