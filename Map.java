
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
/**
 * Map/Grid Class
 * @author Zheyuan Liu
 *
 */
public class Map {
	
	public static final char WALL = 'w';
	public static final char BLOCK = 'b';
	public static final char BLOCK_BREAK = 'x';   
											//the block that is about to break
	public static final char GROUND = 'g';
	public static final char BUBBLE = 'o';        //neutral bubble
	public static final char EXPLOSION = 'e';		//explosion area
	public static final char SPEEDUP = 's';			//speedup item
	public static final char RANGEUP = 'r';			//range up item
	public static final char NUMUP = 'n';			//num up item
	
	public static final String WALL_img_file = "Blocks/SolidBlock.png";
	public static final String BLOCK_img_file = "Blocks/ExplodableBlock.png";
	public static final String GROUND_img_file = "Blocks/BackgroundTile.png";
	
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	public static final int TILE_SIZE = 40;
	
	public static final int COURT_WIDTH = 400;
	public static final int COURT_HEIGHT = 400;
	
	public char[][] map = new char[HEIGHT][WIDTH];
	public Point[][] grid = new Point[HEIGHT][WIDTH];
	
	private static BufferedImage wall_img;
	private static BufferedImage block_img;
	private static BufferedImage ground_img;

	
	
	private Deque<Explosion> explosions;	
	private Iterator<Explosion> itr_e;    
	 // the iterator to iterator over explosions

	
	public Hashtable<Point, Powerup> items;
	
	
	
