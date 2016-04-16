package captcha;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenScraping {
	
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
		try {
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
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
			
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Point(x, y);		
	}

	public static void main(String[] args) {


	}

}
