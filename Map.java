
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")
/**
 * Map/Grid Class
 * @author Zheyuan Liu
 *
 */
public class Map extends JPanel {
	
	public static final char WALL = 'w';
	public static final char BLOCK = 'b';
	public static final char GROUND = 'g';
	public static final char BUBBLE = 'o';        //neutral bubble
	public static final char EXPLOSION = 'e';		//explosion area
	
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
	
	
	public Map(){
		//allocating the components
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0 ; j <WIDTH; j ++) {
				if(i%2==0 && j%2==0){
					map[i][j] = 'w';
				}
				else{
					map[i][j] = 'g';
				}
				
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
		
		//allocating points to drop bombs
		for (int i = 0 ; i < 10; i ++){
			for (int j = 0 ; j < 10; j ++){
				int x = TILE_SIZE * j + TILE_SIZE /2;
				int y = TILE_SIZE * i + TILE_SIZE/2;
				grid[i][j] = new Point(x, y);
			}
		}
		
		
	}
	
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
	
	
	public boolean isBlocked(int x, int y, int player_code){
		
		int i = (int) y/TILE_SIZE ;
		//System.out.println(i);
		if (i < 0){
			i = 0;
		}
		if (i > 9){
			i = 9;
		}
		
		int j = (int) x/TILE_SIZE ;
		
		if (j < 0){
			j = 0;
		}
		if (j > 9){
			j = 9;
		}
		
		return (map[i][j] != 'g' && map[i][j] != 'e' 
					&& map[i][j] != (char)player_code);
		//the tile is not an open floor unit
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
	public boolean receiveInitBubbles(int i, int j, int player_code){
		if (map[i][j] != 'g'){
			return false;
		}
		else{
			map[i][j] = (char)player_code;
			return true;
		}
	}
	
	public void changeToNeutralBubbles(int i, int j, int player_code){
		if (map[i][j] == (char)player_code){
			map[i][j] = 'o';
		}
	}
	
	public void startExplosion(int i, int j){
		map[i][j] = 'e';
	}
	
	public void endExplosion(int i, int j){
		map[i][j] = 'g';
	}
		
}
