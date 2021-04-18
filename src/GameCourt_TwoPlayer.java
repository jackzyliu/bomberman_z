import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;






import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * This class contains the game logic for the two-player mode
 * @author Zheyuan Liu
 *
 */
@SuppressWarnings("serial")
public class GameCourt_TwoPlayer extends JPanel{
	
	public static final String player1_won_img = "resource/image/Misc/player1_won.png";
	public static final String player2_won_img = "resource/image/Misc/player2_won.png";
	public static final String draw_img = "resource/image/Misc/draw.png";
	public static final String OK_img = "resource/image/Buttons/ok.png";
	
	public static final int OK_X = (Map.COURT_WIDTH - Map.TILE_SIZE)/2;
	public static final int OK_Y = 300;
	public static final int OK_WIDTH = Map.TILE_SIZE;
	public static final int OK_HEIGHT = Map.TILE_SIZE;
	
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 10; 
	
	
	/**
	 * Status
	 */
	private boolean playing;
	private GameResult result;


	/**
	 * Display
	 */
	private GameDashboard game_dashboard;
	
	/**
	 * Element
	 */
	private Player player1;         
	private Player player2;
	private HashSet<Integer> keypressed = new HashSet<Integer>();
	//to keep track the keys pressed
	private Map map;
	
	/**
	 * Images
	 */
	
