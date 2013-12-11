import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This is the general info menu.
 * @author Zheyuan Liu
 *
 */
public class GameGeneral extends GameHelp {
	public static final String file = "Menu/General_info.jpg";
	private BufferedImage img;
	public GameGeneral(){
		super();
		try{
			img = ImageIO.read(new File(file));
		} catch (IOException e){
			e.getStackTrace();
		}
	}
	
	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(img, 0, 0, GameMenu.MENU_WIDTH, GameMenu.MENU_HEIGHT, null);
	}
	
}
