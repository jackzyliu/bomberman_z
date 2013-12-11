
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;




/**
 * This class defines the behaviors of AI creeps. (An AI is a subclass of Player
 * since it has all the same interactions with tiles, bubbles, and explosions)
 * 1. An AI roams around pointlessly at a slow speed
 * 2. It turns HUNGRY (faster speed) once it senses player around it
 * 3. It follows the player until it dies or does not sense the player anymore
 * 4. It dodges bubbles and has minimum efforts calculating explosions.
 * @author Zheyuan Liu
 *
 */
public class Creep_AI extends Player{
	
	public static final String dead_img = "Character/dead_player.gif";

	private enum CreepState {
		NORMAL,
		ANGRY,
		DEAD;
	}
	
	private HashSet<Direction> directions;
	private CreepState state;
	private int detect_rng;
	private Player player;
	private PathFinder path_finder;
	private Path path;
	
	private BufferedImage[] front;
	private BufferedImage[] back;
	private BufferedImage[] right;
	private BufferedImage dead;
	
	public Creep_AI(Map map, Player player, int code) {
		super(map, code);
		try{
			String img_file;
			
			front = new BufferedImage[6];
			back = new BufferedImage[5];
			right = new BufferedImage[7];
			
			for (int i = 0 ; i < front.length; i ++){
				img_file = 
						"Creep/Front/Creep_F_f0"+Integer.toString(i)+".png";
				front[i] = ImageIO.read(new File(img_file));
			}
			
			for (int i = 0 ; i < back.length; i ++){
				img_file = 
						"Creep/Back/Creep_B_f0"+Integer.toString(i)+".png";
				back[i] = ImageIO.read(new File(img_file));
			}
			for (int i = 0 ; i < right.length; i ++){
				img_file = 
						"Creep/Side/Creep_S_f0"+Integer.toString(i)+".png";
				right[i] = ImageIO.read(new File(img_file));
			}
			
			dead = ImageIO.read(new File(dead_img));
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		
		state = CreepState.NORMAL;
		detect_rng = 2;
		this.directions = new HashSet<Direction>();
		this.directions.add(Direction.UP);
		this.directions.add(Direction.DOWN);
		this.directions.add(Direction.RIGHT);
		this.directions.add(Direction.LEFT);
		setDir(chooseRandDir(directions));
		this.code = code;
		this.player = player;
		this.path_finder = new PathFinder(map);
		this.path = new Path();
		
	}
	@Override
	public void draw(Graphics g){

	   if(state == CreepState.DEAD){
		   if(dead_display){ //if display toggle is on
			   g.drawImage(dead, 
					   	   pos_x, 
					   	   (pos_y - height), 
					   	   width, 
					   	   (height * 2), 
					   	   null); 
		   }
	   }
	   //not dead
	   else{
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
				   g.drawImage(img, 
						   	   pos_x + width, 
						   	   (pos_y - height), 
						   	   -width, 
						   	   (height * 2), 
						   	   null);  
			   }
			   else{
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
				   g.drawImage(animation.getImage(), 
					   	       pos_x + width, 
					   	       (pos_y - height), 
					   	       -width, 
					   	       (height * 2), 
					   	       null); 
			   }
			   else{
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
	
	@Override
		/**
		 * Moves the object by its velocity.  Ensures that the object does
		 * not go outside its bounds by clipping.
		 */
	public void move(){
		if(state != CreepState.DEAD){
			interactWithUnwalkables();
			if(state == CreepState.ANGRY){
				followPath();
			}
			else{
				navRandom();  // defualt navigation mode --- random
			}
			
			pos_x += v_x;
			pos_y += v_y ;
			clip();
		  
			setAniamtion();
			
		}
	}
	 
	 @Override
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
	 	

	
	private Point getNextTile(Direction d){
		Point p = map.toIndex(getCenter().x, getCenter().y);
		int i = p.x;
		int j = p.y;
		
		switch(d){
		case UP: i -= 1; break;
		case DOWN: i += 1 ; break; 
		case RIGHT: j += 1 ; break; 
		case LEFT: j -= 1; break;
		}
		return new Point(i,j);
	}
	
	/**
	 * This method tests if the direction d is blocked
	 * @param d
	 * @return
	 */
	public boolean isBlocked(Direction d){
		
		return map.unwalkables.get(new Point(getNextTile(d).x, getNextTile(d).y)) != null;
	}
	
	/**
	 * This method  is trying to a set
	 * of unblocked directions from the other 3 directions
	 * @param d
	 * @return
	 */
	public Set<Direction> unblockedDirections(){
		HashSet<Direction> unblocked_dir = new HashSet<Direction>();
		for(Direction d : this.directions){
			if(!isBlocked(d)) unblocked_dir.add(d);
		}
		return unblocked_dir;
	}
	
	
	public void navRandom(){
		if(passedCenterOfTile()){
			if(isBlocked(direction)){
				//randomly generate a walkable direction
				Set<Direction> unblocked = unblockedDirections();
				if(unblocked.isEmpty()) {
					setDir(reverseDir
							(hitObj(map.unwalkables.get
									(new Point(getNextTile(direction).x, 
											   getNextTile(direction).y)))));

					return;
				}
				else setDir(chooseRandDir(unblockedDirections()));
			}	
		}

	}
	
	@Override
	public void interactWithUnwalkables(){
		Point index = map.toIndex(getCenter().x, getCenter().y);
		for(int i = map.clip(index.x - 1, 'h'); 
				i <= map.clip(index.x + 1, 'h'); i++ ){
			for(int j = map.clip(index.y - 1, 'w'); 
					j <= map.clip(index.y + 1, 'w'); j++ ){
				if(map.unwalkables.get(new Point(i, j)) != null){
					Unwalkable unwalkable = map.unwalkables.get(new Point(i, j));
					if(intersects(unwalkable)){
						//can only move if the player is moving AWAY
						if(!unwalkable.isWalkingAway(this)){
							setDir(inQuadrant(unwalkable));
						}
					}
					else{
						setDir(reverseDir(hitObj(unwalkable)));
					}
				}
			}
		}		
	}
	/**			
	 * This method returns a direction based on the quadrant of the unwalkables
	 * the creep is in.
	 *  
	 * Quadrant of a tile/unwalkable
	 * 		  UP
	 *       --------
	 *      |\ 	   / |
	 *      |  \_/   |
	 *  Left|  / \   | Right
	 *      |/     \ |
	 *       --------
	 *       Down
	 * @param unwalkable
	 */
	public Direction inQuadrant(Unwalkable unwalkable){
		Point unwalkable_center = unwalkable.getCenter();
		Point creep_center = getCenter();
		double dx = creep_center.x - unwalkable_center.x;
		double dy = unwalkable_center.y - creep_center.y;
		double theda_radian = Math.atan2(dy, dx);
		if(dy >= 0){ //creep is on the UPPER half of the tile
			if(theda_radian >= 0 && theda_radian <= Math.PI/4 ){
				return Direction.RIGHT;
			}
			else if(theda_radian <= Math.PI/4 * 3 && theda_radian > Math.PI/4){
				return Direction.UP;
			}
			else{
				return Direction.LEFT;
			}
		}
		else{
			if(theda_radian < 0  && theda_radian >= -Math.PI/4){
				return Direction.RIGHT;
			}
			else if(theda_radian >= -Math.PI/4 * 3 && theda_radian < -Math.PI/4){
				return Direction.DOWN;
			}
			else{
				return Direction.LEFT;
			}
		}
	}
	
	/**
	 * This method manages the state of the creep and makes transitions among 
	 * Normal, ANGRY, and DEAD.
	 */
	@Override
	public void stateControl(){
		if(state == CreepState.DEAD){
			
		}
		else if (state == CreepState.ANGRY){
			vel = 2;
			setDir(direction); //reset velocity
			killPlayer();
			if(this.isExploded()){
				state = CreepState.DEAD;
			}
			else if(!enemyWithinRange()|| 
					 map.isBlocked(this.getNextTile(this.direction).x, 
							       this.getNextTile(this.direction).y, 
				                   this) ){
				state = CreepState.NORMAL;
			}
		}
		else if(state == CreepState.NORMAL){
			vel = 1;
			setDir(direction);   //reset veloocity
			killPlayer();
			if(this.isExploded()){
				state = CreepState.DEAD;
			}
			else if(this.enemyWithinRange() && 
					!map.isBlocked(this.getNextTile(this.direction).x, 
							       this.getNextTile(this.direction).y, 
				                   this) ){
				state = CreepState.ANGRY;
			}
		}
	}
	
	/**
	 * This method chooses a random direction among a set of available directions
	 * @param directions
	 * @return
	 */
	public Direction chooseRandDir(Set<Direction> directions){
		if (directions.isEmpty()) return null;
		int n = 0;
		int random = new Random().nextInt(directions.size());
		for(Direction d : directions){
			if(n == random){
				return d;
			}
			n++;
		}
		return null;
	}
	
	/**
	 * Determines if the enemy is within range
	 * @return
	 */
	private boolean enemyWithinRange(){
		Point p = map.toIndex(getCenter().x, getCenter().y);
		Point player_coord = map.toIndex(player.getCenter().x, 
										 player.getCenter().y);
		int i = player_coord.x;
		int j = player_coord.y;
		
		if (i >= map.clip(p.x - detect_rng, 'h') && 
		    i <= map.clip(p.x + detect_rng, 'h') &&
		    j >= map.clip(p.y - detect_rng, 'w') &&
	        j <= map.clip(p.y + detect_rng, 'w')){
			
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	public boolean isDead(){
		return state == CreepState.DEAD;
	}
	
	/**
	 * This method simply set the players state to DEAD if this obj hits player
	 */
	public void killPlayer(){
		if(intersects(player)){
			player.setState(PlayerState.DEAD);
		}
	}
	
	/**
	 * This method returns a path to player that a creep can follow
	 * @param player_x
	 * @param player_y
	 * @return
	 */
	public void setPathToPlayer(){
		Point player_coord = map.toIndex(player.getCenter().x, 
				 player.getCenter().y);
		int t_i = player_coord.x;
		int t_j = player_coord.y;
		//only re-find path if the player is not reachable with this current
		//path
		if(path != null){
			if(!path.isOnPath(player_coord) ){
				this.path=path_finder.findPath(this, t_i, t_j);
			}
		}
		else{
			this.path=path_finder.findPath(this, t_i, t_j);
		}
	}
	
	/**
	 * This method follows the path to player
	 */
	public void followPath(){
		setPathToPlayer();
		if(this.path!= null){
			setDir(getNextDirection());
		}
	}
	
	public Direction getNextDirection(){
		Point current = map.toIndex(getCenter().x, getCenter().y);
		if(this.passedCenterOfTile()){
			if(this.path.hasNext(current)){

				Point next = this.path.getNext(current);

				if(next.x < current.x){
					return Direction.UP;
				}
				else if(next.x > current.x){
					return Direction.DOWN;
				}
				else if (next.y < current.y){
					return Direction.LEFT;
				}
				else if (next.y > current.y){
					return Direction.RIGHT;
				}
				else return null;
				
			}
			else return null;
		}
		else return null;
	}
	/**
	 * This method takes in a moving obj's center x, y and its moving direction
	 * to determine if it has passed the middle point of a tile.
	 * @return
	 */
	public boolean passedCenterOfTile(){
		Point p = map.toIndex(getCenter().x, getCenter().y);
		switch(direction){
		case UP: return getCenter().y <= map.grid[p.x][p.y].y;
		case DOWN: return getCenter().y >= map.grid[p.x][p.y].y;
		case LEFT: return getCenter().x <= map.grid[p.x][p.y].x;
		case RIGHT: return getCenter().x >= map.grid[p.x][p.y].x;
		default: return false;
		}
	}
	
}