	private BufferedImage player1_won;
	private BufferedImage player2_won;
	private BufferedImage draw;
	private BufferedImage ok;

	
	/**
	 * Constructor
	 * @param game_dashboard
	 * @param map_file
	 * @throws IOException
	 */
	public GameCourt_TwoPlayer
		(final GameDashboard game_dashboard, String map_file) throws IOException{
		//super(status, game_timer);
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
		
		timer.start(); // MAKE SURE TO START THE TIMER!
		setFocusable(true);
		
		try{
			player1_won = ImageIO.read(new File(player1_won_img));
			player2_won = ImageIO.read(new File(player2_won_img));
			draw = ImageIO.read(new File(draw_img));
			ok = ImageIO.read(new File(OK_img));

		} catch (IOException e){
			e.printStackTrace();
		}
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				//if game is over
				if(result != GameResult.NONE){
					int m_x = e.getX();
					int m_y = e.getY();
					
					//if mouse pressed the ok button
					if (m_x > OK_X && m_x < OK_X + OK_WIDTH &&
					    m_y > OK_Y && m_y < OK_Y + OK_HEIGHT){
						game_dashboard.setVisible(false);
						setVisible(false);
						Game.setState(GameState.MENU);
						try {
							result = GameResult.QUIT;
							Game.switchPane(false);
						} catch (IOException e1) {

							e1.printStackTrace();
						}
					}
				}
				
			}
			
		});
		
		addKeyListener(new KeyAdapter(){
			
			/**Helper:
			 * perform a series of actions related to a combination of keys
			 *  pressed
			 */
			private void keyActions_player1(){
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_ENTER) ){
					
					player1.dropBubble();
				}
				//if only ONE of the left and right arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_LEFT) 
						&& keypressed.contains(KeyEvent.VK_RIGHT)){
					player1.resetHor();
				}
				else {
					if (keypressed.contains(KeyEvent.VK_LEFT)){
						player1.setDir(Direction.LEFT);
					}
					else if (keypressed.contains(KeyEvent.VK_RIGHT)){
						player1.setDir(Direction.RIGHT);
					}
					else{
						player1.resetHor();
					}
				}
				//if only ONE of the up and down arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_UP)
						&& keypressed.contains(KeyEvent.VK_DOWN)){
					player1.resetVer();
				}	
				else{
					if (keypressed.contains(KeyEvent.VK_UP)){
						player1.setDir(Direction.UP);
					}
					else if (keypressed.contains(KeyEvent.VK_DOWN)){
						player1.setDir(Direction.DOWN);
					}
					else{
						player1.resetVer();
					}
				}
				player1.collectItems();
				player1.interactWithLastBubble();
				player1.interactWithUnwalkables();

				
			}
			/**Helper:
			 * perform a series of actions related to a combination of keys
			 *  pressed
			 */
			private void keyActions_player2(){
				
				//if space is pressed && the character walked out of the 
				//previous bubble.
				if(keypressed.contains(KeyEvent.VK_Z) ){
					
					player2.dropBubble();
				}
				//if only ONE of the left and right arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_F) 
						&& keypressed.contains(KeyEvent.VK_H)){
					player2.resetHor();
				}
				else {
					if (keypressed.contains(KeyEvent.VK_F)){
						player2.setDir(Direction.LEFT);
					}
					else if (keypressed.contains(KeyEvent.VK_H)){
						player2.setDir(Direction.RIGHT);
					}
					else{
						player2.resetHor();
					}
				}
				//if only ONE of the up and down arrow keys are pressed 
				if(keypressed.contains(KeyEvent.VK_T)
						&& keypressed.contains(KeyEvent.VK_G)){
					player2.resetVer();
				}	
				else{
					if (keypressed.contains(KeyEvent.VK_T)){
						player2.setDir(Direction.UP);
					}
					else if (keypressed.contains(KeyEvent.VK_G)){
						player2.setDir(Direction.DOWN);
					}
					else{
						player2.resetVer();
					}
				}
				player2.collectItems();
				player2.interactWithLastBubble();
				player2.interactWithUnwalkables();
			}
			
			
			
			public void keyPressed(KeyEvent e){
				if(result == GameResult.NONE){
					keypressed.add(e.getKeyCode());
					keyActions_player1();
					keyActions_player2();
				}
				
			}
			public void keyReleased(KeyEvent e){
				if(result == GameResult.NONE){
					if (!keypressed.isEmpty()){
						keypressed.remove(e.getKeyCode());
					}
					keyActions_player1();
					keyActions_player2();
				}
				
			}
		});
		
		
		//initialize
		this.result = GameResult.NONE;
		this.game_dashboard = game_dashboard;
		this.map = new Map(map_file);
	}
	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {

		player1 = new Player(map, 1);
		player2 = new Player(map, 2);
		playing = true;
		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

    /**
     * This method is called every time the timer defined
     * in the constructor triggers.
     */
	void tick(){
		if (playing) {
			player1.interactWithLastBubble();
			player1.move();
			player1.trackBubbles();
			player1.stateControl();
			
			player2.interactWithLastBubble();
			player2.move();
			player2.trackBubbles();
			player2.stateControl();
			
			map.trackExplosions();

			game_dashboard.setPlayerScore(1, player1.getDeathTimes());
			game_dashboard.setPlayerScore(2, player2.getDeathTimes());
			
			playing = !game_dashboard.isOver() && result != GameResult.QUIT;
			
			
			if(!game_dashboard.visible){
				result = GameResult.QUIT;
				setVisible(false);
			}
			repaint();
			
			
		} 
		else{
			game_dashboard.stopCounting();
			int player1_score = game_dashboard.getPlayerScore(1);
			int player2_score = game_dashboard.getPlayerScore(2);
			if(player1_score == player2_score){
				result = GameResult.DRAW;
			}
			else if (player1_score >= player2_score){
				result = GameResult.PLAYER1;
			}
			else{
				result = GameResult.PLAYER2;
			}
			if(this.isVisible()){
				if(!game_dashboard.visible){
					result = GameResult.QUIT;
					setVisible(false);
				}
				repaint();
			}
			

		}
	}

	@Override 
	public void paintComponent(Graphics g){

		super.paintComponent(g);
		map.paint(g);
		map.drawAreaExplosionAndItems(g);
		player1.paintBubbles(g);
		player2.paintBubbles(g);
		
		
		//draw the person in the front first
		//TODO make this a separate method while developing multi-layer mode
		
		if(player1.pos_y <= player2.pos_y){
			player1.draw(g);
			player2.draw(g);
		}
		else{
			player2.draw(g);
			player1.draw(g);		
		}
		
		
		//draw result
		if(result == GameResult.PLAYER1){
			g.drawImage(player1_won, 0, (Map.COURT_HEIGHT - 100)/2, 
					Map.COURT_WIDTH, 100, null);
			g.drawImage(ok, OK_X, OK_Y, OK_WIDTH, OK_HEIGHT, null);
		}
		else if(result == GameResult.PLAYER2){
			g.drawImage(player2_won, 0, (Map.COURT_HEIGHT - 100)/2, 
					Map.COURT_WIDTH, 100, null);
			g.drawImage(ok, OK_X, OK_Y, OK_WIDTH, OK_HEIGHT, null);
		}
		else if(result == GameResult.DRAW){
			g.drawImage(draw, 0, (Map.COURT_HEIGHT - 100)/2, 
					Map.COURT_WIDTH, 100, null);
			g.drawImage(ok, OK_X, OK_Y, OK_WIDTH, OK_HEIGHT, null);
		}
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(Map.COURT_WIDTH, Map.COURT_HEIGHT);
	}


}
