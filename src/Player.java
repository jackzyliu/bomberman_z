import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;



/**
 * This class defines the character that the user controls.
 * 
 * A character is in charge of moving, dropping bubbles, collecting items,
 * and tracking and painting his or her bubbles/explosions.
 * 
 * Once the player LEAVES his/her last bubble, the bubble gets pushed to the 
 * central map and becomes a "neutral bubble". Then the map will take over and 
 * track as well as paint the neutral bubbles.
 * 
 * However, the player gets a local copy of how many bubbles he has put that 
 * have not exploded yet.
 *
 * Each character is assigned a code and interacts with the central map by the
 * given code. 
 * 
 * @author Zheyuan Liu
 *
 */
public class Player extends GameObj {
	
	 public static final String dead_img = "Character/dead_player.gif";
	 
	
	/**
	 * Constants Section
	 */
	 public static final int WIDTH = 25;
	 public static final int HEIGHT = 20; //The height of image is, however, 40.
	 public static final int INIT_VEL_X = 0;
	 public static final int INIT_VEL_Y = 0;
	 
	 public static final int MAX_VEL = 3;
	 public static final int MAX_RANGE = 8;
	 public static final int MAX_NUM = 6;
	 
	 public static final int DEATH_PEN = 500;     //5 sec of death penalty
	 public static final int RESPAWN = 300;		  //3 sec of respawn 
	 //TODO better timing options : System.currentMillisTime()
	 
	 /**
	  * Player Stats
	  */
	 protected int vel = 1;
	 private int range = 1;
	 private int num = 1;
	 private boolean onBubble = false;
	 private int death_times = 0;

	 /**
	  * Utility Section
	  */
	 private ArrayList<Bubble> my_bubbles;		
	 //to keep track of the bubbles dropped
	 //use arraylist because index-specific concurrent modification is required
	 private Iterator<Bubble> itr_b;    // the iterator to iterator over bubbles
	 private Bubble last_bubble;			// the last bubble dropped;
	 private Hashtable<Powerup, Integer> my_items;
	 //to keep track of the items;


	 protected Map map;
	 protected int code;				//the player's code, e.g player "1", player
	 								//"2"
	 /**
	  * State Control Section
	  */
	 protected enum PlayerState{
		 DEAD,
		 RESPAWN,
		 NORMAL,
		 VICTORY;
	 }
	 
	 private PlayerState state;
	 
	 private int respawn_invulnerable = RESPAWN;  //2 seconds of invulnerability
	 private int death_time = DEATH_PEN;           //5 seconds of death-penalty
	 
