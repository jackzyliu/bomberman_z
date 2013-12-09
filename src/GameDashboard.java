import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class GameDashboard extends JPanel{
	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 1000; 
	public static final int WIDTH = 200;
	public static final int TIMER_HEIGHT = 100;
	
	public static final int SCOREBOARD_HEIGHT = 80;
	public static final int SCOREBOARD1_Y = TIMER_HEIGHT ;
	public static final int SCOREBOARD2_Y = TIMER_HEIGHT + SCOREBOARD_HEIGHT;
	
	public static final int DECORATION_HEIGHT = 150;
	public static final int DECORATION_Y = SCOREBOARD2_Y + SCOREBOARD_HEIGHT;
	
	public static final int QUIT_WIDTH = GameMapOpt.OPT_BUTTON_WIDTH;
	public static final int QUIT_HEIGHT = GameMapOpt.OPT_BUTTON_HEIGHT;
	
	public static final int QUIT_X = (WIDTH - QUIT_WIDTH) /2;
	public static final int QUIT_Y = GameMapOpt.OPT_BUTTON_Y; 
	public static final int HEIGHT = Map.HEIGHT;
	
	//image files
	public static final String colon_img = "Numbers/colon.png";
	public static final String bg_img = "Menu/timer_background.png";
	public static final String frame_img = "Menu/timer_frame.png";
	public static final String scoreboard1_img = "Menu/player1_score_board.png";
	public static final String scoreboard2_img = "Menu/player2_score_board.png";
	public static final String creepboard_img = "Menu/creep_scoreboard.png";
	public static final String decoration_img = "Misc/bomberman_decoration.png";
	public static final String quit_img = "Buttons/QUIT.png";
	public static final String quit_hover_img = "Buttons/QUIT_HOVER.png";
	
	//image constants
	public static final int NUM_WIDTH = 30;
	public static final int NUM_HEIGHT = 40;
	
	public static final int MIN_X = (WIDTH - 4*NUM_WIDTH) /2;
	public static final int COLON_X = MIN_X + NUM_WIDTH;
	public static final int SEC1_X = MIN_X + 2*NUM_WIDTH;
	public static final int SEC2_X = MIN_X + 3*NUM_WIDTH;
	public static final int NUM_Y = 40;
	
	private GameState mode;
	private int player1_score;
	private int player2_score;
	private int creep_num;
	
	public boolean visible;
	private int time_length;   //in seconds
	private Timer timer;
	
	private BufferedImage[] nums;
	private BufferedImage bg;
	private BufferedImage frame;
	private BufferedImage scoreboard1;
	private BufferedImage scoreboard2;
	private BufferedImage decoration;
	private BufferedImage quit;
	private BufferedImage quit_hover;
	
	private boolean quit_pressed;
	
	public GameDashboard(GameState mode){
		//TODO PRINT BASED ON MODE
		
		setBackground(Color.CYAN);	
		this.mode = mode;
		this.time_length = 3*60;   //3 minutes
		this.quit_pressed = false;
		this.player1_score = 0;
		this.player2_score = 0;
		this.visible = true;
		
		try{
			bg = ImageIO.read(new File(bg_img));
			frame = ImageIO.read(new File(frame_img));
			scoreboard1 = ImageIO.read(new File(scoreboard1_img));
			if(mode == GameState.ONEP){
				scoreboard2 = ImageIO.read(new File(creepboard_img));
			}
			else{
				scoreboard2 = ImageIO.read(new File(scoreboard2_img));
			}
			decoration = ImageIO.read(new File(decoration_img));
			quit = ImageIO.read(new File(quit_img));
			quit_hover = ImageIO.read(new File(quit_hover_img));
			
			nums = new BufferedImage[11];
			for(int i = 0; i < nums.length; i++){
				if(i == 10){
					nums[i] = ImageIO.read(new File(colon_img));
				}
				else{
					String file_name = "Numbers/num_" +
										Integer.toString(i) +
										".png";
											
					nums[i] = ImageIO.read(new File(file_name));
				}
			
			}
		} catch (IOException e){
			System.out.println("Internal Error: " + e.getMessage());
		}
		
		
		timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				time_length --;
				repaint();
			}
		});
		
		
		timer.start(); 
		
		addMouseListener(new MouseAdapter(){
			
			public void mousePressed(MouseEvent e){
				
				int m_x = e.getX();
				int m_y = e.getY();
				
				if(m_x > QUIT_X && m_x < QUIT_X + QUIT_WIDTH &&
				   m_y > QUIT_Y && m_y < QUIT_Y + QUIT_HEIGHT){
					quit_pressed = true;
					repaint();
				}
			}
			
			public void mouseReleased(MouseEvent e){
				quit_pressed = false;
				int m_x = e.getX();
				int m_y = e.getY();
				
				if(m_x > QUIT_X && m_x < QUIT_X + QUIT_WIDTH &&
				   m_y > QUIT_Y && m_y < QUIT_Y + QUIT_HEIGHT){
					Game.setState(GameState.MENU);
					repaint();
					visible = false;
					setVisible(false);
					try {
						Game.switchPane(false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}


	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//DISPLAY TIMER
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawImage(bg, 0, 0, WIDTH, 100, null);
		g.drawImage(frame, 0, 0, WIDTH, 100, null);
		g.drawString("Time:", 10, 30);
		g.drawImage(nums[setMin()], MIN_X, NUM_Y, NUM_WIDTH, NUM_HEIGHT, null);
		g.drawImage(nums[10], COLON_X, NUM_Y, NUM_WIDTH, NUM_HEIGHT, null);
		g.drawImage(nums[setSec_First()], SEC1_X, NUM_Y, NUM_WIDTH, NUM_HEIGHT, null);
		g.drawImage(nums[setSec_Second()], SEC2_X, NUM_Y, NUM_WIDTH, NUM_HEIGHT, null);
		
		//DISPLAY SCORE BOARD
		g.drawImage(scoreboard1, 0, SCOREBOARD1_Y, WIDTH, SCOREBOARD_HEIGHT, null);
		g.drawImage(scoreboard2, 0, SCOREBOARD2_Y, WIDTH, SCOREBOARD_HEIGHT, null);
		
		g.drawString(Integer.toString(player1_score), WIDTH*3 /4, SCOREBOARD1_Y + SCOREBOARD_HEIGHT * 3 / 4);
		if(mode == GameState.ONEP){
			g.drawString(Integer.toString(creep_num), WIDTH*3 /4, SCOREBOARD2_Y + SCOREBOARD_HEIGHT * 3 / 4);
		}
		else{
			g.drawString(Integer.toString(player2_score), WIDTH*3 /4, SCOREBOARD2_Y + SCOREBOARD_HEIGHT * 3 / 4);
		}
		
		
		//DISPLAY DECORATION
		g.drawImage(decoration, 0, DECORATION_Y, WIDTH, DECORATION_HEIGHT, null);
		//DISPLAY QUIT BUTTON
		if(quit_pressed){
			g.drawImage(quit_hover, QUIT_X, QUIT_Y, QUIT_WIDTH, QUIT_HEIGHT, null);
		}
		else{
			g.drawImage(quit, QUIT_X, QUIT_Y, QUIT_WIDTH, QUIT_HEIGHT, null);
		}
		
	}
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(WIDTH, HEIGHT);
	}
	
	public boolean isOver(){
		if(time_length <= 0){
			this.timer.stop();
			return true;
		}
		else return false;
		
	}
	
	public void stopCounting(){
		this.timer.stop();
	}
	
	private int setMin(){
		int min = time_length / 60;
		return min;
	} 
	
	private int setSec_First(){
		int sec = time_length % 60;
		if(sec < 10) return 0;
		else return sec / 10;
	}
	
	private int setSec_Second(){
		int sec = time_length % 60;
		return sec % 10;
	}
	
	/**
	 * Sets the player score based on the input player code and its DEATH TIMES
	 * @param player_code
	 * @param score
	 */
	public void setPlayerScore(int player_code, int death_times){
		if(player_code == 1) this.player2_score = death_times;
		else this.player1_score = death_times;
	}
	
	
	public int getPlayerScore(int player_code){
		if(player_code == 1) return this.player1_score;
		else if(player_code == 2) return this.player2_score;
		else return 0;
	}
	
	public void setCreepNum(int num){
		creep_num = num;
	}
	
}
