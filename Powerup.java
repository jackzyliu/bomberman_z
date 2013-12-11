import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class specifies the behavior of power-ups.
 * @author Zheyuan Liu
 *
 */
public class Powerup extends GameObj{
	public static final int OBJ_WIDTH = 40;
	public static final int OBJ_HEIGHT = 40;
	
	public static final int IMG_WIDTH = 30;
	public static final int IMG_HEIGHT = 30;

	private static BufferedImage img;
	
	private boolean visible;
	
	public Powerup(int courtWidth, int courtHeight, int center_x, int center_y){
		super(0, 0, center_x - OBJ_WIDTH/2, center_y - OBJ_HEIGHT/2, 
				OBJ_WIDTH, OBJ_HEIGHT, courtWidth, courtHeight);
		
	}
	
	@Override
	public void draw(Graphics g){
		int img_x = pos_x + (OBJ_WIDTH - IMG_WIDTH) / 2;
		int img_y = pos_y + (OBJ_HEIGHT - IMG_HEIGHT) / 2;
		g.drawImage(img, img_x, img_y, IMG_WIDTH, IMG_HEIGHT, null); 
	}
	
	public Point getIndex(){
		int i = (int)(getCenter().y / Map.TILE_SIZE);
		int j = (int)(getCenter().x / Map.TILE_SIZE);
		return new Point (i,j);
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public void turnVisible(){
		visible = true;
	}
	
	public void turnInvisible(){
		visible = false;
	}
}
