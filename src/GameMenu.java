
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class GameMenu extends JPanel {
	
	public static final String img_file = "Menu/bomberman.jpg";
	public static final String one_player_normal = "Menu/One_Player_Normal.png";
	public static final String two_player_normal = "Menu/Two_Players_Normal.png";
	public static final String one_player_hover = "Menu/One_Player_Hover.png";
	public static final String two_player_hover = "Menu/Two_Players_Hover.png";
	
	public static final int MENU_WIDTH = Map.COURT_WIDTH + GameDashboard.WIDTH;
	public static final int MENU_HEIGHT = Map.COURT_HEIGHT;
	
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 50;
	
	public static final int BUTTON_INIT_X = (MENU_WIDTH-BUTTON_WIDTH)/2;
	
	public static final int ONEP_INIT_Y = (MENU_HEIGHT-BUTTON_HEIGHT)/2;
	public static final int TWOP_INIT_Y =  (MENU_HEIGHT-BUTTON_HEIGHT)/2 + 
											BUTTON_HEIGHT;
	public static final int HELP_INIT_Y = (MENU_HEIGHT-BUTTON_HEIGHT)/2 +  
											2*BUTTON_HEIGHT;
	
	private boolean mouse_on_button1;
	private boolean mouse_on_button2;
	private boolean mouse_on_help;
	
	
	private BufferedImage background_img;
	private BufferedImage button1_normal;
	private BufferedImage button2_normal;
	private BufferedImage button1_hover;
	private BufferedImage button2_hover;
	
	public GameMenu(){
		
		try{
			if(background_img == null){
				background_img = ImageIO.read(new File(img_file));
			}
			if(button1_normal == null){
				button1_normal = ImageIO.read(new File(one_player_normal));
			}
			if(button2_normal == null){
				button2_normal = ImageIO.read(new File(two_player_normal));
			}
			if(button1_hover == null){
				button1_hover = ImageIO.read(new File(one_player_hover));
			}
			if(button2_hover == null){
				button2_hover = ImageIO.read(new File(two_player_hover));
			}
		} catch (IOException e){
			System.out.println("Internal Error" + e.getMessage());
		}
		
		addMouseListener(new MouseAdapter(){
			
			
			private int buttonPressed(MouseEvent e){
				int m_x = e.getX();
				int m_y = e.getY();
				if (m_x > BUTTON_INIT_X
						&& m_x < (BUTTON_INIT_X + BUTTON_WIDTH)){
						//a button is pressed
					if(m_y > ONEP_INIT_Y
							&& m_y < (ONEP_INIT_Y + BUTTON_HEIGHT)){
						return 1;
					}
					else if(m_y > TWOP_INIT_Y 
							&& m_y < (TWOP_INIT_Y  + BUTTON_HEIGHT)){
						return 2;
					}
					else if(m_y > HELP_INIT_Y 
							&& m_y < (HELP_INIT_Y  + BUTTON_HEIGHT)){
						return 3;
					}
				}
				return 0;
			}
				
			public void mousePressed(MouseEvent e) {
				
				switch(buttonPressed(e)){
				case 1:
					mouse_on_button1 = true;
					mouse_on_button2 = false;
					mouse_on_help = false;
					repaint();
					break;
				case 2:
					mouse_on_button1 = false;
					mouse_on_button2 = true;
					mouse_on_help = false;
					repaint();
					break;
				case 3:
					mouse_on_button1 = false;
					mouse_on_button2 = false;
					mouse_on_help = true;
					repaint();
					break;
				default:
					mouse_on_button1 = false;
					mouse_on_button2 = false;
					mouse_on_help = false;
					repaint();
					break;
				}
				
			}
			/**
			 * To allow the player to "regret"
			 */
			public void mouseReleased(MouseEvent e) {
				mouse_on_button1 = false;
				mouse_on_button2 = false;
				mouse_on_help = false;
				repaint();
				switch(buttonPressed(e)){
				case 1:
					try {
						Game.setState(GameState.ONEP);
						Game.switchPane(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					setVisible(false);
					break;
				case 2:
					try {
						Game.setState(GameState.TWOP);
						Game.switchPane(true);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					setVisible(false);
					break;
				case 3:
					try {
						Game.setState(GameState.HELP);
						Game.switchPane(false);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					setVisible(false);
					break;
				default:
					break;
				}
				
			}
		});
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(MENU_WIDTH,  MENU_HEIGHT);
	}
	
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(background_img, 0, 0, MENU_WIDTH, MENU_HEIGHT, null);
		if(mouse_on_button1){
			g.drawImage(button1_hover, BUTTON_INIT_X, ONEP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(button1_normal, BUTTON_INIT_X, ONEP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		
		if(mouse_on_button2){
			g.drawImage(button2_hover, BUTTON_INIT_X, TWOP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(button2_normal, BUTTON_INIT_X, TWOP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		if(mouse_on_help){
			g.drawImage(button2_hover, BUTTON_INIT_X, HELP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(button2_normal, BUTTON_INIT_X, HELP_INIT_Y, BUTTON_WIDTH, BUTTON_HEIGHT, null);
		}
		

	}
}