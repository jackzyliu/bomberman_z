
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	public static final char TREE = 't';
	public static final char BUBBLE = 'o';        //neutral bubble
	public static final char EXPLOSION = 'e';		//explosion area
	public static final char SPEEDUP = 's';			//speedup item
	public static final char RANGEUP = 'r';			//range up item
	public static final char NUMUP = 'n';			//num up item
	
	public static final String WALL_img_file = "Blocks/SolidBlock.png";
	public static final String TREE_img_file = "Blocks/tree.png";
	public static final String BLOCK_img_file = "Blocks/ExplodableBlock.png";
	public static final String GROUND_img_file = "Blocks/BackgroundTile.png";
	
	public static final int WIDTH = 12;
	public static final int HEIGHT = 12;
	public static final int TILE_SIZE = 40;
	
	public static final int COURT_WIDTH = 480;
	public static final int COURT_HEIGHT = 480;
	
	public static final int PLAYER1_INIT_X = 2*TILE_SIZE;
	public static final int PLAYER1_INIT_Y = 2*TILE_SIZE;
	public static final int PLAYER2_INIT_X = COURT_WIDTH - 3*TILE_SIZE;
	public static final int PLAYER2_INIT_Y = COURT_HEIGHT - 3*TILE_SIZE;
	
	public char[][] map = new char[HEIGHT][WIDTH];
	public Point[][] grid = new Point[HEIGHT][WIDTH];
	
	private static BufferedImage wall_img;
	private static BufferedImage tree_img;
	private static BufferedImage block_img;
	private static BufferedImage ground_img;

	public Deque<Explosion> explosions;	
	private Iterator<Explosion> itr_e;    
	 // the iterator to iterator over explosions
	private Hashtable<Point, Unwalkable> unwalkables;    
	//store and update unwalkables
	public Hashtable<Point, Powerup> items;
	//store and update walkables
	
	//Note: the POINTS are indices in arrays
	
	
	@SuppressWarnings("unused")
	private String map_file;
	private BufferedReader br;
	
	public Map(String map_file) throws IOException{
		
		validateInputMap(map_file);
		//bubbles = new ArrayList<Bubble>();
		explosions = new ArrayDeque<Explosion>();
		//player_initBubbles = new Hashtable<Integer, Bubble>();
		items = new Hashtable<Point, Powerup>();
		unwalkables = new Hashtable<Point, Unwalkable>();
		
		
		//allocating points to drop bombs and items
		for (int i = 0 ; i < HEIGHT; i ++){
			for (int j = 0 ; j < WIDTH; j ++){
				int x = TILE_SIZE * j + TILE_SIZE / 2;
				int y = TILE_SIZE * i + TILE_SIZE / 2;
				grid[i][j] = new Point(x, y);
			}
		}
				
				
		//allocating the components
		
		this.map_file = map_file;
		try{
			if(br == null){
				br = new BufferedReader(new FileReader(map_file));
			}
			int i = -1;
			String line = br.readLine();
			while(line != null){
				i++;
				String[] tiles = line.split(",");
				for(int j = 0; j < WIDTH; j++){
					map[i][j] = tiles[j].charAt(0);
					addMapParam(i,j);
					assignItems(i,j);
				}
				line = br.readLine();
			}
			
		} catch (IOException e){
			System.out.println("Internal Error: " + e.getMessage());
		} finally{
			br.close();
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
			if (tree_img == null){
				tree_img = ImageIO.read(new File(TREE_img_file));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}

	}
	
	/**
	 * This method helps validate the input map_file
	 * @param map_file
	 * @throws IOException
	 */
	private void validateInputMap(String map_file) throws IOException{
		if(map_file == null) throw new IllegalArgumentException();
	
		//local reader
		BufferedReader br = new BufferedReader(new FileReader(map_file));
		String line = br.readLine();
		int row = 0;
		while(line != null){
			row++;
			String[] tile = line.split(",");
			if(tile.length  != WIDTH){
				br.close();
				throw new IllegalArgumentException();
			}
			line = br.readLine();
		}
		if(row != HEIGHT){
			br.close();
			throw new IllegalArgumentException();
		}
		else{
			br.close();
		}

	}
	/**
	 * This helper method adds new unwalkable tiles 
	 * @param i
	 * @param j
	 */
	private void addMapParam(int i, int j){
		if (map[i][j] == WALL || map[i][j] == BLOCK || map[i][j] == TREE){
			unwalkables.put(new Point (i, j), new Unwalkable (i, j));
		}
		
	}
	
	/**
	 * This method randomly assigns items to blocks 
	 * @param i
	 * @param j
	 */
	private void assignItems(int i, int j){
		if(map[i][j] == BLOCK){
			
			int min = 0;
			int max = 5;
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
	 * This method returns a player's initial position based on their "code"
	 * @param player_code
	 * @return
	 */
	public Point getPlayerInitPosition(int player_code){
		if(player_code == 1){
			return new Point (PLAYER1_INIT_X, PLAYER1_INIT_Y);
		}
		else if(player_code == 2){
			return new Point (PLAYER2_INIT_Y, PLAYER2_INIT_Y);
		}
		else return null;
	}
	/**
	 * This method paints the map BACKGROUND
	 */
	public void paint(Graphics g){
		for (int i = 0 ; i < HEIGHT; i ++){
			for(int j = 0 ; j < WIDTH; j++){
				switch(map[i][j]){
				case WALL: 
					g.drawImage(wall_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				case BLOCK:
					g.drawImage(block_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				default:
					g.drawImage(ground_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				
				}
				//paint tree ON TOP OF GROUND
				if(map[i][j] == TREE){
					g.drawImage(tree_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
				
				}
			}
		}
	}
	
	/**
	 * This method takes in any single pair of coordinate and convert it to the 
	 * indices of the map array.
	 * @param x
	 * @param y
	 * @return
	 */
	public Point toIndex(int x, int y){
		int i = clip((int) y/TILE_SIZE, 'h') ;
		int j = clip((int) x/TILE_SIZE, 'w') ;
		
		return new Point(i,j);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param player_code
	 * @return whether the player is blocked by movement
	 */
	public boolean isBlocked(int x, int y, int player_code){
		
		int i = toIndex(x,y).x;
		int j = toIndex(x,y).y;
		
		return (map[i][j] == WALL 
				|| map[i][j] == BUBBLE 
				|| map[i][j] == BLOCK) 
				&& map[i][j] != (char)player_code;
		//the tile is not an open floor unit or an explosion or the bubble that
		//the player is already standing on or items
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
			unwalkables.put(new Point (i,j), new Unwalkable(i,j));
		}
	}
	
	
	/**
	 * This helper method helps determines if a bubble is exploded
	 * @param bubble
	 * @return
	 */
	public boolean isExploded(Bubble bubble){
		int i = toIndex(bubble.getCenter().x, bubble.getCenter().y).x;
		int j = toIndex(bubble.getCenter().x, bubble.getCenter().y).y;
		
		return map[i][j] == EXPLOSION;
	}
	
	/**
	 * start explosions by turning the map index to "e"
	 * @param i
	 * @param j
	 * @param range
	 */
	public void startExplosion(int i, int j, int range){
		unwalkables.remove(new Point (i,j));
		
		for (int h = i ; h >= clip(i - range, 'h') ; h--){
			if(!isExplodable(h, j)){
				break;
			}
			else{
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
			if(!isExplodable(h, j)){
				break;
			}
			else{
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
			if(!isExplodable(i, w)){
				break;
			}
			else{
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
			if(!isExplodable(i, w)){
				break;
			}
			else{
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
				( grid[i][j].x, grid[i][j].y, range, this));
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
			unwalkables.remove(new Point (i,j));
		}
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
	
	
	public boolean isExplodable(int i, int j){
		return (map[i][j] != WALL && map [i][j] != TREE); 
	}
	/**
	 * This method removes the item from the item list.
	 * @param Point of the item
	 */
	public void removeItems(Point p){
			items.remove(p);
			map[p.x][p.y] = GROUND;
		
	}
	
	/**
	 * This method specifies the interaction between any player and unwalkables,
	 * including bubbles, blocks, walls, that are NOT simply unwalkable.
	 * @param player
	 */
	public void interactWithUnwalkables(Player player){
		Point player_index = toIndex(player.getCenter().x, player.getCenter().y);
		for(int i = player_index.x - 1; i <= player_index.x + 1; i++ ){
			for(int j = player_index.y - 1; j <= player_index.y + 1; j++ ){
				if(unwalkables.get(new Point(i, j)) != null){
					Unwalkable unwalkable = unwalkables.get(new Point(i, j));
					if(player.intersects(unwalkable)){
						//can only move if the player is moving AWAY
						if(!unwalkable.isWalkingAway(player)){
							player.v_x = 0;
							player.v_y = 0;  
						}
					}
					else{
						player.stop(player.hitObj(unwalkable));
					}
				}
			}		
		}
	}

}
