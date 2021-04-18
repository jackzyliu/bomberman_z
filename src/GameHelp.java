import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * This class is the help menu.
 * @author Zheyuan Liu
 *
 */
@SuppressWarnings("serial")
public class GameHelp extends JPanel{
	public static final String file = "resource/image/Menu/Help.jpg";
	public static final int BUTTON_X = 0;
	public static final int BUTTON_Y = 0;
	public static final int BUTTON_WIDTH = 120;
	public static final int BUTTON_HEIGHT = 30;
	
	private BufferedImage img;
	
	public GameHelp(){
		
		try{
			img = ImageIO.read(new File(file));
		} catch (IOException e){
			e.getStackTrace();
		}
		
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int m_x = e.getX();
				int m_y = e.getY();
				
				if(m_x > BUTTON_X && m_x < BUTTON_X + BUTTON_WIDTH &&
				   m_y > BUTTON_Y && m_y < BUTTON_Y + BUTTON_HEIGHT){
					Game.setState(GameState.MENU);
					setVisible(false);
					try {
						Game.switchPane(false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
	}
	
	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(img, 0, 0, GameMenu.MENU_WIDTH, GameMenu.MENU_HEIGHT, null);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(GameMenu.MENU_WIDTH,  GameMenu.MENU_HEIGHT);
	}
	
	
	
	
}
