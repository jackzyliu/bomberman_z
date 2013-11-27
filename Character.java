import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javax.imageio.ImageIO;

/**
 * This class defines the character that the user controls.
 * @author Zheyuan Liu
 *
 */
public class Character extends GameObj {
	 public static final String img_file = "ultraman.jpeg";
	 public static final int SIZE = 40;
	 public static final int INIT_X = 0;
	 public static final int INIT_Y = 0;
	 public static final int INIT_VEL_X = 0;
	 public static final int INIT_VEL_Y = 0;
	 public int vel = 2;
	 public int range = 1;
	 public int num = 1;
	 public boolean onBubble = false;
	 
	 private static BufferedImage img;
	 
	 public Character(int courtWidth, int courtHeight) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, INIT_Y, 
				SIZE, SIZE, courtWidth, courtHeight);
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
   
   public void setDir(Direction d){
	   if (d == null) return;
		switch (d) {
		case UP:   
			v_y = -vel;
			break;
		case DOWN:  
			v_y = vel;
			break;
		case LEFT: 
			v_x = -vel;
			break;
		case RIGHT: 
			v_x = vel;
			break;
		}
		
   }
   
   public void resetHor(){
	   v_x = 0;
   }
   
   public void resetVer(){
	   v_y = 0;
   }
   
   public Bubble dropBubble(int COURT_HEIGHT, int COURT_WIDTH, Point[][] grid){
	   int i = (int)getCenter().y/ (COURT_HEIGHT/10) ;
	   int j = (int)getCenter().x/ (COURT_WIDTH/10) ;
	   onBubble(true);
	   return new Bubble (COURT_WIDTH, COURT_HEIGHT, 
						grid[i][j].x, grid[i][j].y);
	   
   }
   public void onBubble(boolean b){
	   this.onBubble = b;
   }
   

	/**
	 * This method specifies how the player/character should interact with
	 * bubble objects.
	 * 
	 * The player is only allowed to intersect with the bubble he/she just 
	 * dropped. 
	 * 
	 * The player is not allowed to traverse the bubble if he/she is already on
	 * one side of it.
	 */
	public void interactWithBubbles(Deque<Bubble> bubbles, Bubble last_bubble){
		//detects if the character is going to collide with a bubble
		if(last_bubble == null) return;
		Iterator<Bubble> itr = bubbles.iterator();
		while (itr.hasNext()){
			stop(hitObj(itr.next()));
		}
		if (onBubble){
			if(!intersects(last_bubble)){
				onBubble = false;
				if (!bubbles.contains(last_bubble)){
					bubbles.add(last_bubble);
					last_bubble = null;
				}
			}
			else{
				stop(relativePos(last_bubble));
			}
		}

	}
}
   
   
   

