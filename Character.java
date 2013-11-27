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
	 public static final String img_file = "Character/Front/Bman_f_f00.png";
	 public static final int WIDTH = 25;
	 public static final int HEIGHT = 10; //The height of image is, however, 40.
	 public static final int INIT_X = 40;
	 public static final int INIT_Y = 40;
	 public static final int INIT_VEL_X = 0;
	 public static final int INIT_VEL_Y = 0;
	 
	 public int vel = 1;
	 public int range = 1;
	 public int num = 2;
	 public boolean onBubble = false;
	 
	 private static BufferedImage img;
	 private Map map;
	 
	 public Character(int courtWidth, int courtHeight, Map map) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, (INIT_Y + HEIGHT * 3), 
				WIDTH, HEIGHT, courtWidth, courtHeight);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		this.map = map;
	}

   @Override
	public void draw(Graphics g){
		 g.drawImage(img, pos_x, (pos_y-height*3), width, (height * 4), null); 
	}
   
   @Override
	/**
	 * Moves the object by its velocity.  Ensures that the object does
	 * not go outside its bounds by clipping.
	 */
	public void move(){
		pos_x += v_x;
		pos_y += v_y;
		clip();
		
		if(!isWalkable(pos_x, pos_y)){
			pos_x -= v_x;
			pos_y -= v_y;
		}
		
	}
   
   public boolean isWalkable(int pos_x, int pos_y){
	   if (map.isBlocked(pos_x, pos_y)) {
			return false;
	   }
	   else if (map.isBlocked(pos_x, pos_y + height)) {
			return false;
	   }
	   else if (map.isBlocked(pos_x + width, pos_y )) {
			return false;
	   }
	   else if (map.isBlocked(pos_x + width, pos_y + height)) {
			return false;
	   }
	   else{
		   return true;
	   }
		
   }
   
   /**
    * This method sets the direction of the character
    * @param d
    */
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
   
   
   
   
   /**
    * Drop the bomb on the grid using the position of the center of the 
    * character's lower half body
    * 
    * @param COURT_HEIGHT
    * @param COURT_WIDTH
    * @param grid
    * @return
    */
   public Bubble dropBubble (int COURT_HEIGHT, int COURT_WIDTH, 
		   					Point[][] grid, Deque<Bubble> bubbles){

		if ( bubbles.size() < num){
			   int i = (int)getCenter().y / (COURT_HEIGHT/10) ;
			   int j = (int)getCenter().x/ (COURT_WIDTH/10) ;
			   onBubble = true;
			   return new Bubble (COURT_WIDTH, COURT_HEIGHT, 
							grid[i][j].x, grid[i][j].y);
		   }
	   else{
		   return null;
	   }
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
				if(last_bubble.duration <= (int)500*0.9){
					//allow time to traverse the bubble when first drop it
					stop(relativePos(last_bubble));
				}
			}
		}
	}

	/**
	 * 
	 * @param other game object
	 * @return the relative position of the OTHER compared to this object while
	 * the two objects are overlapping
	 */
	public Direction relativePos(GameObj other){
		int ob_x = getCenter().x;
		int ob_y = getCenter().y;
		int min_x = other.getCenter().x - other.width/4;
		int min_y = other.getCenter().y - other.height/4;
		int max_x = other.getCenter().x + other.width/4;
		int max_y = other.getCenter().y + other.height/4;
		if (ob_x + v_x < min_x){
			return Direction.RIGHT;
		}
		else if (ob_x + v_x > max_x){
			return Direction.LEFT;
		}
		if (ob_y + v_y > max_y){
			return Direction.UP;
		}
		else if (ob_y + v_y < min_y){
			return Direction.DOWN;
		}
		else {
			return null;
		}
	}
	
	
}
   
   
   