	 /**
	  * Animation Section
	  */
	 protected Animation animation;
	 protected Direction direction;
	 protected boolean is_idle;
	 protected boolean display;
	 protected boolean dead_display;
	 private BufferedImage[] front;
	 private BufferedImage[] back;
	 private BufferedImage[] right;
	 private BufferedImage dead;

	 
	 /**
	  * Constructor
	  * @param map
	  * @param code
	  */
	 public Player(Map map, int code) {
		
		super(INIT_VEL_X, INIT_VEL_Y, 
				map.getPlayerInitPosition(code).x, 
				map.getPlayerInitPosition(code).y + HEIGHT, 
				WIDTH, HEIGHT, 
				Map.COURT_WIDTH, Map.COURT_HEIGHT);
		
		try{
			String img_file;
			
			front = new BufferedImage[8];
			back = new BufferedImage[8];
			right = new BufferedImage[8];
			
			for (int i = 0 ; i < front.length; i ++){
				img_file = 
						"Character/Front/Bman_F_f0"+Integer.toString(i)+".png";
				front[i] = ImageIO.read(new File(img_file));
			}
			
			for (int i = 0 ; i < back.length; i ++){
				img_file = 
						"Character/Back/Bman_B_f0"+Integer.toString(i)+".png";
				back[i] = ImageIO.read(new File(img_file));
			}
			for (int i = 0 ; i < right.length; i ++){
				img_file = 
						"Character/Side/Bman_F_f0"+Integer.toString(i)+".png";
				right[i] = ImageIO.read(new File(img_file));
			}
			
			dead = ImageIO.read(new File(dead_img));
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	
		this.map = map;
		this.my_bubbles = new ArrayList<Bubble>();
		this.code = code;
		//this.explosions = new ArrayDeque<Explosion>();
		this.my_items = new Hashtable<Powerup, Integer>();
		animation = new Animation();
		direction = Direction.DOWN;
		is_idle = true;
		state = PlayerState.RESPAWN;
		display = true;
		dead_display = false;
		
	}
	 
	 
	 /**
	  * This method sets the animation for the player
	  */
	public void setAniamtion(){

		if (v_x == 0 && v_y ==0){
			is_idle = true;
		}
		else{
			is_idle = false;
			switch(direction){
			case UP: 
				animation.setFrames(back);
				animation.setDelay(100 / vel);
				break;
			case DOWN:
				animation.setFrames(front);
				animation.setDelay(100 / vel);
				break;
			case RIGHT: case LEFT: 
				animation.setFrames(right);
				animation.setDelay(100 / vel);
				break;
			}
			animation.update();
		}
	}
	
	
   @Override
	public void draw(Graphics g){
	   String label = "P" + Integer.toString(code);
	   g.setFont(new Font("default", Font.BOLD, 16));
	   
	   if(state == PlayerState.DEAD){
		   if(dead_display){
			   g.drawString(label, pos_x, (pos_y - height));
			   g.drawImage(dead, 
					   	   pos_x, 
					   	   (pos_y - height), 
					   	   width, 
					   	   (height * 2), 
					   	   null); 
		   }
	   }
		   //do not draw
	   else{
		   if(display) {
			   //if idle
			   if (is_idle){
				   BufferedImage img;
				   switch(direction){
				   case UP: img = back[0]; break;
				   case DOWN: img = front[0]; break;
				   case RIGHT: case LEFT: img = right[0]; break;
				   default: img = front[0]; break;
				   }
				   if(direction == Direction.LEFT){
					   //flip the image
					   
					   g.drawString(label, pos_x, (pos_y - height));

					   g.drawImage(img, 
							   	   pos_x + width, 
							   	   (pos_y - height), 
							   	   -width, 
							   	   (height * 2), 
							   	   null);  
				   }
				   else{
					   g.drawString(label, pos_x, (pos_y - height));
					   g.drawImage(img, 
						   	   	   pos_x, 
						   	       (pos_y - height), 
						   	       width, 
						   	       (height * 2), 
						   	       null);
				   }
			   }	
			   else{
				   if(direction == Direction.LEFT){
					   g.drawString(label, pos_x, (pos_y - height));
					   g.drawImage(animation.getImage(), 
						   	       pos_x + width, 
						   	       (pos_y - height), 
						   	       -width, 
						   	       (height * 2), 
						   	       null); 
				   }
				   else{
					   g.drawString(label, pos_x, (pos_y - height));
					   g.drawImage(animation.getImage(), 
							   	   pos_x, 
							   	   (pos_y - height), 
							   	   width, 
							   	   (height * 2), 
							   	   null); 
				   }
			   }
		   }
	   }
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
	   if(state != PlayerState.DEAD){
		   
		   interactWithUnwalkables();
		   pos_x += v_x;
		   pos_y += v_y;
		   clip();
	  
		   setAniamtion();
	   }
	}

   
   
   /**
    * This method collects items as the player is moving
    */
   public void collectItems(){
	   Point player_index = map.toIndex(getCenter().x, getCenter().y);
	   for(int i = player_index.x - 1; i <= player_index.x + 1; i++ ){
		   for(int j = player_index.y - 1; j <= player_index.y + 1; j++ ){	  
			   Powerup item = map.items.get(new Point(i,j));
			   if(item != null){
				   if(intersects(item) && item.isVisible()){
					   //upgrade abilities
					   upgrade(item);
					   //add to my_items
					   if(my_items.containsKey(item)){
						   my_items.put(item, my_items.get(item) + 1);
					   }
					   else{
						   my_items.put(item, 1);
					   }

					   //ask the map to remove the item
					   map.removeItems(new Point(i,j));
				   }	
			   }
				
		   }
	   }
   }
   
   /**
    * This method upgrade player's stats based on the absorbed item
    * @param item
    */
   private void upgrade(Powerup item){
	   if (item instanceof Powerup_Speed){
		   if (vel + 1 <= MAX_VEL){
			   vel ++;
		   }
	   }
	   else if (item instanceof Powerup_Explosion){
		   if (range + 1 <= MAX_RANGE){
			   range ++;
		   }
	   }
	   else if (item instanceof Powerup_Bubble){
		   if (num + 1 <= MAX_NUM){
			   num ++;
		   }
	   }
	   else{
		   //TODO for future item additions
	   }
   }
   
   
   /**
    * This method sets the direction of the character
    * @param d
    */
   public void setDir(Direction d){
	   if (d == null) return;
	   direction = d;
		switch (d) {
		case UP:   
			v_y = -vel;
			v_x = 0;
			break;
		case DOWN:  
			v_y = vel;
			v_x = 0;
			break;
		case LEFT: 
			v_x = -vel;
			v_y = 0;
			break;
		case RIGHT: 
			v_x = vel;
			v_y = 0;
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

    */
   public void dropBubble (){
	   if(state != PlayerState.DEAD && !onBubble){
		   if (my_bubbles.size() < num){
			   int i = (int)(getCenter().y / Map.TILE_SIZE);
			   int j = (int)(getCenter().x / Map.TILE_SIZE);
			   if(map.receiveInitBubbles(i, j, this.code, this.range)){
				   onBubble = true;
				   last_bubble = new Bubble (map.grid[i][j].x, 
						   					 map.grid[i][j].y, 
						   					 this.range);
		   		}
		   }

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
		if (onBubble){
			if(!intersects(last_bubble)){
				//turn the bubble into a neutral bubble, unspecified to any
				//single player
				onBubble = false;
				if (!my_bubbles.contains(last_bubble)){
					my_bubbles.add(last_bubble);
					int i = last_bubble.getIndex().x;
					int j = last_bubble.getIndex().y;
					map.changeToNeutralBubbles(i, j, this.code);
					last_bubble = null;
				}
			}
			else{
				
				if(last_bubble.getDuration() <= (int)200*0.9){
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
		if (!my_bubbles.isEmpty()){
			for(int index = 0; index < my_bubbles.size(); index++)
			{
				Bubble b = my_bubbles.get(index);
				b.countdown();
				if(b.getDuration() <= 0 || map.isExploded(b)){
					map.startExplosion(b.getIndex().x,
									   b.getIndex().y, 
									   b.getRange());
					my_bubbles.remove(index);
				}
			}
		}	
		//treat last bubble separately
		if (last_bubble!=null){
			last_bubble.countdown();
			if (last_bubble instanceof Explosion){
				if (last_bubble.getDuration() <=0 ){
					map.endExplosion(last_bubble.getIndex().x,
							last_bubble.getIndex().y, this.range);
					last_bubble = null;
					onBubble = false;
				}
			}
			else{
				if (last_bubble.getDuration() <= 0 || 
					map.isExploded(last_bubble)){
					
					last_bubble = new Explosion (last_bubble.getCenter().x, 
												 last_bubble.getCenter().y, 
									             this.range, this.map);
					map.startExplosion(last_bubble.getIndex().x, 
							last_bubble.getIndex().y, this.range);
				}
			}
		}
		
	
	}
	

	/**
	 * This method paint the existing bubbles.
	 * NOTE: always paint bubbles before paint the character
	 */
	public void paintBubbles(Graphics g){
		
		itr_b = my_bubbles.iterator();
		while (itr_b.hasNext()){
			Bubble bubble_tmp = itr_b.next();
			bubble_tmp.draw(g);
		}
		if(last_bubble != null && !my_bubbles.contains(last_bubble)){
			last_bubble.draw(g);
		}
	}
	
	/**
	 * @return if the player has touched explosion
	 */
	public boolean isExploded(){
		Point p = map.toIndex(getCenter().x, getCenter().y);
		return map.map[p.x][p.y] == Map.EXPLOSION;
	}
	
	/**
	 * this method controls the player's state
	 */
	public void stateControl(){
		if(state == PlayerState.DEAD){
			death_time --;  //count down death time
			if(death_time > DEATH_PEN - 100){
				dead_display  = true;		//display image
			}
			else {
				dead_display = false;
			}
			
			if(death_time <= 0){
				state = PlayerState.RESPAWN;	//respawn
				death_time = DEATH_PEN;         //reset death time
				pos_x = map.getPlayerInitPosition(code).x;	//reset location
				pos_y = map.getPlayerInitPosition(code).y;
				direction = Direction.DOWN;				//reset direction
			}
		}
		else if (state == PlayerState.RESPAWN){
			
			respawn_invulnerable --;			//countdown
			if(respawn_invulnerable % 10 == 0){
				display = !display;				//blink image every 100 milisec
			}
			if(respawn_invulnerable <= 0){
				state = PlayerState.NORMAL;			//normal
				respawn_invulnerable = RESPAWN;     //reset respawn countdown
			}
		}
		else{  // normal state
			if(isExploded()){
				state = PlayerState.DEAD;
				death_times ++;   //count of number death times
			}
		}
	}
	
	public int getDeathTimes(){
		return death_times;
	}
	
	
	public boolean isDead(){
		return state == PlayerState.DEAD;
	}
	
	public PlayerState getState(){
		return this.state;
	}
	
	public void setState(PlayerState state){
		this.state = state;
	}
	
	/**
	 * This method specifies the interaction between any player and unwalkables,
	 * including bubbles, blocks, walls, that are NOT simply unwalkable.
	 */
	public void interactWithUnwalkables(){
		Point index = map.toIndex(getCenter().x, getCenter().y);
		for(int i = map.clip(index.x - 1, 'h'); 
				i <= map.clip(index.x + 1, 'h'); 
				i++ ){
			for(int j = map.clip(index.y - 1, 'w'); 
					j <= map.clip(index.y + 1, 'w'); 
					j++ ){
				
				if(map.unwalkables.get(new Point(i, j)) != null){
					Unwalkable unwalkable = map.unwalkables.get(new Point(i, j));
					if(intersects(unwalkable)){
						//can only move if the player is moving AWAY
						if(!unwalkable.isWalkingAway(this)){
							v_x = 0;
							v_y = 0;  
						}
					}
					else{						
						stop(hitObj(unwalkable));
						}
					}
				}
			}		
		}
}
   
   
   

