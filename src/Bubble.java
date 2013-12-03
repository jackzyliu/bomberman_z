import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;


public class Bubble extends GameObj {
	public static final String img_file = "Bombs/Bomb_f01.png";
	public static final int OBJ_WIDTH = 40;
	public static final int OBJ_HEIGHT = 40;
	
	public static final int IMG_WIDTH = 30;
	public static final int IMG_HEIGHT = 30;
	
	private int duration = 200;
	private int range;
	
	
	private static BufferedImage img;
	 
	public Bubble(int center_x, int center_y, int range) {
		super(0, 0, center_x - OBJ_WIDTH/2, center_y - OBJ_HEIGHT/2, 
				OBJ_WIDTH, OBJ_HEIGHT, Map.COURT_WIDTH, Map.COURT_HEIGHT);
	try {
		if (img == null) {
			img = ImageIO.read(new File(img_file));
		}
	} catch (IOException e) {
		System.out.println("Internal Error:" + e.getMessage());
	}
	this.range = range;
}

	@Override
	public void draw(Graphics g){
		int img_x = pos_x + (OBJ_WIDTH - IMG_WIDTH) / 2;
		int img_y = pos_y + (OBJ_HEIGHT - IMG_HEIGHT) / 2;
		g.drawImage(img, img_x, img_y, IMG_WIDTH, IMG_HEIGHT, null); 
		
	}
	
	public void countdown(){
		duration -= 1;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public Point getIndex(){
		int i = (int)(getCenter().y / Map.TILE_SIZE);
		int j = (int)(getCenter().x / Map.TILE_SIZE);
		return new Point (i,j);
	}
	
	public int getRange(){
		return range;
	}
}

