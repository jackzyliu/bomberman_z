
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
	
	public static final String WALL_img_file = "Blocks/SolidBlock.png";
	public static final String BLOCK_img_file = "Blocks/ExplodableBlock.png";
	public static final String GROUND_img_file = "Blocks/BackgroundTile.png";
	
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	
	public static final int TILE_SIZE = 40;
	
	
	
	public char[][] grid = new char[HEIGHT][WIDTH];
	
	private static BufferedImage wall_img;
	private static BufferedImage block_img;
	private static BufferedImage ground_img;
	
	public Map(){
		for (int i = 0; i < HEIGHT; i++) {
			for (int j = 0 ; j <WIDTH; j ++) {
				if(i%2==0 && j%2==0){
					grid[i][j] = 'w';
				}
				else{
					grid[i][j] = 'g';
				}
				
			}	
		}
	
		
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
	
	public void paint(Graphics g){
		for (int i = 0 ; i < HEIGHT; i ++){
			for(int j = 0 ; j < WIDTH; j++){
				switch(grid[i][j]){
				case 'w': 
					g.drawImage(wall_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				case 'b':
					g.drawImage(block_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				case 'g':
					g.drawImage(ground_img, j*TILE_SIZE, i*TILE_SIZE, 
							TILE_SIZE, TILE_SIZE, null);
					break;
				}
			}
		}
	}
	
	
	public boolean isBlocked(int x, int y){
		
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
		
		return grid[i][j] == 'w';
		
		
	}
		
}
