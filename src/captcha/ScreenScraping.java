package captcha;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenScraping {
	
	// Colors of the blue boxes
	private final static int boxRed = 74;
	private final static int boxGreen = 144;
	private final static int boxBlue = 226;	
	
	private static Robot robot = null;
	static{
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.out.println("STATIC INITIALISATION FOR ROBOT FAILED!");
			System.out.println("PROGRAM WILL EXIT");
			System.exit(-1);
		}
	}
	
	
	private static int getRed(int color){
		return (color >> 16) & 0x000000FF;
	}
	
	private static int getGreen(int color){
		return (color >> 8) & 0x000000FF;
	}
	
	private static int getBlue(int color){
		return color & 0x000000FF;
	}
	
	private static Point getCaptchaBlueBoxUpperLeft(){
		// How much the colors can differ
		final int colorTolerance = 5;
		
		// The minimum width and height of the box
		final int minWidth = 250;
		final int minHeight = 80;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		
		for(int x = 0; x < width; x++){
			if(x > width - minWidth)
				continue;
			
			for(int y = 0; y < height; y++){
				if(y > height - minHeight)
					continue;
				
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(Math.abs(red - boxRed) <= colorTolerance && Math.abs(green - boxGreen) <= colorTolerance && Math.abs(blue - boxBlue) <= colorTolerance){
					boolean xOk = true;
					for(int x2 = x; x2 < x + minWidth; x2++){
						int indexInner = y * width + x2;
						int redInner = getRed(dataBuffInt[indexInner]);
						int greenInner = getGreen(dataBuffInt[indexInner]);
						int blueInner = getBlue(dataBuffInt[indexInner]);
						if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
								Math.abs(greenInner - boxGreen) <= colorTolerance && 
									Math.abs(blueInner - boxBlue) <= colorTolerance)){
							xOk = false;
							break;
						}
					}
					if(xOk){
						boolean yOk = true;
						for(int y2 = y; y2 < y + minHeight; y2++){
							int indexInner = y2 * width + x;
							int redInner = getRed(dataBuffInt[indexInner]);
							int greenInner = getGreen(dataBuffInt[indexInner]);
							int blueInner = getBlue(dataBuffInt[indexInner]);
							if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
									Math.abs(greenInner - boxGreen) <= colorTolerance && 
										Math.abs(blueInner - boxBlue) <= colorTolerance)){
								yOk = false;
								break;
							}
						}
						if(yOk){
							return new Point(x, y);
						}
					}
				}
			}
		}		
		return null;
	}
	
	private static Point getCaptchaBlueBoxUpperRight(){
		// How much the colors can differ
		final int colorTolerance = 5;
		
		// The minimum width and height of the box
		final int minWidth = 250;
		final int minHeight = 80;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		// Get Upper Right Corner
		for(int x = width - 1; x >= 0; x--){
			if(x <= minWidth)
				continue;
			
			for(int y = 0; y < width; y++){
				if(y > height - minHeight)
					continue;
				
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(Math.abs(red - boxRed) <= colorTolerance && Math.abs(green - boxGreen) <= colorTolerance && Math.abs(blue - boxBlue) <= colorTolerance){
					boolean xOk = true;
					for(int x2 = x; x2 >= x - minWidth; x2--){
						int indexInner = y * width + x2;
						int redInner = getRed(dataBuffInt[indexInner]);
						int greenInner = getGreen(dataBuffInt[indexInner]);
						int blueInner = getBlue(dataBuffInt[indexInner]);
						if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
								Math.abs(greenInner - boxGreen) <= colorTolerance && 
									Math.abs(blueInner - boxBlue) <= colorTolerance)){
							xOk = false;
							break;
						}
					}
					if(xOk){
						boolean yOk = true;
						for(int y2 = y; y2 < y + minHeight; y2++){
							int indexInner = y2 * width + x;
							int redInner = getRed(dataBuffInt[indexInner]);
							int greenInner = getGreen(dataBuffInt[indexInner]);
							int blueInner = getBlue(dataBuffInt[indexInner]);
							if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
									Math.abs(greenInner - boxGreen) <= colorTolerance && 
										Math.abs(blueInner - boxBlue) <= colorTolerance)){
								yOk = false;
								break;
							}
						}
						if(yOk){
							return new Point(x, y);
						}
					}				
				}	
			}
		}	
		return null;
	}
	
	private static Point getCaptchaBlueBoxBottomLeft(){
		// How much the colors can differ
		final int colorTolerance = 5;
		
		// The minimum width and height of the box
		final int minWidth = 250;
		final int minHeight = 80;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		
		for(int x = 0; x < width; x++){
			if(x > width - minWidth)
				continue;
			
			for(int y = height - 1; y >= 0; y--){
				if(y <= minHeight)
					continue;
				
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(Math.abs(red - boxRed) <= colorTolerance && Math.abs(green - boxGreen) <= colorTolerance && Math.abs(blue - boxBlue) <= colorTolerance){
					boolean xOk = true;
					for(int x2 = x; x2 < x + minWidth; x2++){
						int indexInner = y * width + x2;
						int redInner = getRed(dataBuffInt[indexInner]);
						int greenInner = getGreen(dataBuffInt[indexInner]);
						int blueInner = getBlue(dataBuffInt[indexInner]);
						if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
								Math.abs(greenInner - boxGreen) <= colorTolerance && 
									Math.abs(blueInner - boxBlue) <= colorTolerance)){
							xOk = false;
							break;
						}
					}
					if(xOk){
						boolean yOk = true;
						for(int y2 = y; y2 > y - minHeight; y2--){
							int indexInner = y2 * width + x;
							int redInner = getRed(dataBuffInt[indexInner]);
							int greenInner = getGreen(dataBuffInt[indexInner]);
							int blueInner = getBlue(dataBuffInt[indexInner]);
							if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
									Math.abs(greenInner - boxGreen) <= colorTolerance && 
										Math.abs(blueInner - boxBlue) <= colorTolerance)){
								yOk = false;
								break;
							}
						}
						if(yOk){
							return new Point(x, y);
						}
					}
				}		
			}
		}
		return null;
	}
	
	private static Point getCaptchaBlueBoxBottomRight(){
		// How much the colors can differ
		final int colorTolerance = 5;
		
		// The minimum width and height of the box
		final int minWidth = 250;
		final int minHeight = 80;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		
		for(int x = width - 1; x >= 0; x--){
			if(x <= minWidth)
				continue;
			
			for(int y = height - 1; y >= 0; y--){
				if(y <= minHeight)
					continue;
				
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(Math.abs(red - boxRed) <= colorTolerance && Math.abs(green - boxGreen) <= colorTolerance && Math.abs(blue - boxBlue) <= colorTolerance){
					boolean xOk = true;
					for(int x2 = x; x2 >= x - minWidth; x2--){
						int indexInner = y * width + x2;
						int redInner = getRed(dataBuffInt[indexInner]);
						int greenInner = getGreen(dataBuffInt[indexInner]);
						int blueInner = getBlue(dataBuffInt[indexInner]);
						if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
								Math.abs(greenInner - boxGreen) <= colorTolerance && 
									Math.abs(blueInner - boxBlue) <= colorTolerance)){
							xOk = false;
							break;
						}
					}
					if(xOk){
						boolean yOk = true;
						for(int y2 = y; y2 > y - minHeight; y2--){
							int indexInner = y2 * width + x;
							int redInner = getRed(dataBuffInt[indexInner]);
							int greenInner = getGreen(dataBuffInt[indexInner]);
							int blueInner = getBlue(dataBuffInt[indexInner]);
							if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
									Math.abs(greenInner - boxGreen) <= colorTolerance && 
										Math.abs(blueInner - boxBlue) <= colorTolerance)){
								yOk = false;
								break;
							}
						}
						if(yOk){
							return new Point(x, y);
						}
					}
				}		
			}
		}
		return null;
	}
	
	public static Point getImNotRobotBoxCoordinates(){
		int x = -1;
		int y = -1;
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		for(int i = 0; i < dataBuffInt.length; i++){
			if(getRed(dataBuffInt[i])==232 && getGreen(dataBuffInt[i])==232 && getBlue(dataBuffInt[i])==232){
				if(getRed(dataBuffInt[i+1])==203 && getGreen(dataBuffInt[i+1])==203 && getBlue(dataBuffInt[i+1])==203){
					if(getRed(dataBuffInt[i+2])==193 && getGreen(dataBuffInt[i+2])==193 && getBlue(dataBuffInt[i+2])==193){
						if(getRed(dataBuffInt[i+27])==232 && getGreen(dataBuffInt[i+27])==232 && getBlue(dataBuffInt[i+27])==232){
							x = (i % width)+13;
							y = (i / width)+13;
							break;
						}
					}
				}
			}				
		}
		return new Point(x, y);		
	}
	
	public static boolean isImNotARobotWindow(){
		Point p = getImNotRobotBoxCoordinates();
		if(p.x != -1 && p.y != -1)
			return true;
		return false;
	}
	
	/**
	 * 
	 * @return Wether a Captcha Window is open in the Screenshot
	 */
	public static boolean isCaptchaWindow(){	
		Point p0 = getCaptchaBlueBoxUpperLeft();
		Point p1 = getCaptchaBlueBoxBottomRight();
		if(p0 != null && p1 != null && p0.x != -1 && p1.x != -1)
			return true;
		return false;
	}
	
	public static Point getCaptchaUpperLeftCorner(){
		Point p = getCaptchaBlueBoxBottomLeft();
		if(p != null){
			return new Point(p.x, p.y + 10);
		}
		return null;
	}
	
	/**
	 * 
	 * @return The Point for the Right Click on the Captcha
	 */
	public static Point getCaptchaClickPoint(){
		Point p = null;
		
		// How much the colors can differ
		final int colorTolerance = 5;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		
		int resX0 = -1;
		int resY0 = -1;
		Point p0 = getCaptchaBlueBoxBottomLeft();
		if(p0 != null){
			resX0 = p0.x;
			resY0 = p0.y;
		}
		
		int resX1 = -1;

		Point p1 = getCaptchaBlueBoxUpperRight();
		if(p1 != null){
			resX1 = p1.x;
		}
		
		// Get Bottom Line
		int bottomY = -1;
		int startX = resX0 + 10;
		int endX = resX1 - 10;
		
		int bottomRed = 206;
		int bottomGreen = 206;
		int bottomBlue = 206;
		
		for(int y = resY0 + 10; y < resY0 + height; y++){
			boolean xOk = true;
			for(int x = startX; x < endX; x++){
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(!(Math.abs(red - bottomRed) <= colorTolerance && Math.abs(green - bottomGreen) <= colorTolerance && Math.abs(blue - bottomBlue) <= colorTolerance)){		
					xOk = false;
					break;
				}
			}
			if(xOk){
				bottomY = y;
				break;
			}
		}
		
		if(resX0 != -1 && resX1 != -1 && bottomY != -1){
			p = new Point(resX0 + 10, (resY0 + bottomY) / 2);
		}
		return p;	
	}
	
	/**
	 * 
	 * @return The width of the Captcha as displayed by Chrome
	 */
	public static int getCaptchaWidth(){
		Point p0 = getCaptchaBlueBoxUpperLeft();
		Point p1 = getCaptchaBlueBoxUpperRight();
		if(p0 != null && p1 != null){
			return p1.x - p0.x;
		}
		return -1;
	}
	
	public static Point getOkCorrdinates(){
		// How much the colors can differ
		final int colorTolerance = 5;
		
		// The minimum width and height of the box
		final int minWidth = 70;
		final int minHeight = 20;
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
				
		for(int y = height - 1; y >= 0; y--){
			if(y <= minHeight)
				continue;
								
				for(int x = 0; x < width; x++){
					if(x > width - minWidth)
						continue;
				
				int index = y * width + x;
				int red = getRed(dataBuffInt[index]);
				int green = getGreen(dataBuffInt[index]);
				int blue = getBlue(dataBuffInt[index]);
				if(Math.abs(red - boxRed) <= colorTolerance && Math.abs(green - boxGreen) <= colorTolerance && Math.abs(blue - boxBlue) <= colorTolerance){
					boolean xOk = true;
					for(int x2 = x; x2 < x + minWidth; x2++){
						int indexInner = y * width + x2;
						int redInner = getRed(dataBuffInt[indexInner]);
						int greenInner = getGreen(dataBuffInt[indexInner]);
						int blueInner = getBlue(dataBuffInt[indexInner]);
						if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
								Math.abs(greenInner - boxGreen) <= colorTolerance && 
									Math.abs(blueInner - boxBlue) <= colorTolerance)){
							xOk = false;
							break;
						}
					}
					if(xOk){
						boolean yOk = true;
						for(int y2 = y; y2 < y - minHeight; y2--){
							int indexInner = y2 * width + x;
							int redInner = getRed(dataBuffInt[indexInner]);
							int greenInner = getGreen(dataBuffInt[indexInner]);
							int blueInner = getBlue(dataBuffInt[indexInner]);
							if(!(Math.abs(redInner - boxRed) <= colorTolerance && 
									Math.abs(greenInner - boxGreen) <= colorTolerance && 
										Math.abs(blueInner - boxBlue) <= colorTolerance)){
								yOk = false;
								break;
							}
						}
						if(yOk){
							return new Point(x + 20, y - 20);
						}
					}
				}
			}
		}		
		return null;		
	}

	
	public static int getNumberOfCells(){	
		// Bottom Left Corner
		int resX0 = -1;
		int resY0 = -1;
		Point p0 = getCaptchaBlueBoxBottomLeft();
		if(p0 != null){
			resX0 = p0.x;
			resY0 = p0.y;
		}

		// Upper Right
		int resX1 = -1;

		Point p1 = getCaptchaBlueBoxUpperRight();
		if(p1 != null){
			resX1 = p1.x;
		}
		
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
			
		if(resX0 != -1 && resX1 != -1){
			int numLines = 0;
			int minHeightLine = 300;
			int startX = resX0 + 10;
			int endX = resX1 - 10;
			int startY = resY0 + 20;
			
			for(int x = startX; x < endX; x++){
				boolean xOk = true;
				for(int y = startY; y < startY + minHeightLine; y++){
					int index = y * width + x;
					int red = getRed(dataBuffInt[index]);
					int green = getGreen(dataBuffInt[index]);
					int blue = getBlue(dataBuffInt[index]);
					if(!(red > 245 && green > 245 && blue > 245)){
						xOk = false;
						break;
					}
				}
				if(xOk){
					numLines++;
					x = Math.min(endX, x + 20);
				}
			}
			return numLines + 1;
		}		
		return 0;
	}
	
	public static String getCaptchaTaskString(){
		Point upperLeft = getCaptchaBlueBoxUpperLeft();
		Point bottomRight = getCaptchaBlueBoxBottomRight();
		if(upperLeft != null && bottomRight != null){
			int x = (bottomRight.x - upperLeft.x)/2 + upperLeft.x;
			int y = (bottomRight.y - upperLeft.y)/2 + upperLeft.y;
			robot.mouseMove(x, y);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);	
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_A);
			robot.keyRelease(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_C);
			robot.keyRelease(KeyEvent.VK_C);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				String taskString = (String) Toolkit.getDefaultToolkit()
						.getSystemClipboard().getData(DataFlavor.stringFlavor);
				taskString = taskString.replaceAll("\\..*", "");
				return taskString;
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}		
		}
		
		return "";		
	}
	
	public static boolean isTipWindow(){
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		for(int i = 0; i < dataBuffInt.length; i++){
			if(getRed(dataBuffInt[i])==172 && getGreen(dataBuffInt[i])==242 && getBlue(dataBuffInt[i])==246)
				if(getRed(dataBuffInt[i+1])==251 && getGreen(dataBuffInt[i+1])==255 && getBlue(dataBuffInt[i+1])==253)
					if(getRed(dataBuffInt[i+2])==243 && getGreen(dataBuffInt[i+2])==244 && getBlue(dataBuffInt[i+2])==225)
						if(getRed(dataBuffInt[i+3])==145 && getGreen(dataBuffInt[i+3])==218 && getBlue(dataBuffInt[i+3])==202)
							return true;
			if(getRed(dataBuffInt[i])==140 && getGreen(dataBuffInt[i])==51 && getBlue(dataBuffInt[i])==51)
				if(getRed(dataBuffInt[i+5])==51 && getGreen(dataBuffInt[i+5])==51 && getBlue(dataBuffInt[i+5])==51)
					if(getRed(dataBuffInt[i+9])==51 && getGreen(dataBuffInt[i+9])==140 && getBlue(dataBuffInt[i+9])==210)
						if(getRed(dataBuffInt[i+10])==255 && getGreen(dataBuffInt[i+10])==255 && getBlue(dataBuffInt[i+10])==255)
							return true;
				
		}
		return false;
	}
	
	public static Point getShowMoreDetailButton(){
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		for(int i = 0; i < dataBuffInt.length; i++){
			if(getRed(dataBuffInt[i])==210 && getGreen(dataBuffInt[i])==140 && getBlue(dataBuffInt[i])==51)
				if(getRed(dataBuffInt[i+1])==51 && getGreen(dataBuffInt[i+1])==51 && getBlue(dataBuffInt[i+1])==51)
					if(getRed(dataBuffInt[i+5])==140 && getGreen(dataBuffInt[i+5])==204 && getBlue(dataBuffInt[i+5])==255)
						if(getRed(dataBuffInt[i+6])==255 && getGreen(dataBuffInt[i+6])==255 && getBlue(dataBuffInt[i+6])==255)
							if(getRed(dataBuffInt[i+7])==204 && getGreen(dataBuffInt[i+7])==140 && getBlue(dataBuffInt[i+7])==51){
								int x = (i % width)+6;
								int y = (i / width)+4;
								return new Point(x,y);
							}
								
				
		}
		return null;
	}
	
	public static boolean isComboBetTip(){
		if(getShowMoreDetailButton() == null)
			return false;
		else
			return true;
	}
	

	public static void main(String[] args) {
//		Point p0 = getCaptchaBlueBoxUpperLeft();
//		Point p1 = getCaptchaBlueBoxUpperRight();
//		Point p2 = getCaptchaBlueBoxBottomLeft();
//		Point p3 = getCaptchaBlueBoxBottomRight();
//		Point p4 = getCaptchaClickPoint();
//		int c = getNumberOfCells();
//		System.out.println(p0);
//		System.out.println(p1);
//		System.out.println(p2);
//		System.out.println(p3);
//		System.out.println(p4);
//		Point p0 = getCaptchaBlueBoxUpperLeft();
//		Point p1 = getCaptchaBlueBoxUpperRight();
//		Point p2 = getCaptchaBlueBoxBottomLeft();
//		Point p3 = getCaptchaBlueBoxBottomRight();
//		System.out.println(p0);
//		System.out.println(p1);
//		System.out.println(p2);
//		System.out.println(p3);
		//String s = getCaptchaTaskString();
		//System.out.println(s);
		System.out.println(isTipWindow());
	}

}
