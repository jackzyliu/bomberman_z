import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * This class contains the game logic for one-player mode
 * @author Zheyuan Liu
 *
 */
@SuppressWarnings("serial")
public class GameCourt_OnePlayer extends JPanel {
	
	 public static final String CLEAR_img = "resource/image/Misc/clear.png";
	 public static final String LOSE_img = "resource/image/Misc/lose.png";
	 public static final String OK_img = "resource/image/Buttons/ok.png";
	 
	 public static final int OK_X = (Map.COURT_WIDTH - Map.TILE_SIZE)/2;
	 public static final int OK_Y = 300;
	 public static final int OK_WIDTH = Map.TILE_SIZE;
	 public static final int OK_HEIGHT = Map.TILE_SIZE;
	// Update interval for timer in milliseconds 
	public static final int INTERVAL = 10; 
	
	private boolean playing;
	private GameResult result;
	private GameDashboard game_dashboard;
	private Player player1;         
	private ArrayList<Creep_AI> creeps;

	
	private HashSet<Integer> keypressed = new HashSet<Integer>();
	//to keep track the keys pressed
	private Map map;
	
	private BufferedImage clear;
	private BufferedImage lose;
	private BufferedImage ok;
	
	public GameCourt_OnePlayer
		(final GameDashboard game_dashboard, String map_file) throws IOException{
		//super(status, game_timer);
		Timer timer = new Timer(INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tick();
			}
		});
		
		timer.start(); // MAKE SURE TO START THE TIMER!
		setFocusable(true);
		
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
				if(keypressed.contains(KeyEvent.VK_SPACE) ){
					
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
		
			
			public void keyPressed(KeyEvent e){
				keypressed.add(e.getKeyCode());
				keyActions_player1();
			}
			public void keyReleased(KeyEvent e){
				if (!keypressed.isEmpty()){
					keypressed.remove(e.getKeyCode());
				}
				keyActions_player1();
			}
		});
		
		
		try{
			clear = ImageIO.read(new File(CLEAR_img));
			lose = ImageIO.read(new File(LOSE_img));
			ok = ImageIO.read(new File(OK_img));

		} catch (IOException e){
			e.printStackTrace();
		}
		
		this.game_dashboard = game_dashboard;
		this.map = new Map(map_file);
		this.creeps = new ArrayList<Creep_AI>();
		this.result = GameResult.NONE;
	}
	/** (Re-)set the state of the game to its initial state.
	 */
	public void reset() {

		player1 = new Player(map, 1);
		for(int i = 1; i < map.supportNumOfPlayer(); i++){
			creeps.add(new  Creep_AI(map, player1, i+1));
		}
		creeps.trimToSize();
		playing = true;
		game_dashboard.setCreepNum(creeps.size());
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
			
			for(int i = 0; i < creeps.size(); i++){
				creeps.get(i).interactWithUnwalkables();
				creeps.get(i).stateControl();
				if(creeps.get(i).isDead()){
					creeps.remove(i);
					i--;
				}
				else{
					creeps.get(i).move();
				}
				
			}
			game_dashboard.setPlayerScore(2,  
					map.supportNumOfPlayer() - creeps.size() - 1);
			game_dashboard.setCreepNum(creeps.size());
			
			map.trackExplosions();
			/*
			map.interactWithUnwalkables(player1);
*/
			playing = !game_dashboard.isOver() && 
					  !player1.isDead() &&
					  !creeps.isEmpty() &&
					  result != GameResult.QUIT;
			if(!game_dashboard.visible){
				result = GameResult.QUIT;
				setVisible(false);
			}

			repaint();
			
		} 
		else{
			game_dashboard.stopCounting();
			
			if(!player1.isDead() && creeps.isEmpty()){
				result = GameResult.CLEAR;
			}
			else{
				result = GameResult.LOSE;
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
		player1.paintBubbles(g);
		map.drawAreaExplosionAndItems(g);
		

		//draw the person in the front first
		//TODO make this a separate method while developing multi-layer mode
		drawQueue(g);
		
		//draw result
		if(result == GameResult.CLEAR){
			g.drawImage(clear, 0, (Map.COURT_HEIGHT - 100)/2, 
					Map.COURT_WIDTH, 100, null);
			g.drawImage(ok, OK_X, OK_Y, OK_WIDTH, OK_HEIGHT, null);
		}
		else if(result == GameResult.LOSE){
			g.drawImage(lose, 0, (Map.COURT_HEIGHT - 100)/2, 
					Map.COURT_WIDTH, 100, null);
			g.drawImage(ok, OK_X, OK_Y, OK_WIDTH, OK_HEIGHT, null);
		}
		
	}
	
	/**
	 * This method makes a queue based creeps pos_x and draws the creeps and 
	 * player
	 * @param g
	 */
	private void drawQueue(Graphics g){
		//do a bubble sort so that the creeps list is in descending order in 
		//terms of their pos_y
		if(creeps.isEmpty() ){
			player1.draw(g);
		}
		else{
			boolean cont = true;
			while (cont){
				cont = false;
				for(int i = 0; i < creeps.size()-1 ; i++){
					if(creeps.get(i).pos_y > creeps.get(i+1).pos_y){
						Creep_AI tmp = creeps.get(i);
						creeps.remove(i);
						creeps.add(i+1, tmp);
						cont = true;
					}
				}
			}
			creeps.trimToSize();
			if(player1.pos_y <= creeps.get(0).pos_y){
				player1.draw(g);
				for(int i = 0; i < creeps.size() ; i++){
					creeps.get(i).draw(g);
				}
			}
			else if (player1.pos_y >= creeps.get(creeps.size() -1).pos_y){
				for(int i = 0; i < creeps.size() ; i++){
					creeps.get(i).draw(g);
				}
				player1.draw(g);
			}
			else{
				creeps.get(0).draw(g);
				for(int i = 0; i < creeps.size()-1 ; i++){
					if(player1.pos_y >= creeps.get(i).pos_y &&
					   player1.pos_y <= creeps.get(i+1).pos_y){
						player1.draw(g);
						creeps.get(i+1).draw(g);
					}
					else{
						creeps.get(i+1).draw(g);
					}
				}
			}
		}
	
	}

	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(Map.COURT_WIDTH, Map.COURT_HEIGHT);
	}
	

}


