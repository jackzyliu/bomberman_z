
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class is the explosion object, which inherits Bubble
 * @author Zheyuan Liu
 *
 */
public class Explosion extends Bubble {
	
	private int duration = 30;

	private int range;
	private Map map;

	private BufferedImage[] imgs;
	private Animation animation;
	
	public Explosion (int center_x, int center_y, int range, Map map){
		
		super(center_x, center_y, range);
		try{
			imgs = new BufferedImage[5];
			for(int i = 0; i < imgs.length; i ++){
				String img_file = "resource/image/Flame/Flame_f0" + Integer.toString(i) + ".png";
				imgs[i] = ImageIO.read(new File(img_file));
			}
		}catch(IOException e){
			System.out.println("Internal Error:" + e.getMessage());
		}
	
		this.range = range;
		this.map = map;
		this.animation = new Animation();
		animation.setFrames(imgs);
		animation.setDelay(100);
		
	}
	
	@Override
	public void draw(Graphics g){
		int img_x = pos_x + (OBJ_WIDTH - IMG_WIDTH) / 2;
		int img_y = pos_y + (OBJ_HEIGHT - IMG_HEIGHT) / 2;
		
		int i = (int)img_y / Map.TILE_SIZE;
		int j = (int)img_x / Map.TILE_SIZE;
		
		for (int h = i ; h >= map.clip(i - range, 'h') ; h--){
			if(!map.isExplodable(h, j)){
				// explosions do not TOUCH WALL;
				break;
			}
			
			char tile = map.map[h][j];
			
			int img_y_tmp = img_y - ((i - h) * Map.TILE_SIZE);
			g.drawImage(animation.getImage(), 
					img_x, img_y_tmp, IMG_WIDTH, IMG_HEIGHT, null);
			
			if(tile == Map.BLOCK || tile == Map.BLOCK_BREAK){   
				// explosions do not go through blocks
				break;
			}
		}
		for (int h = i ; h <= map.clip(i + range, 'h') ; h++){
			
			if(!map.isExplodable(h, j)){
				// explosions do not TOUCH WALL;
				break;
			}
			
			char tile = map.map[h][j];
			
			int img_y_tmp = img_y - ((i - h) * Map.TILE_SIZE);
			g.drawImage(animation.getImage(), 
					img_x, img_y_tmp, IMG_WIDTH, IMG_HEIGHT, null);
			
			if(tile == Map.BLOCK || tile == Map.BLOCK_BREAK){   
				// explosions do not go through blocks
				break;
			}
		}
		
		for (int w = j ; w >= map.clip(j - range, 'w') ; w--){
			if(!map.isExplodable(i, w)){
				// explosions do not TOUCH WALL;
				break;
			}
			
			char tile = map.map[i][w];
			
			int img_x_tmp = img_x - ((j - w) * Map.TILE_SIZE);
			g.drawImage(animation.getImage(), 
					img_x_tmp, img_y, IMG_WIDTH, IMG_HEIGHT, null);
			
			if(tile == Map.BLOCK || tile == Map.BLOCK_BREAK){ 	
				// explosions do not go through blocks
				break;
			}
		}
		
		for (int w = j ; w <= map.clip(j + range, 'w') ; w++){
			if(!map.isExplodable(i, w)){
				// explosions do not TOUCH WALL;
				break;
			}
			
			char tile = map.map[i][w];
			
			int img_x_tmp = img_x - ((j - w) * Map.TILE_SIZE);
			g.drawImage(animation.getImage(), 
					img_x_tmp, img_y, IMG_WIDTH, IMG_HEIGHT, null);
			
			if(tile == Map.BLOCK || tile == Map.BLOCK_BREAK){ 	
				// explosions do not go through blocks
				break;
			}
		}
		
	}
	
	@Override
	public void countdown(){
		this.duration -= 1;
		animation.update();
	}
	
	@Override
	public int getDuration(){
		return this.duration;
	}
	

}
