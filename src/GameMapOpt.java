import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class GameMapOpt extends JPanel{
	
	//file paths
	public static final String map_files = "map_files.txt";
	public static final String background_img = "Menu/map_select.png";
	public static final String logo_img = "GAWESOME.jpg";
	//Button images
	public static final String l_img = "Buttons/LEFT_ARROW.png";
	public static final String r_img = "Buttons/RIGHT_ARROW.png";
	public static final String l_h_img = "Buttons/LEFT_ARROW_HOVER.png";
	public static final String r_h_img = "Buttons/RIGHT_ARROW_HOVER.png";
	public static final String s_img = "Buttons/START.png";
	public static final String q_img = "Buttons/QUIT.png";
	public static final String s_h_img = "Buttons/START_HOVER.png";
	public static final String q_h_img = "Buttons/QUIT_HOVER.png";
	
	
	//panel constants
	public static final int MAPOPT_WIDTH = GameMenu.MENU_WIDTH;
	public static final int MAPOPT_HEIGHT = GameMenu.MENU_HEIGHT;
	
	//map display constants
	public static final int MAP_SAMPLE_WIDTH = MAPOPT_WIDTH / 2;
	public static final int MAP_SAMPLE_HEIGHT = MAPOPT_HEIGHT / 2;
	
	public static final int MAP_SAMPLE_X = (MAPOPT_WIDTH - MAP_SAMPLE_WIDTH) / 2;
	public static final int MAP_SAMPLE_Y = (MAPOPT_HEIGHT - MAP_SAMPLE_HEIGHT) / 2;
	
	//button constants
	private static final int SPACE = 20;
	
	public static final int ARROW_BUTTON_WIDTH = 40;
	public static final int ARROW_BUTTON_HEIGHT = 40;
	
	public static final int LEFT_BUTTON_X = MAP_SAMPLE_X - ARROW_BUTTON_WIDTH - SPACE;
	public static final int RIGHT_BUTTON_X = MAP_SAMPLE_X + MAP_SAMPLE_WIDTH + SPACE; 
	public static final int ARROW_BUTTON_Y = (MAPOPT_HEIGHT - ARROW_BUTTON_HEIGHT) / 2;
	
	public static final int OPT_BUTTON_WIDTH = 80;
	public static final int OPT_BUTTON_HEIGHT = 40;
	
	public static final int START_BUTTON_X = MAPOPT_WIDTH-2*SPACE-2*OPT_BUTTON_WIDTH;
	public static final int QUIT_BUTTON_X = MAPOPT_WIDTH-SPACE-OPT_BUTTON_WIDTH;
	public static final int OPT_BUTTON_Y = MAPOPT_HEIGHT- SPACE-OPT_BUTTON_HEIGHT;

	//data structures
	private ArrayList<String> maps;      
	//a resizable array to store the map files
	private ArrayList<BufferedImage> maps_preview;
	//a resizable array to store the map image previews
	private int iterator;
	
	private BufferedReader br;
	private BufferedImage logo;
	private BufferedImage bg_img;
	private BufferedImage l;
	private BufferedImage r;
	private BufferedImage l_h;
	private BufferedImage r_h;
	private BufferedImage s;
	private BufferedImage q;
	private BufferedImage s_h;
	private BufferedImage q_h;
	
	private int button_pressed;     //to keep track of which button is pressed
	private boolean draw_logo;
	
	public GameMapOpt(){
		button_pressed = -1;
		draw_logo = false;
		iterator = 0;
		maps = new ArrayList<String>();
		maps_preview = new ArrayList<BufferedImage>();
		try{
			//read file
			br = new BufferedReader(new FileReader(map_files));
			String line = br.readLine();
			while(line != null){
				String[] components = line.split(",");
				//add the map path name
				maps.add(components[0].trim());
				String preview = components[1].trim();
				//add the image preview
				maps_preview.add(ImageIO.read(new File(preview)));
				line = br.readLine();
			}
			
			//read background image
			if(bg_img == null){
				bg_img = ImageIO.read(new File(background_img));
			}
			
			//read button images
			if(l == null){
				l = ImageIO.read(new File(l_img));
			}
			if(r == null){
				r = ImageIO.read(new File(r_img));
			}
			if(l_h == null){
				l_h = ImageIO.read(new File(l_h_img));
			}
			if(r_h == null){
				r_h = ImageIO.read(new File(r_h_img));
			}if(s == null){
				s = ImageIO.read(new File(s_img));
			}
			if(q == null){
				q = ImageIO.read(new File(q_img));
			}if(s_h == null){
				s_h = ImageIO.read(new File(s_h_img));
			}
			if(q_h == null){
				q_h = ImageIO.read(new File(q_h_img));
			}
			if(logo == null){
				logo = ImageIO.read(new File(logo_img));
			}
		} catch(IOException e){
			System.out.println("Internal Error: " + e.getMessage());
		}
		
		//add mouse listener
		addMouseListener(new MouseAdapter(){
			private int buttonPressed(MouseEvent e){
				
				int m_x = e.getX();
				int m_y = e.getY();
				
				if (m_y > ARROW_BUTTON_Y
						&& m_y < (ARROW_BUTTON_Y + ARROW_BUTTON_HEIGHT)){
						//a button is pressed
					if(m_x > LEFT_BUTTON_X
						   && m_x < (LEFT_BUTTON_X + ARROW_BUTTON_WIDTH)){
						return 1;
					}
					else if(m_x > RIGHT_BUTTON_X
							&& m_x < (RIGHT_BUTTON_X + ARROW_BUTTON_WIDTH)){
						return 2;
						
					}
				}
				else if(m_y > OPT_BUTTON_Y
						&& m_y < (OPT_BUTTON_Y + OPT_BUTTON_HEIGHT)){
						//a button is pressed
					if(m_x > START_BUTTON_X
						   && m_x < (START_BUTTON_X + OPT_BUTTON_WIDTH)){
						return 3; //START
					}
					else if(m_x > QUIT_BUTTON_X
							&& m_x < (QUIT_BUTTON_X + OPT_BUTTON_WIDTH)){
						return 0; //EXIT
						
					}
				}
					
				
		
				return -1;
			}
			
			public void mousePressed(MouseEvent e) {
				button_pressed = buttonPressed(e);
				switch(buttonPressed(e)){
				case 1:
					if(iterator > 0){
						iterator --;
					}
					repaint();
					break;
				case 2:
					if(iterator < maps_preview.size() - 1){
						iterator ++;
					}
					repaint();
					break;
				default:
					repaint();
					break;
				}
				
				
			}
			/**
			 * To allow the player to "regret"
			 */
			public void mouseReleased(MouseEvent e) {
				button_pressed = -1;
				switch(buttonPressed(e)){
				case 0:
					Game.setState(GameState.MENU);
					repaint();
				
					try {
						Game.switchPane(false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setVisible(false);
					break;
				case 3:
					Game.setMap_file(maps.get(iterator));
					draw_logo = true;
					//force repaint to draw logo before game starts
					Graphics g = getGraphics();
					if(g != null) drawLogo(g);
					repaint();
					
					try {
						Game.switchPane(false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					setVisible(false);
					break;
				default:
					repaint();
					break;
				}
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
			g.drawImage(bg_img, 0, 0, MAPOPT_WIDTH, MAPOPT_HEIGHT, null);
			g.drawImage(maps_preview.get(iterator), 
						MAP_SAMPLE_X,
						MAP_SAMPLE_Y,
						MAP_SAMPLE_WIDTH,
						MAP_SAMPLE_HEIGHT,
						null);
			drawStart(g);
			drawQuit(g);
			drawLeft(g);
			drawRight(g);
			
	}
	
	/**
	 * a series of helper methods that help draw the buttons
	 * @param g
	 */
	private void drawStart(Graphics g){
		if(button_pressed == 3){
			g.drawImage(s_h, START_BUTTON_X, OPT_BUTTON_Y, 
							OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(s, START_BUTTON_X, OPT_BUTTON_Y, 
					OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, null);
		}
	}
	private void drawQuit(Graphics g){
		if(button_pressed == 0){
			g.drawImage(q_h, QUIT_BUTTON_X, OPT_BUTTON_Y, 
							OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(q, QUIT_BUTTON_X, OPT_BUTTON_Y, 
					OPT_BUTTON_WIDTH, OPT_BUTTON_HEIGHT, null);
		}
	}
	private void drawLeft(Graphics g){
		if(button_pressed == 1){
			g.drawImage(l_h, LEFT_BUTTON_X, ARROW_BUTTON_Y, 
							ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, null);
		}
		else{
			g.drawImage(l, LEFT_BUTTON_X, ARROW_BUTTON_Y, 
					ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, null);
		}
	}
	private void drawRight(Graphics g){
		if(button_pressed == 2){
			g.drawImage(r_h, RIGHT_BUTTON_X, ARROW_BUTTON_Y, 
							ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, null);
			
		}
		else{
			g.drawImage(r, RIGHT_BUTTON_X, ARROW_BUTTON_Y, 
					ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, null);
		}
	}
	public void drawLogo(Graphics g){
		if(draw_logo){
			

			g.drawImage(logo, 0, 0, MAPOPT_WIDTH, MAPOPT_HEIGHT, null);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}	
	}
	
	
}
