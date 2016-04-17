package captcha;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenScraping {
	
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
	
	public static int getRed(int color){
		return (color >> 16) & 0x000000FF;
	}
	
	public static int getGreen(int color){
		return (color >> 8) & 0x000000FF;
	}
	
	public static int getBlue(int color){
		return color & 0x000000FF;
	}
	
	public static Point getImNotRobotBoxCoordinates(){
		int x = -1;
		int y = -1;
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		System.out.println(dataBuffInt.length);
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
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = robot.createScreenCapture(screenRect);
		int width = capture.getWidth();
		int height = capture.getHeight();
		int[] dataBuffInt = capture.getRGB(0, 0, width, height, null, 0, width);
		System.out.println(dataBuffInt.length);
		for(int i = 0; i < dataBuffInt.length; i++){
			if(getRed(dataBuffInt[i])==232 && getGreen(dataBuffInt[i])==232 && getBlue(dataBuffInt[i])==232){
				if(getRed(dataBuffInt[i+1])==203 && getGreen(dataBuffInt[i+1])==203 && getBlue(dataBuffInt[i+1])==203){
					if(getRed(dataBuffInt[i+2])==193 && getGreen(dataBuffInt[i+2])==193 && getBlue(dataBuffInt[i+2])==193){
						if(getRed(dataBuffInt[i+27])==232 && getGreen(dataBuffInt[i+27])==232 && getBlue(dataBuffInt[i+27])==232){
							return true;
						}
					}
				}
			}				
		}
		return false;			
	}
	
	/**
	 * 
	 * @return Wether a Captcha Window is open in the Screenshot
	 */
	public static boolean isCaptchaWindow(){	
		// Colors of the blue box on the top
		final int boxRed = 74;
		final int boxGreen = 144;
		final int boxBlue = 226;
		
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
							return true;
						}
					}
				}
			}
		}		
		return false;
	}
	
	public static Point getCaptchaUpperLeftCorner(){
		// Colors of the blue box on the top
		final int boxRed = 74;
		final int boxGreen = 144;
		final int boxBlue = 226;
		
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
		
		// Bottom Left Corner of blue box
		for(int x = 0; x < width; x++){
			if(x > width - minWidth)
				continue;
			
			for(int y = height -1; y >= 0; y--){
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
							return new Point(x, y + 10);
						}
					}
				}
			}
		}				
		return null;
	}
	
	/**
	 * 
	 * @return The Point for the Right Click on the Captcha
	 */
	public static Point getCaptchaClickPoint(){
		Point p = null;
		
		// Colors of the blue box on the top
		final int boxRed = 74;
		final int boxGreen = 144;
		final int boxBlue = 226;
		
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
		
		int resX0 = -1;
		int resY0 = -1;
		
		int resX1 = -1;
		int resY1 = -1;
		
		int bottomY = -1;
		
		// Get Upper Left Corner
		for(int x = 0; x < width; x++){
			if(x > width - minWidth)
				continue;
			
			boolean breakX = false;
		
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
							resX0 = x;
							resY0 = y;
							breakX = true;
							break;
						}
					}
				}		
			}
			if(breakX)
				break;
		}
		
		// Get Upper Right Corner
		for(int x = width - 1; x >= 0; x--){
			if(x <= minWidth)
				continue;
			
			boolean breakX = false;
			
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
						for(int y2 = y; y2 >= y - minHeight; y2--){
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
							resX1 = x;
							resY1 = y;
							breakX = true;
							break;
						}
					}				
				}	
			}
			if(breakX)
				break;
		}
		
		// Get Bottom Line
		int startX = resX0 + 10;
		int endX = resX1 - 10;
		
		int bottomRed = 206;
		int bottomGreen = 206;
		int bottomBlue = 206;
		
		for(int y = resY0; y < height; y++){
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
		// Colors of the blue box on the top
		final int boxRed = 74;
		final int boxGreen = 144;
		final int boxBlue = 226;
		
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
		
		int resX0 = -1;
		int resY0 = -1;
		
		int resX1 = -1;
		int resY1 = -1;
		
		int bottomY = -1;
	
		
		// Get Upper Left Corner
		for(int x = 0; x < width; x++){
			if(x > width - minWidth)
				continue;
			
			boolean breakX = false;
			
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
							resX0 = x;
							resY0 = y;
							breakX = true;
							break;
						}
					}
				}		
			}
			if(breakX)
				break;
		}
		
		// Get Upper Right Corner
		for(int x = width - 1; x >= 0; x--){
			boolean breakX = false;
			
			if(x <= minWidth)
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
							resX1 = x;
							resY1 = y;
							breakX = true;
							break;
						}
					}
				}	
			}
			if(breakX)
				break;
		}	
		
		if(resX0 != -1 && resX1 != -1){
			return (resX1 - resX0);
		}
		return -1;
	}
	
	public static Point getOkCorrdinates(){
		// Colors of the blue box on the top
		final int boxRed = 74;
		final int boxGreen = 144;
		final int boxBlue = 226;
		
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

	public static void main(String[] args) {
		boolean c = isCaptchaWindow();
		Point p = getCaptchaClickPoint();
		int w = getCaptchaWidth();
		Point p2 = getCaptchaUpperLeftCorner();
		Point p3 = getOkCorrdinates();
		System.out.println(c);
		System.out.println(p);
		System.out.println(w);
		System.out.println(p2);
		System.out.println(p3);
	}

}
