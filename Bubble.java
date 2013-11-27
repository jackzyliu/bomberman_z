import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Deque;

import javax.imageio.ImageIO;


public class Bubble extends GameObj {
	public static final String img_file = "CartoonBomb.jpg";
	public static final int SIZE = 30;
	public int duration = 100;
	
	
	
	
	private static BufferedImage img;
	 
	public Bubble(int courtWidth, int courtHeight, int center_x, int center_y) {
		super(0, 0, center_x - SIZE/2, center_y - SIZE/2, SIZE, SIZE, courtWidth, courtHeight);
	try {
		if (img == null) {
			img = ImageIO.read(new File(img_file));
		}
	} catch (IOException e) {
		System.out.println("Internal Error:" + e.getMessage());
	}
}

	@Override
	public void draw(Graphics g){
	
	 g.drawImage(img, pos_x, pos_y, width, height, null); 
	}
	
	public void countdown(){
		duration -= 1;
	}
	
}