	public Map(){
		//bubbles = new ArrayList<Bubble>();
		explosions = new ArrayDeque<Explosion>();
		//player_initBubbles = new Hashtable<Integer, Bubble>();
		items = new Hashtable<Point, Powerup>();
		
		//allocating points to drop bombs and items
		for (int i = 0 ; i < HEIGHT; i ++){
			for (int j = 0 ; j < WIDTH; j ++){
				int x = TILE_SIZE * j + TILE_SIZE / 2;
				int y = TILE_SIZE * i + TILE_SIZE / 2;
				grid[i][j] = new Point(x, y);
			}
		}
				
				
		//allocating the components
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0 ; j <WIDTH; j ++) {
				
				if(i%2==0 && j%2==0){
					map[i][j] = 'b';
					
				}
				else{
					map[i][j] = GROUND;
				}
				assignItems(i,j);
			}	
		}
		//load image
		try{
			if (wall_img == null) {
				wall_img = ImageIO.read(new File(WALL_img_file));
			}
			if (block_img == null) {
				block_img = ImageIO.read(new File(BLOCK_img_file));
			}
			if (ground_img == null) {
				ground_img = ImageIO.read(new File(GROUND_img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		
		
		
		
	}
	
	
	/**
	 * This method randomly assigns items to blocks 
	 * @param i
	 * @param j
	 */
	private void assignItems(int i, int j){
		if(map[i][j] == 'b'){
			
			int min = 0;
			int max = 9;
			int item_code = 
					min + (int)(Math.random() * ((max - min) + 1));
			//a random number from 0 - 9
			switch(item_code){
			case 1:
				items.put(new Point(i,j), new Powerup_Speed(COURT_WIDTH, 
						COURT_HEIGHT, grid[i][j].x, grid[i][j].y));
				break;
			case 2:
				items.put(new Point(i,j), new Powerup_Explosion(COURT_WIDTH, 
						COURT_HEIGHT, grid[i][j].x, grid[i][j].y));
				break;
			case 3:
				items.put(new Point(i,j), new Powerup_Bubble(COURT_WIDTH, 
						COURT_HEIGHT, grid[i][j].x, grid[i][j].y));
				break;
						
			default:
			}
		}
	}
	/**
	 * This helper method clips the index 
	 * @param num
	 * @param type
	 * @return
	 */
	public int clip(int num, char type){
		if(type == 'w'){
			if(num >= WIDTH){
				return WIDTH - 1;
			}
			else if(num < 0){
				return 0;
			}
			else{
				return num;
			}
		}
		else if(type == 'h'){
			if(num >= HEIGHT){
				return HEIGHT - 1;
			}
			else if(num < 0){
				return 0;
			}
			else{
				return num;
			}
		}
		else return -1;
	}
	
	
	/**
	 * This method paints the map BACKGROUND
	 */
	public void paint(Graphics g){
		for (int i = 0 ; i < HEIGHT; i ++){
			for(int j = 0 ; j < WIDTH; j++){
				switch(map[i][j]){
				case 'w': 
					g.drawImage(wall_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				case 'b':
					g.drawImage(block_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				default:
					g.drawImage(ground_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param player_code
	 * @return whether the player is blocked by movement
	 */
	public boolean isBlocked(int x, int y, int player_code){
		
		int i = clip((int) y/TILE_SIZE, 'h') ;
		int j = clip((int) x/TILE_SIZE, 'w') ;
		
		return (map[i][j] == WALL 
				|| map[i][j] == BUBBLE 
				|| map[i][j] == BLOCK);
		//the tile is not an open floor unit or an explosion or the bubble that
		//the player is already standing on or items
	}
	
	
	
	/**
	 * This method tracks the explosions, including evoking their countdowns
	 * as well as removing them
	 */
	public void trackExplosions(){
	//not using iterator to allow "concurrent modification"
		
		//use iterator to ensure efficiency
		itr_e = explosions.iterator();
		while(itr_e.hasNext()){
			Explosion e = itr_e.next();
			e.countdown();
		}
			
		if(!explosions.isEmpty()){
			Explosion first = explosions.getFirst();
			if (first.getDuration() <=0 ){
				endExplosion(first.getIndex().x, first.getIndex().y, first.getRange());
				explosions.removeFirst();
			}
		}

	}
	
	/**
	 * This method receives the last bubble any player drops and store the 
	 * position in terms of the player's code so that the player can have
	 * a special interaction with it.
	 * @param i
	 * @param j
	 * @param player's code(the player that drops the bomb)  
	 * @return
	 */
	public boolean receiveInitBubbles(int i, int j, int player_code, int range){
		if (map[i][j] != GROUND){
			return false;
		}
		else{
			map[i][j] = (char)player_code;
			return true;
		}
	}
	

	/**
	 * change the bubbles to neutral bubbles that the player cannot traverse
	 * @param i
	 * @param j
	 * @param player_code
	 */
	public void changeToNeutralBubbles(int i, int j, int player_code){
		if (map[i][j] == (char)player_code){
			map[i][j] = BUBBLE;
			//bubbles.add(player_initBubbles.get(player_code));
			//player_initBubbles.remove(player_code);
		}
	}
	
	
	/**
	 * This helper method helps determines if a bubble is exploded
	 * @param bubble
	 * @return
	 */
	public boolean isExploded(Bubble bubble){
		int i = clip((int)bubble.getCenter().y/TILE_SIZE, 'h');
		int j = clip((int)bubble.getCenter().x/TILE_SIZE, 'w');
		
		return map[i][j] == EXPLOSION;
	}
	
	/**
	 * start explosions by turning the map index to "e"
	 * @param i
	 * @param j
	 * @param range
	 */
	public void startExplosion(int i, int j, int range){
		for (int h = i ; h >= clip(i - range, 'h') ; h--){
			if(map[h][j] != WALL){
				char tile = map[h][j];
				if(tile == BLOCK){   // explosions do not go through blocks
					map[h][j] = BLOCK_BREAK;
					break;
				}
				else{
					map[h][j] = EXPLOSION;
				}
			}
			
		}
		
		for (int h = i ; h <= clip(i + range, 'h') ; h++){
			if(map[h][j] != WALL){
				char tile = map[h][j];
				if(tile == BLOCK){ // explosions do not go through blocks
					map[h][j] = BLOCK_BREAK;
					break;
				}
				else{
					map[h][j] = EXPLOSION;
				}
			}
		}
		
		for (int w = j ; w >= clip(j - range, 'w') ; w--){
			if(map[i][w] != WALL){
				char tile = map[i][w];
				if(tile == BLOCK){   // explosions do not go through blocks
					map[i][w] = BLOCK_BREAK;
					break;
				}
				else{
					map[i][w] = EXPLOSION;
				}
			}
		}
		
		for (int w = j ; w <= clip(j + range, 'w') ; w++){
			if(map[i][w] != WALL){
				char tile = map[i][w];
				if(tile == BLOCK){   // explosions do not go through blocks
					map[i][w] = BLOCK_BREAK;
					break;
				}
				else{
					map[i][w] = EXPLOSION;
				}
			}
		}
		
		explosions.add(new Explosion
				(Map.COURT_WIDTH, Map.COURT_HEIGHT, grid[i][j].x, grid[i][j].y, 
						range, this));
	}
	

	
	/**
	 * end explosions by turning the "e"s back to "g"'s
	 * NOTE: if the block is supposed to be an item, make the tile an item 
	 * instead, and remove the item from the hashtable "items"
	 * @param i
	 * @param j
	 * @param range
	 */
	public void endExplosion(int i, int j, int range){
		for (int h = clip(i - range, 'h'); h <= clip(i + range, 'h') ; h++){
			postExplosion(h,j);
		}
		for (int w = clip(j - range, 'w'); w <= clip(j + range, 'w') ; w++){
			postExplosion(i,w);
		}
	}
	
	/**
	 * This helper method specifies all the actions after any explosion,
	 * including exposing items, removing items, and removing blocks
	 * @param i
	 * @param j
	 */
	private void postExplosion(int i, int j){
		
		if(map[i][j] == EXPLOSION || map[i][j] == BLOCK_BREAK){
			if(items.containsKey(new Point(i,j))){
				Point p = new Point(i,j);
				Powerup item = items.get(p);
				if(!item.isVisible()){
					if(item instanceof Powerup_Speed){
						map[i][j] = SPEEDUP;	
					}
					else if(item instanceof Powerup_Explosion){
						map[i][j] = RANGEUP;
					}
					else if(item instanceof Powerup_Bubble){
						map[i][j] = NUMUP;
					}
					item.turnVisible();
				}
				else{
					items.remove(new Point(i,j));
				}
			}
			else{
				map[i][j] = GROUND;
			}
		}
	}
		
	
	
	
	/**
	 * draws the area effect of explosions on the MAP as well as the items
	 * @param g
	 */
	public void drawAreaExplosionAndItems(Graphics g){
		
		//draw items
		Enumeration<Point> item_locations = items.keys();
		while(item_locations.hasMoreElements()){
			   Point p = item_locations.nextElement();
			   Powerup item = items.get(p);
			   if(item != null && item.isVisible()){;
				  	item.draw(g);
			   }
		}

		itr_e = explosions.iterator();
		while (itr_e.hasNext()){
			Explosion explosion_tmp = itr_e.next();
			explosion_tmp.draw(g);
		}
		
	}
	
	public void removeItems(Point p){
		if (map[p.x][p.y] == SPEEDUP ||
			map[p.x][p.y] == RANGEUP ||
			map[p.x][p.y] == NUMUP){
			
			items.remove(p);
			map[p.x][p.y] = GROUND;
		}
	}
	

}
