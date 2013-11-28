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
 * 
 * A character is in charge of moving, dropping bubbles, track the state of his
 * or her own bubbles, and paint his or her bubbles.
 * 
 * Each character is assigned a code and interacts with the central map by the
 * given code. 
 * 
 * @author Zheyuan Liu
 *
 */
public class Character extends GameObj {
	 public static final String img_file = "Character/Front/Bman_f_f00.png";
	 public static final int WIDTH = 25;
	 public static final int HEIGHT = 20; //The height of image is, however, 40.
	 public static final int INIT_X = 40;
	 public static final int INIT_Y = 40;
	 public static final int INIT_VEL_X = 0;
	 public static final int INIT_VEL_Y = 0;
	 
	 public int vel = 1;
	 public int range = 1;
	 public int num = 5;
	 public boolean onBubble = false;
	 
	 public Deque<Bubble> bubbles;		//to keep track of the bubbles dropped
	 public Bubble last_bubble;			// the last bubble dropped;
	 private Iterator<Bubble> itr;    // the iterator to iterator over bubbles
	 
	 private static BufferedImage img;
	 private Map map;
	 private int code;				//the player's code, e.g player "1", player
	 								//"2"
	 
	 public Character(Map map, int code) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, (INIT_Y + HEIGHT ), 
				WIDTH, HEIGHT, Map.COURT_WIDTH, Map.COURT_HEIGHT);
		try {
			if (img == null) {
				img = ImageIO.read(new File(img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		this.map = map;
		this.bubbles = new ArrayDeque<Bubble>();
		this.code = code;
	}

   @Override
	public void draw(Graphics g){
		 g.drawImage(img, pos_x, (pos_y - height), width, (height * 2), null); 
	}
   
   /**
    * @return player's code
    */
   public int getCode(){
	   return code;
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
   
   /**
    * Determines if a block is walkable
    * @param pos_x
    * @param pos_y
    * @return
    */
   public boolean isWalkable(int pos_x, int pos_y){
	   if (map.isBlocked(pos_x, pos_y, this.code)) {
			return false;
	   }
	   else if (map.isBlocked(pos_x, pos_y + height,this.code)) {
			return false;
	   }
	   else if (map.isBlocked(pos_x + width, pos_y, this.code )) {
			return false;
	   }
	   else if (map.isBlocked(pos_x + width, pos_y + height, this.code)) {
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
   public void dropBubble (){
	   if(!onBubble){
		   if (bubbles.size() < num){
			   int i = (int)(getCenter().y / Map.TILE_SIZE);
			   int j = (int)(getCenter().x / Map.TILE_SIZE);
			   if(map.receiveInitBubbles(i, j, this.code)){
				   onBubble = true;
				   last_bubble = new Bubble (Map.COURT_WIDTH, Map.COURT_HEIGHT, 
								map.grid[i][j].x, map.grid[i][j].y);
		   		}
		   }
		   /*
		   else{
			   last_bubble = null;
		   }
		   */
	   }
   }


	/**
	 * This method specifies how the player/character should interact with
	 * the last bubble he/she just dropped
	 * 
	 * The player is only allowed to intersect with the bubble he/she just 
	 * dropped. 
	 * 
	 * The player is not allowed to traverse the bubble if he/she is already on
	 * one side of it.
	 */
	public void interactWithLastBubble(){
		//detects if the character is going to collide with a bubble
		//if(last_bubble == null) return;
		/*
		Iterator<Bubble> itr = bubbles.iterator();
		while (itr.hasNext()){
			//System.out.print("I am here");
			stop(hitObj(itr.next()));
		}
		*/
		if (onBubble){
			if(!intersects(last_bubble)){
				//turn the bubble into a neutral bubble, unspecified to any
				//single player
				onBubble = false;
				if (!bubbles.contains(last_bubble)){
					bubbles.add(last_bubble);
					int i = last_bubble.getIndex().x;
					int j = last_bubble.getIndex().y;
					map.changeToNeutralBubbles(i, j, this.code);
					last_bubble = null;
					
				}
			}
			else{
				if(last_bubble.getDuration() <= (int)500*0.9){
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
	
	/**
	 * This method tracks the duration of the player's bubbles as well as 
	 * turning bubbles into explosions
	 */
	public void trackBubbles(){
		itr = bubbles.iterator();
		while (itr.hasNext()){
			Bubble bubble = itr.next(); 
			bubble.countdown();
		}
		if (last_bubble!=null){
			last_bubble.countdown();
			if (last_bubble instanceof Explosion){
				if (last_bubble.getDuration() <=0 ){
					map.endExplosion(last_bubble.getIndex().x, last_bubble.getIndex().y);
					last_bubble = null;
					onBubble = false;
				}
			}
			else{
				if (last_bubble.getDuration() <= 0){
					//System.out.println("here");
					last_bubble = new Explosion
							(Map.COURT_WIDTH, Map.COURT_HEIGHT, 
									last_bubble.getCenter().x, last_bubble.getCenter().y);
					map.startExplosion(last_bubble.getIndex().x, last_bubble.getIndex().y);
				}
			}
		}
		
		if (!bubbles.isEmpty()){
			Bubble first = bubbles.getFirst();
			if(first instanceof Explosion){
				//System.out.println("here");
				if (first.getDuration() <=0 ){
					map.endExplosion(first.getIndex().x, first.getIndex().y);
					bubbles.removeFirst();
				}
			}
			else{
				if(first.getDuration() <= 0){
					int x = first.getCenter().x;
					int y = first.getCenter().y;
					map.startExplosion(first.getIndex().x, first.getIndex().y);
					bubbles.removeFirst();
					bubbles.addFirst
						(new Explosion(Map.COURT_WIDTH, Map.COURT_HEIGHT, x, y));
					
				}
			}
		}
	}
	

	/**
	 * This method paint the existing bubbles.
	 * NOTE: always paint bubbles before paint the character
	 */
	public void paintBubbles(Graphics g){
		itr = bubbles.iterator();
		while (itr.hasNext()){
			Bubble bubble_tmp = itr.next();
			bubble_tmp.draw(g);
		}
		//do not draw twice
		if(last_bubble != null && !bubbles.contains(last_bubble)){
			last_bubble.draw(g);
		}
	}

}
   
   
   

